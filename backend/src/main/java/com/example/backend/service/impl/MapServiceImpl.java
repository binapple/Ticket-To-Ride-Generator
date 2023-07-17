package com.example.backend.service.impl;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

import com.example.backend.endpoint.dto.CityDto;
import com.example.backend.endpoint.dto.CreateMapDto;
import com.example.backend.endpoint.dto.MapPointDto;
import com.example.backend.endpoint.mapper.CityMapper;
import com.example.backend.endpoint.mapper.MapMapper;
import com.example.backend.endpoint.mapper.MapPointMapper;
import com.example.backend.entitiy.City;
import com.example.backend.entitiy.Map;
import com.example.backend.entitiy.MapPoint;
import com.example.backend.repository.CityRepository;
import com.example.backend.repository.MapRepository;
import com.example.backend.service.MapService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MapServiceImpl implements MapService {


  private final MapMapper mapMapper;
  private final MapRepository mapRepository;
  private final CityMapper cityMapper;
  private final CityRepository cityRepository;
  private final MapPointMapper mapPointMapper;

  @Autowired
  public MapServiceImpl(MapMapper mapMapper, MapRepository mapRepository, CityMapper cityMapper, CityRepository cityRepository, MapPointMapper mapPointMapper) {
    this.mapMapper = mapMapper;
    this.mapRepository = mapRepository;
    this.cityMapper = cityMapper;
    this.cityRepository = cityRepository;
    this.mapPointMapper = mapPointMapper;
  }

  @Override
  public CreateMapDto create(CreateMapDto createMapDto) {

    Map map = mapMapper.createMapDtoToMap(createMapDto);

    map = mapRepository.save(map);

    return mapMapper.mapToCreateMapDto(map);
  }

  @Override
  public List<CityDto> getInitialCities(Long id) {

    return getCityDtos(id, "city");

  }

  @Override
  public List<CityDto> getCities(Long id) {
    return cityMapper.cityListToCityDtoList(cityRepository.findCitiesByMapsId(id));
  }

  @Override
  public List<CityDto> getTowns(Long id) {
    return getCityDtos(id, "town");

  }

  private List<CityDto> getCityDtos(Long id, String place) {
    Map map = mapRepository.getReferenceById(id);
    String boundary = map.getSouthWestBoundary().y + "," +
        map.getSouthWestBoundary().x + "," +
        map.getNorthEastBoundary().y + "," +
        map.getNorthEastBoundary().x;

    String urlUnencoded = "[out:json]; node [\"place\"=\""+ place +"\"] (" + boundary + "); out body;";

    String urlEncoded = URLEncoder.encode(urlUnencoded);

    try {
      URL url = new URL("https://overpass-api.de/api/interpreter?data="+urlEncoded);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.connect();

      int responseCode = connection.getResponseCode();

      if(responseCode != 200){
        throw new RuntimeException("HttpResponseCode" + responseCode);
      }
      else {
        StringBuilder responseString = new StringBuilder();
        Scanner scans = new Scanner(url.openStream());

        while (scans.hasNext())
        {
          responseString.append(scans.nextLine());
        }

        scans.close();

        //System.out.println(responseString);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(String.valueOf(responseString));
        JsonNode cityElements = jsonNode.get("elements");

        cityElements.forEach(
            node-> {
              City newCity = new City();

              Point2D.Float location = new Point2D.Float();
              location.y = node.get("lat").floatValue();
              location.x = node.get("lon").floatValue();
              newCity.setLocation(location);

              JsonNode tags = node.get("tags");

              //This game needs a name for a city to work -> no name = not usable
              boolean nameAvailable = true;
              JsonNode name = tags.get("name");
              if(name == null)
              {
                nameAvailable = false;
              }
              else {
                newCity.setName(name.asText());
              }



              JsonNode population = tags.get("population");
              if(population == null) {
                newCity.setPopulation(0L);
              }
              else {
                newCity.setPopulation(population.asLong());
              }


              if(nameAvailable) {
                List<City> alreadySavedCity = cityRepository.findCityByNameAndPopulation(newCity.getName(), newCity.getPopulation());
                if (alreadySavedCity.size() == 0) {
                  map.addCity(newCity);
                  cityRepository.save(newCity);

                } else {
                  map.addCity(alreadySavedCity.get(0));
                  cityRepository.save(alreadySavedCity.get(0));
                }
              }
            }
        );

        mapRepository.save(map);


      }

    } catch (MalformedURLException e) {
      throw new RuntimeException("MalformedURL "+e.getMessage());
    } catch (IOException e) {
      throw new RuntimeException("IOException " + e.getMessage());
    }

    return cityMapper.cityListToCityDtoList(cityRepository.findCitiesByMapsId(id));
  }

  @Override
  public List<MapPointDto> createMapPoints(Long id, List<CityDto> cityDtos) {

    Map map =  mapRepository.getReferenceById(id);

    Graph<MapPoint, DefaultWeightedEdge> graph
        = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

    for (CityDto c:
         cityDtos) {
      MapPoint newPoint = new MapPoint();

      newPoint.setLocation(c.getLocation());
      newPoint.setName(c.getName());
      newPoint.setId(c.getId());
      graph.addVertex(newPoint);
    }

    CompleteGraphGenerator<MapPoint, DefaultWeightedEdge> completeGraphGenerator
        = new CompleteGraphGenerator<>();



    completeGraphGenerator.generateGraph(graph,null);

    Set<DefaultWeightedEdge> edges = graph.edgeSet();

    for (DefaultWeightedEdge e: edges
         ) {
      MapPoint source = graph.getEdgeSource(e);
      MapPoint target = graph.getEdgeTarget(e);
      double weight = source.getLocation().distance(target.getLocation());
      graph.setEdgeWeight(e,weight);
    }

    reduceEdges(graph,map);

      for (MapPoint p: graph.vertexSet()
           ) {
          List<MapPoint> neighbors = new ArrayList<>();
          for (DefaultWeightedEdge e: graph.edgesOf(p)
               ) {
              MapPoint source = graph.getEdgeSource(e);
              MapPoint target = graph.getEdgeTarget(e);
              if(!source.equals(p))
              {
                  neighbors.add(source);
              } else
              {
                  neighbors.add(target);
              }
          }
          p.setNeighbors(neighbors);
      }

      Set<MapPoint> mapPoints = graph.vertexSet();
      List<MapPoint> toReturn = new ArrayList<>();
      for (MapPoint m : mapPoints
           ) {
          toReturn.add(m);
      }

 /*   JGraphXAdapter<MapPoint, DefaultWeightedEdge> graphAdapter = new JGraphXAdapter<>(graph);

    mxIGraphLayout layout = new mxFastOrganicLayout(graphAdapter);
    layout.execute(graphAdapter.getDefaultParent());

    BufferedImage image =
        mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
    File img = new File("src/test/resources/graph.png");
    try {
      img.createNewFile();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    try {
      ImageIO.write(image,"PNG", img);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
*/
    return mapPointMapper.mapPointListToMapPointDtoListCustom(toReturn);
  }

  private void reduceEdges(Graph<MapPoint, DefaultWeightedEdge> graph, Map map)
  {

     Set<DefaultWeightedEdge> edges = graph.edgeSet();

     List<DefaultWeightedEdge> edgeList = new ArrayList<>(edges);


     Comparator<DefaultWeightedEdge> comp = new Comparator<DefaultWeightedEdge>() {
       @Override
       public int compare(DefaultWeightedEdge o1, DefaultWeightedEdge o2) {
         double first = graph.getEdgeWeight(o1);
         double second = graph.getEdgeWeight(o2);
         return Double.compare(second,first);
       }
     };

     edgeList.sort(comp);

     Point2D.Float sw = map.getSouthWestBoundary();
     Point2D.Float ne = map.getNorthEastBoundary();

     float trainsize = (Math.abs(ne.x - sw.x))*26/1189;

     float eight = trainsize * 8;
     float seven = trainsize * 7;
     float six = trainsize * 6;
     float five = trainsize * 5;

     //remove connections with greater length than eight, and all except one of length eight
     float tooBigSize = eight * 2;
     while (tooBigSize >= eight) {

         //This step is used to have a DefaultWeightEdge Object of the graph that can be deleted and is needed for binary search
         Iterator<DefaultWeightedEdge> i = edgeList.iterator();
         DefaultWeightedEdge e = new DefaultWeightedEdge();
         while (i.hasNext())
         {
             DefaultWeightedEdge current = i.next();
             if(graph.getEdgeWeight(current) >= tooBigSize)
             {
                 e = current;
                 graph.setEdgeWeight(e,tooBigSize);
                 break;
             }
         }

         Graph<MapPoint, DefaultWeightedEdge> safetyCopy = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

         Graphs.addGraph(safetyCopy, graph);

         edgeList.sort(comp);
         int index = Collections.binarySearch(edgeList,e,comp);
         graph.removeEdge(e);
         edgeList.remove(e);
         if(tooBigSize == eight)
         {
             index = index-1;
         }
         List<DefaultWeightedEdge> toRemove = edgeList.subList(0, index);
         graph.removeAllEdges(toRemove);


         if(GraphTests.isConnected(graph))
         {
             edgeList = edgeList.subList(index,edgeList.size());
             tooBigSize = tooBigSize - trainsize;
         }
         else {
             Graphs.addGraph(graph,safetyCopy);
             checkOneByOne(graph, toRemove, tooBigSize);
             break;
         }


     }

     edgeList = edges.stream().toList();







  }

    private void checkOneByOne(Graph<MapPoint, DefaultWeightedEdge> graph, List<DefaultWeightedEdge> toRemove, float tooBigSize) {

        Graph<MapPoint, DefaultWeightedEdge> safetyCopy = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        Graphs.addGraph(safetyCopy, graph);

        DefaultWeightedEdge problemEdge = new DefaultWeightedEdge();

        for (DefaultWeightedEdge e: toRemove
             ) {
            graph.removeEdge(e);

            if(!GraphTests.isConnected(graph))
            {
                problemEdge = e;
                MapPoint v1 = safetyCopy.getEdgeSource(problemEdge);
                MapPoint v2 = safetyCopy.getEdgeTarget(problemEdge);

                v1.setConnectionIssue(true);
                v2.setConnectionIssue(true);
                graph.addEdge(v1, v2);
            }
        }

  }

}
