package com.example.backend.service.impl;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
import com.example.backend.repository.MapPointRepository;
import com.example.backend.repository.MapRepository;
import com.example.backend.service.MapService;
import com.example.backend.type.Colorization;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.CriteriaBuilder;
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
  private final MapPointRepository mapPointRepository;

  @Autowired
  public MapServiceImpl(MapMapper mapMapper, MapRepository mapRepository, CityMapper cityMapper, CityRepository cityRepository, MapPointMapper mapPointMapper, MapPointRepository mapPointRepository) {
    this.mapMapper = mapMapper;
    this.mapRepository = mapRepository;
    this.cityMapper = cityMapper;
    this.cityRepository = cityRepository;
    this.mapPointMapper = mapPointMapper;
    this.mapPointRepository = mapPointRepository;
  }

  @Override
  public CreateMapDto create(CreateMapDto createMapDto) {

    Map map = mapMapper.createMapDtoToMap(createMapDto);

    map = mapRepository.save(map);

    return mapMapper.mapToCreateMapDto(map);
  }

    @Override
    public CreateMapDto get(Long id) {
        Map map = mapRepository.getReferenceById(id);
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

    String urlEncoded = URLEncoder.encode(urlUnencoded, StandardCharsets.UTF_8);

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
                if (alreadySavedCity.isEmpty()) {
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
  public List<MapPointDto> createMapPoints(Long id, List<CityDto> cityDtos, boolean save) {

    Map map =  mapRepository.getReferenceById(id);

    Graph<MapPoint, DefaultWeightedEdge> graph
        = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

    for (CityDto c:
         cityDtos) {
      MapPoint newPoint = new MapPoint();

      newPoint.setLocation(c.getLocation());
      newPoint.setName(c.getName());
      newPoint.setNeighbors(new HashSet<>());
      if(save)
      {
          newPoint.setColor(Colorization.CITY);
          newPoint.setConnectionIssue(false);
          newPoint.setMap(map);
          newPoint = this.mapPointRepository.save(newPoint);
      }
      else {
          newPoint.setId(c.getId());
      }
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
          Set<MapPoint> pNeighbors = new HashSet<>();
          for (DefaultWeightedEdge e: graph.edgesOf(p)
               ) {
              MapPoint source = graph.getEdgeSource(e);
              MapPoint target = graph.getEdgeTarget(e);

              if(!source.equals(p))
              {
                  pNeighbors.add(source);
              } else
              {
                  pNeighbors.add(target);
              }

          }
          p.setNeighbors(pNeighbors);

      }

      Set<MapPoint> mapPointsWithNeighbors = graph.vertexSet();

      if(save)
      {
          this.mapPointRepository.saveAll(mapPointsWithNeighbors);
      }

      List<MapPoint> toReturn = new ArrayList<>(mapPointsWithNeighbors);

    return mapPointMapper.mapPointListToMapPointDtoListCustom(toReturn);
  }

  private void reduceEdges(Graph<MapPoint, DefaultWeightedEdge> graph, Map map) {

      Set<DefaultWeightedEdge> edges = graph.edgeSet();

      List<DefaultWeightedEdge> edgeList = new ArrayList<>(edges);


      Comparator<DefaultWeightedEdge> comp = (o1, o2) -> {
          double first = graph.getEdgeWeight(o1);
          double second = graph.getEdgeWeight(o2);
          return Double.compare(second, first);
      };

      edgeList.sort(comp);

      Point2D.Float sw = map.getSouthWestBoundary();
      Point2D.Float ne = map.getNorthEastBoundary();

      float trainsize = (Math.abs(ne.x - sw.x)) * 26 / 1189;

      float eight = trainsize * 8;
      float six = trainsize * 6;
      float five = trainsize * 5;

      //remove connections with greater equal length then eight and leave 1 of eight
      //then remove all of 7 and all of 6 except 2
      //then remove all of 5
      float tooBigSize = eight * 2;
      while (tooBigSize >= five) {

          //This step is used to have a DefaultWeightEdge Object of the graph that can be deleted and is needed for binary search
          Iterator<DefaultWeightedEdge> i = edgeList.iterator();
          DefaultWeightedEdge e = new DefaultWeightedEdge();
          //used to keep enough of the long edges
          int skipCounter = 0;
          while (i.hasNext()) {
              DefaultWeightedEdge current = i.next();
              if (graph.getEdgeWeight(current) >= tooBigSize) {

                  if (tooBigSize <= eight && tooBigSize > six) {
                      if (skipCounter >= 1) {
                          e = current;
                          graph.setEdgeWeight(e, tooBigSize);
                          break;
                      }
                  } else if (tooBigSize <= six) {
                      if (skipCounter >= 3) {
                          e = current;
                          graph.setEdgeWeight(e, tooBigSize);
                          break;
                      }
                  } else {
                      e = current;
                      graph.setEdgeWeight(e, tooBigSize);
                      break;
                  }

                  skipCounter++;
              }
          }

          Graph<MapPoint, DefaultWeightedEdge> safetyCopy = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

          Graphs.addGraph(safetyCopy, graph);

          edgeList.sort(comp);
          int index = Collections.binarySearch(edgeList, e, comp);
          graph.removeEdge(e);
          edgeList.remove(e);

          if (tooBigSize <= eight && tooBigSize > six) {
              index = index - 1;
          }
          if (tooBigSize <= six) {
              index = index - 3;
          }
          if (index < 0) {
              index = 0;
          }

          List<DefaultWeightedEdge> toRemove = edgeList.subList(0, index);
          graph.removeAllEdges(toRemove);

          if (GraphTests.isConnected(graph)) {
              edgeList = edgeList.subList(index, edgeList.size());
              tooBigSize = tooBigSize - trainsize;
          } else {
              Graphs.addGraph(graph, safetyCopy);
              checkOneByOne(graph, toRemove);
          }


      }


      edgeList = edges.stream().toList();

      //Check if connections are intersecting each other
      for (DefaultWeightedEdge e : edgeList
      ) {
          MapPoint source = graph.getEdgeSource(e);
          MapPoint target = graph.getEdgeTarget(e);
          Line2D edgeLine = new Line2D.Float(source.getLocation(), target.getLocation());

          for (DefaultWeightedEdge intersect : edgeList
          ) {
              MapPoint interSource = graph.getEdgeSource(intersect);
              MapPoint interTarget = graph.getEdgeTarget(intersect);
              Line2D interLine = new Line2D.Float(interSource.getLocation(), interTarget.getLocation());

              if (edgeLine.intersectsLine(interLine)) {


                  if (source.getLocation() != interSource.getLocation() && source.getLocation() != interTarget.getLocation()) {
                      if (target.getLocation() != interSource.getLocation() && target.getLocation() != interTarget.getLocation()) {
                              double weightEdge = graph.getEdgeWeight(e);
                              double weightInter = graph.getEdgeWeight(intersect);
                              if (weightEdge > weightInter) {
                                  graph.removeEdge(e);
                                  break;
                              } else {
                                  graph.removeEdge(intersect);
                              }
                      }
                  }
              }
          }

      }


  }

  private void checkOneByOne(Graph<MapPoint, DefaultWeightedEdge> graph, List<DefaultWeightedEdge> toRemove) {

        Graph<MapPoint, DefaultWeightedEdge> safetyCopy = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        Graphs.addGraph(safetyCopy, graph);

        DefaultWeightedEdge problemEdge;

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

    @Override
    public List<MapPointDto> colorizeMapPoints(Long id, List<CityDto> cityDtos) {
        List<MapPointDto> mapPointDtoList = this.createMapPoints(id, cityDtos, true);

        Graph<MapPoint, DefaultWeightedEdge> graph = this.generateGraphFromMap(id);

        Set<DefaultWeightedEdge> edgeSet = graph.edgeSet();

        for (MapPoint mP:
             graph.vertexSet()) {
            mP.setColor(Colorization.CITY);
            mP.setNeighbors(new HashSet<>());
            mP.setNeighbor(new HashSet<>());
            this.mapPointRepository.save(mP);
        }


        Map map = this.mapRepository.getReferenceById(id);

        //used for colorization of longer connections
        Point2D.Float sw = map.getSouthWestBoundary();
        Point2D.Float ne = map.getNorthEastBoundary();

        float trainsize = (Math.abs(ne.x - sw.x)) * 26 / 1189;

        float eight = trainsize * 8;
        float six = trainsize * 6;

        //find out how many edges the graph has
       int connectionCount = graph.edgeSet().size();

        // 1/3 of connections are colorless
        int colorless = connectionCount / 3;

        // 1/3 of colorless are Tunnel/Joker-Fields
        int jokerCount = colorless / 3;
        int tunnelCount = jokerCount;

        // each color should have equal amount of connections
        int colorMax = (connectionCount - colorless) / 8;
        java.util.Map<Colorization, Integer> colorMaxMap = new HashMap<Colorization, Integer>();
        for (Colorization color: Colorization.values()
             ) {
            colorMaxMap.put(color, colorMax);
        }
        colorMaxMap.put(Colorization.COLORLESS, colorless+colorMax);
        colorMaxMap.put(Colorization.CITY, 0);

        //Random number Generator for color distribution
        Random random = new Random();

        //Colorization for each of the cities: Set is used to check if neighboring city is already finished coloring their edges
       Set<Long> coloredCities = new HashSet<>();

        Colorization[] colorizations = Colorization.values();

        for (MapPoint mP: graph.vertexSet()
             ) {

            if(mP.getColor() == Colorization.CITY) {

                //one city can not have duplicates of same colored connections
                java.util.Map<Colorization, Integer> cityColorCount = new HashMap<>();
                for (Colorization c: Colorization.values()
                     ) {
                    cityColorCount.put(c,1);
                }
                cityColorCount.put(Colorization.COLORLESS, colorless+colorMax);
                cityColorCount.put(Colorization.CITY,0);

                for (DefaultWeightedEdge e :
                        graph.outgoingEdgesOf(mP)) {

                    //check if neigboring city is already colored
                    MapPoint origin = graph.getEdgeSource(e);
                    MapPoint destination = graph.getEdgeTarget(e);

                    //set origin to be mP (for later calculations necessary)
                    if(origin != mP)
                    {
                        destination = origin;
                        origin= mP;
                    }

                    if (!coloredCities.contains(destination.getId()) && !coloredCities.contains(origin.getId())) {

                        Colorization edgeColor = null;
                        boolean check = false;
                        do {
                            //getting random Color (colorless included)
                            int colorNum = random.nextInt(9 - 1 + 1) + 1;
                            edgeColor = colorizations[colorNum];

                            //check if color maximum has been reached & check if already used in the city
                            if(cityColorCount.get(edgeColor) > 0) {
                                if (colorMaxMap.get(edgeColor) > 0) {
                                    check = true;
                                }
                            }
                        } while (!check);

                        //update cities "color-tracking" Map and usage of the same color in overall "tracking" Map
                        int oldCityValue = cityColorCount.get(edgeColor);
                        cityColorCount.put(edgeColor, oldCityValue - 1);
                        int oldMaxValue = colorMaxMap.get(edgeColor);
                        colorMaxMap.put(edgeColor, oldMaxValue - 1);

                        double edgeLength = graph.getEdgeWeight(e);

                        double edgeDivision = edgeLength / trainsize;
                        int edgeSize = (int) Math.floor(edgeDivision);

                        //split the edge into train-sized parts, therefore calculate slope of the edge
                        float edgeSlope = (destination.getLocation().y - origin.getLocation().y) / (destination.getLocation().x - origin.getLocation().x);


                        MapPoint toNeighbor = origin;

                        //For each trainsized part of the Connection add a new "in-between" MapPoint (for later editing of connections)
                        for (int i = 0; i < edgeSize-1; i++) {
                            float newX = (float) (toNeighbor.getLocation().x + (trainsize / Math.sqrt(1 + Math.pow(edgeSlope, 2))));
                            float newY = (float) (toNeighbor.getLocation().y + (edgeSlope * (newX - toNeighbor.getLocation().x)));
                            MapPoint toBeSaved = new MapPoint();
                            toBeSaved.setColor(edgeColor);
                            toBeSaved.setMap(map);
                            toBeSaved.setLocation(new Point2D.Float(newX, newY));
                            Set<MapPoint> toBeSavedNeighbors = new HashSet<>();

                            Set<MapPoint> oldNeighbors = toNeighbor.getNeighbors();

                            oldNeighbors.add(toBeSaved);
                            toBeSavedNeighbors.add(toNeighbor);
                            toBeSaved.setNeighbors(toBeSavedNeighbors);
                            toNeighbor.setNeighbors(oldNeighbors);

                            this.mapPointRepository.save(toBeSaved);
                            this.mapPointRepository.save(toNeighbor);

                            toNeighbor = toBeSaved;
                        }

                        //final MapPoint has to be added to destination as a neighbor and vice versa
                        Set<MapPoint> toBeAddedToDestination = destination.getNeighbors();
                        Set<MapPoint> oldNeigbors = toNeighbor.getNeighbors();

                        oldNeigbors.add(destination);
                        toBeAddedToDestination.add(toNeighbor);
                        toNeighbor.setNeighbors(oldNeigbors);
                        destination.setNeighbors(toBeAddedToDestination);

                        this.mapPointRepository.save(destination);
                        this.mapPointRepository.save(toNeighbor);

                    }
                }

                //add finished city to be already colored
                coloredCities.add(mP.getId());

            }

        }

        List<MapPoint> mapPointList = this.mapPointRepository.findMapPointsByMapId(id);

        return this.mapPointMapper.mapPointListToMapPointDtoListCustom(mapPointList);



    }

    @Override
    public List<MapPointDto> getMapPoints(Long id) {
        return this.mapPointMapper.mapPointListToMapPointDtoListCustom(this.mapPointRepository.findMapPointsByMapId(id));
    }

    private Graph<MapPoint, DefaultWeightedEdge> generateGraphFromMap(long id) {
      Graph<MapPoint,DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

      List<MapPoint> savedMapPoints = this.mapPointRepository.findMapPointsByMapId(id);

      savedMapPoints.sort(Comparator.comparing(MapPoint::getId));

      //Add MapPoints to graph
        for (MapPoint mP: savedMapPoints
             ) {
            graph.addVertex(mP);
        }

        //Add edges to graph
        Set<MapPoint> mapPoints = graph.vertexSet();
        List<MapPoint> finishedMapPoint = new ArrayList<>();

        for (MapPoint mP: mapPoints
             ) {

            for (MapPoint neighbor : mP.getNeighbors()
            ) {
                if (!finishedMapPoint.contains(neighbor)) {
                    graph.addEdge(mP, neighbor);
                }
            }
            finishedMapPoint.add(mP);
        }

        //Set Weights for graph
        for (DefaultWeightedEdge e: graph.edgeSet()
             ) {
            MapPoint source = graph.getEdgeSource(e);
            MapPoint target = graph.getEdgeTarget(e);
            double weight = source.getLocation().distance(target.getLocation());
            graph.setEdgeWeight(e,weight);
        }

      return graph;
    }
}
