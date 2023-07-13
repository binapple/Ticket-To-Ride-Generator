package com.example.backend.service.impl;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.example.backend.endpoint.dto.CityDto;
import com.example.backend.endpoint.dto.CreateMapDto;
import com.example.backend.endpoint.mapper.CityMapper;
import com.example.backend.endpoint.mapper.MapMapper;
import com.example.backend.entitiy.City;
import com.example.backend.entitiy.Map;
import com.example.backend.entitiy.MapPoint;
import com.example.backend.repository.CityRepository;
import com.example.backend.repository.MapRepository;
import com.example.backend.service.MapService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
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

  @Autowired
  public MapServiceImpl(MapMapper mapMapper, MapRepository mapRepository, CityMapper cityMapper, CityRepository cityRepository) {
    this.mapMapper = mapMapper;
    this.mapRepository = mapRepository;
    this.cityMapper = cityMapper;
    this.cityRepository = cityRepository;
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
  public List<MapPoint> createMapPoints(Long id, List<CityDto> cityDtos) {

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

    JGraphXAdapter<MapPoint, DefaultWeightedEdge> graphAdapter = new JGraphXAdapter<>(graph);

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

    return null;
  }

  private void reduceEdges(Graph<MapPoint, DefaultWeightedEdge> graph, Map map)
  {

     Set<DefaultWeightedEdge> edges = graph.edgeSet();

     List<DefaultWeightedEdge> edgeList = edges.stream().collect(Collectors.toList());

     Comparator<DefaultWeightedEdge> comp = new Comparator<DefaultWeightedEdge>() {
       @Override
       public int compare(DefaultWeightedEdge o1, DefaultWeightedEdge o2) {
         double first = graph.getEdgeWeight(o1);
         double second = graph.getEdgeWeight(o2);
         return Double.compare(first,second);
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





     Iterator<DefaultWeightedEdge> i = edgeList.iterator();
     DefaultWeightedEdge e = new DefaultWeightedEdge();
     while (i.hasNext())
     {
       DefaultWeightedEdge current = i.next();
       if(graph.getEdgeWeight(current) > eight)
       {
         e = current;
         graph.setEdgeWeight(e,eight+trainsize*0.1);
         break;
       }
     }

    int index = Collections.binarySearch(edgeList,e,comp);
     graph.removeEdge(e);
     edgeList.remove(e);
     List<DefaultWeightedEdge> toRemove = edgeList.subList(index, edgeList.size());
     graph.removeAllEdges(toRemove);



  }

}
