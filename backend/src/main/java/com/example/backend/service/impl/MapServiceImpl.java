package com.example.backend.service.impl;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import com.example.backend.endpoint.dto.CityDto;
import com.example.backend.endpoint.dto.CreateMapDto;
import com.example.backend.endpoint.dto.MapPointDto;
import com.example.backend.endpoint.dto.PDFDto;
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
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.fop.activity.ContainerUtil;
import org.apache.fop.configuration.Configuration;
import org.apache.fop.configuration.ConfigurationException;
import org.apache.fop.configuration.DefaultConfigurationBuilder;
import org.apache.fop.svg.PDFTranscoder;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;

@Service
public class MapServiceImpl implements MapService {



  private final MapMapper mapMapper;
  private final MapRepository mapRepository;
  private final CityMapper cityMapper;
  private final CityRepository cityRepository;
  private final MapPointMapper mapPointMapper;
  private final MapPointRepository mapPointRepository;

  //these variables are used to change the length and height in millimeters of the resulting game plan (now it is set to DIN A0)
  public static final int FORMATWIDTH = 1189;
  public static final int FORMATHEIGHT = 841;

  //these variables are for the card sizes in millimeters
  public static final int CARD_FORMAT_WIDTH = 68;
  public static final int CARD_FORMAT_HEIGHT = 44;

  //these variables are for the size of the card-collage (A2 fits all 45 cards on one page)
  public static final int CARD_PRINT_WIDTH = 594;
  public static final int CARD_PRINT_HEIGHT = 420;

  //this variable is the real size equivalent of a train from Ticket-To-Ride in millimeters
  public static final float TRAINLENGTH = 26.5f;

  //this variable is the real size equivalent of a cities diameter from Ticket-To-Ride in millimeters
  public static final float CITYDIAMETER = 10.0f;

  //ratio of inches to millimeters
  public static final double INCH_IN_MILLIMETERS = 25.4;

  //Constant for deciding how big the raster image will be rendered
  public static final int DPI = 96;
  //getting folder path from application.properties
  @Value("${maperitive.folderpath}")
  private String maperitivePath;

  //SVG Constant for mm
  public static final double SVGMMCONSTANT = DPI / INCH_IN_MILLIMETERS;

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
        Scanner scans = new Scanner(url.openStream(),StandardCharsets.UTF_8);

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

    //if there are old MapPoints saved to the map, they are reset when choosing new cities with save mode
    if(save)
    {
        if(!mapPointRepository.findMapPointsByMapId(map.getId()).isEmpty())
        {
            List<MapPoint> oldPoints = mapPointRepository.findMapPointsByMapId(map.getId());
            mapPointRepository.deleteAll(oldPoints);
        }
    }

    //initializing the city-MapPoints
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


    //Create a complete graph connecting all cities with each other
    CompleteGraphGenerator<MapPoint, DefaultWeightedEdge> completeGraphGenerator
        = new CompleteGraphGenerator<>();



    completeGraphGenerator.generateGraph(graph,null);

    Set<DefaultWeightedEdge> edges = graph.edgeSet();

    //setting weights equal to distance between cities
    for (DefaultWeightedEdge e: edges
         ) {
      MapPoint source = graph.getEdgeSource(e);
      MapPoint target = graph.getEdgeTarget(e);
      double weight = source.getLocation().distance(target.getLocation());
      graph.setEdgeWeight(e,weight);
    }

    //reduce the edges in graph to get a playable result
    reduceEdges(graph,map);


    //result of edge reduction needs to be saved: Neighbors are MapPoints that are connected by edges
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

      //when using save mode the MapPoints are stored in the database, else it is only used as preview
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

    //comparator used for sorting by edge weight
    Comparator<DefaultWeightedEdge> comp = (o1, o2) -> {
      double first = graph.getEdgeWeight(o1);
      double second = graph.getEdgeWeight(o2);
      return Double.compare(second, first);
    };

    edgeList.sort(comp);

    //calculate the ratio of the current map
    float nwX = map.getNorthWestBoundary().x;
    float seaX = map.getSouthEastBoundary().x;


    float mapWidth = calculateDistancesFromCoordinateSystem(nwX, seaX);


    //trainsize calculated off the set values
    float trainsize = mapWidth * TRAINLENGTH / FORMATWIDTH;
    //define a diameter, because cities have a circle around them
    float cityDiameter = mapWidth * CITYDIAMETER / FORMATWIDTH;

    float eight = trainsize * 8 + cityDiameter;
    float six = trainsize * 6 + cityDiameter;
    float five = trainsize * 5 + cityDiameter;
    //float four = trainsize * 4 + cityDiameter;
    //float three = trainsize * 3 + cityDiameter;

    //remove connections with greater equal length then 5
    //keep ones that are between 4 and 5 as connections are not lines that always start on the city itself
    //for performance reasons binary search is used to have a faster deletion of unnecessary edges

 
    float tooBigSize = eight;
    while (tooBigSize >= five) {

      //This step is used to have a DefaultWeightEdge Object of the graph that can be deleted and is needed for binary search
      Iterator<DefaultWeightedEdge> i = edgeList.iterator();
      DefaultWeightedEdge e = new DefaultWeightedEdge();
      //used to keep enough of the long edges
      //int skipCounter = 0;
      while (i.hasNext()) {
        DefaultWeightedEdge current = i.next();
        if (graph.getEdgeWeight(current) >= tooBigSize) {

          e = current;
          graph.setEdgeWeight(e, tooBigSize);
          break;

        }
      }

      Graph<MapPoint, DefaultWeightedEdge> safetyCopy = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

      Graphs.addGraph(safetyCopy, graph);

      edgeList.sort(comp);
      int index = Collections.binarySearch(edgeList, e, comp);
      graph.removeEdge(e);
      edgeList.remove(e);

      //set the List of Edges that are to be removed from the graph
      List<DefaultWeightedEdge> toRemove;
      if (index < 0) {
        index = 0;
        toRemove = Collections.emptyList();
      } else {
        toRemove = edgeList.subList(0, index);
      }
      graph.removeAllEdges(toRemove);

      //if removing the previous list has led to connection issues, the list itself is checked one by one
      //to find out the edges that are causing the issue and set a boolean to show the issue in the frontend
      if (GraphTests.isConnected(graph)) {
        edgeList = edgeList.subList(index, edgeList.size());
        tooBigSize = tooBigSize - trainsize;
      } else {
        Graphs.addGraph(graph, safetyCopy);
        checkOneByOne(graph, toRemove);
      }


    }

    //new Approach for long connections 8 and two 6 are added after reduction
    //by not setting a weight on the edge, they are prioritized by the following intersection algorithm

    //find out lone cities ( only one, two or three neighbors)
    List<MapPoint> loneCities = new ArrayList<>();

    for (MapPoint m : graph.vertexSet()
    ) {
      int neighborCount = Graphs.neighborListOf(graph, m).size();
      if (neighborCount == 1 || neighborCount == 2 || neighborCount == 3) {
        loneCities.add(m);
      }
    }

    int eightCount = 1;
    int sixCount = 2;


    //match cities to each other when they meet the length criteria
    for (MapPoint one:
         loneCities) {
      for (MapPoint onesNeighbor:
           loneCities) {
        double weight = one.getLocation().distance(onesNeighbor.getLocation());

        double weightDivision = weight / trainsize;
        float edgeSize = (float) weightDivision;

        if(edgeSize <= eight && edgeSize > six)
        {
          if(eightCount > 0) {
            graph.addEdge(one, onesNeighbor);
            eightCount--;
          }
        }

        if(edgeSize <= six && edgeSize > five)
        {
          if(sixCount > 0)
          {
            graph.addEdge(one,onesNeighbor);
            sixCount--;
          }
        }

      }
    }

    //get updated edge List after reduction of edges and adding long ones
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

          //We do not want to delete the connection itself (because it is of course intersecting itself)
          if (source.getLocation() != interSource.getLocation() && source.getLocation() != interTarget.getLocation()) {
            if (target.getLocation() != interSource.getLocation() && target.getLocation() != interTarget.getLocation()) {
              double weightEdge = graph.getEdgeWeight(e);
              double weightInter = graph.getEdgeWeight(intersect);

             // the algorithm prefers shorter connections to keep the grid cleaner
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

      //resetting neighbors after saving Cities as graph
      for (MapPoint mP :
          graph.vertexSet()) {
        mP.setColor(Colorization.CITY);
        mP.setNeighbors(new HashSet<>());
        mP.setNeighbor(new HashSet<>());
        this.mapPointRepository.save(mP);
      }


      Map map = this.mapRepository.getReferenceById(id);

      //calculate the ratio of the current map
      float nwX = map.getNorthWestBoundary().x;
      float seaX = map.getSouthEastBoundary().x;


      float mapWidth = calculateDistancesFromCoordinateSystem(nwX, seaX);

      //trainsize calculated off the set values
      float trainsize = mapWidth * TRAINLENGTH / FORMATWIDTH;
      //define a diameter, because cities have a circle around them
      float cityDiameter = mapWidth * CITYDIAMETER / FORMATWIDTH;


      //find out how many edges the graph has
      int connectionCount = graph.edgeSet().size();

      // 1/3 of connections are colorless
      int colorless = connectionCount / 3;

      // 1/3 of colorless are Tunnel/Joker-Fields
      int jokerCount = colorless / 3;
      boolean sixJoker = false;
      int tunnelCount = jokerCount;
      boolean sixTunnel = false;
      //Random used for later allocation of tunnels and jokers
      Random random = new Random();

      Colorization[] colorizations = Colorization.values();

      // each color should have equal amount of connections
      int colorMax = (connectionCount - colorless) / 8;
      java.util.Map<Colorization, Integer> colorMaxMap = new HashMap<>();
      for (Colorization color : colorizations
      ) {
        colorMaxMap.put(color, colorMax);
      }
      colorMaxMap.put(Colorization.COLORLESS, colorless + colorMax);
      colorMaxMap.put(Colorization.CITY, 0);

      //each color should once have a tunnel
      java.util.Map<Colorization, Boolean> tunnelColorCheck = new HashMap<>();
      for (Colorization color : colorizations) {
        tunnelColorCheck.put(color, false);
      }
      tunnelColorCheck.put(Colorization.COLORLESS, true);
      tunnelColorCheck.put(Colorization.CITY, true);

      //Colorization for each of the cities: Set is used to check if neighboring city has already finished coloring their edges
      Set<Long> coloredCities = new HashSet<>();

      //Keep track of the Colors that a city has used
      java.util.Map<MapPoint, java.util.Map<Colorization, Integer>> mapPointColorCounter = new HashMap<>();
      //Inintialize values to 1 times per MapPoint (only cities)
      for (MapPoint mP :
          graph.vertexSet()) {
        java.util.Map<Colorization, Integer> colorCountMap = new HashMap<>();
        for (Colorization color : colorizations
        ) {
          colorCountMap.put(color, 1);
        }
        colorCountMap.put(Colorization.CITY, 0);
        colorCountMap.put(Colorization.COLORLESS, colorless + colorMax);
        mapPointColorCounter.put(mP, colorCountMap);
      }

      for (MapPoint mP : graph.vertexSet()
      ) {

        if (mP.getColor() == Colorization.CITY) {

          //one city can not have duplicates of same colored connections
          java.util.Map<Colorization, Integer> cityColorCount = mapPointColorCounter.get(mP);

          //to not have all colored tunnels clustered too much their usage per Vertex will be reduced
          int tunnelCityMax = 3;

          for (DefaultWeightedEdge e :
              graph.edgesOf(mP)) {

            //check if neigboring city is already colored
            MapPoint origin = graph.getEdgeSource(e);
            MapPoint destination = graph.getEdgeTarget(e);

            //set origin to be mP (for later calculations necessary)
            if (origin != mP) {
              destination = origin;
              origin = mP;
            }

            if (!coloredCities.contains(destination.getId()) && !coloredCities.contains(origin.getId())) {

              //neighboring city may have already used colors
              java.util.Map<Colorization, Integer> neighborColorCount = mapPointColorCounter.get(destination);

              double edgeLength = graph.getEdgeWeight(e);

              double edgeDivision = edgeLength / trainsize;
              int edgeSize = (int) Math.floor(edgeDivision);

              //if edge is longer or equal six it has to be colorless
              Colorization edgeColor = Colorization.COLORLESS;
              if (edgeSize < 6) {
                edgeColor = selectColor(colorizations, colorMaxMap, cityColorCount, neighborColorCount);
              }

              boolean toTunnel = false;
              boolean toJoker = false;
              //tunnel checks
              if (edgeColor == Colorization.COLORLESS) {
                //tunnel for 8 long connection
                if (edgeSize > 7) {
                  toTunnel = true;
                  tunnelCount--;
                }

                //tunnels and jokers for 6 long connection
                if (edgeSize < 8 && edgeSize > 5) {
                  if (!sixTunnel) {
                    if (!sixJoker) {
                      toJoker = true;
                      sixJoker = true;
                      jokerCount--;
                    } else {
                      toTunnel = true;
                      sixTunnel = true;
                      tunnelCount--;
                    }
                  } else {
                    if (!sixJoker) {
                      toJoker = true;
                      sixJoker = true;
                      tunnelCount--;
                    }
                  }
                }

                //tunnels and jokers for the rest of the colorless connections
                if (edgeSize < 5 && edgeSize > 1) {
                  //random if special colorless connection
                  if (random.nextBoolean()) {
                    //random if joker or tunnel
                    if (random.nextBoolean()) {
                      if (jokerCount > 0) {
                        toJoker = true;
                        jokerCount--;
                      }
                    } else {
                      if (tunnelCount > 0) {
                        toTunnel = true;
                        tunnelCount--;
                      }
                    }
                  }
                }

              } else {
                //check if there is a need of adding colored tunnels (color not yet used, city has not too many tunnels, edge is too short or too long)
                if (!tunnelColorCheck.get(edgeColor) && tunnelCityMax > 0 && edgeSize >= 2 && edgeSize < 4) {
                  toTunnel = true;
                  tunnelColorCheck.put(edgeColor, true);
                  tunnelCityMax--;
                }
              }

              //update cities "color-tracking" Map and usage of the same color in overall "tracking" Map
              int oldCityValue = cityColorCount.get(edgeColor);
              cityColorCount.put(edgeColor, oldCityValue - 1);
              java.util.Map<Colorization, Integer> neigborColorMap = mapPointColorCounter.get(destination);
              int oldNeighborValue = neigborColorMap.get(edgeColor);
              neigborColorMap.put(edgeColor, oldNeighborValue - 1);
              mapPointColorCounter.put(destination, neigborColorMap);
              mapPointColorCounter.put(origin, cityColorCount);
              int oldMaxValue = colorMaxMap.get(edgeColor);
              colorMaxMap.put(edgeColor, oldMaxValue - 1);


              //split the edge into train-sized parts but add a padding to it therefore "distanceBetween" points should be calculated
              float deltaX = destination.getLocation().x - origin.getLocation().x;
              float deltaY = destination.getLocation().y - origin.getLocation().y;
              float distanceBetween = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

              //use the cityDiameter to calculate a padding for the partition of train connection
              float padding = cityDiameter / 2 * -1;

              // Calculate the padding values for X and Y
              float paddingX = padding * deltaX / distanceBetween;
              float paddingY = padding * deltaY / distanceBetween;

              //The padded Start and Endpoints have to be saved
              Point2D.Float paddedStart = new Point2D.Float(origin.getLocation().x - paddingX, origin.getLocation().y - paddingY);
              Point2D.Float paddedEnd = new Point2D.Float(destination.getLocation().x + paddingX, destination.getLocation().y + paddingY);

              MapPoint paddedStartPoint = new MapPoint();
              paddedStartPoint.setColor(edgeColor);
              paddedStartPoint.setMap(map);
              paddedStartPoint.setHasTunnel(toTunnel);
              paddedStartPoint.setHasJoker(toJoker);
              paddedStartPoint.setLocation(paddedStart);
              paddedStartPoint.setNeighbors(new HashSet<>());

              MapPoint paddedEndPoint = new MapPoint();
              paddedEndPoint.setColor(edgeColor);
              paddedEndPoint.setMap(map);
              paddedEndPoint.setHasTunnel(toTunnel);
              paddedEndPoint.setLocation(paddedEnd);
              paddedEndPoint.setNeighbors(new HashSet<>());

              this.mapPointRepository.save(paddedStartPoint);
              this.mapPointRepository.save(paddedEndPoint);

              //calculate the interpolation step sizes based on the padded start and end points
              float stepX = (paddedEnd.x - paddedStart.x) / edgeSize;
              float stepY = (paddedEnd.y - paddedStart.y) / edgeSize;

              //initial point set to padded start
              MapPoint toNeighbor = paddedStartPoint;

              //if Joker is set only one or two fields of the connection should be jokers
              //paddedStart is already having a joker, so we reduce the overall joker usage by one meaning one or zero should be used in the interpolation
              int jokerPointCounter = 1;
              if (random.nextBoolean() && edgeSize < 6) {
                jokerPointCounter = 0;
              }

              //For each trainsized part of the Connection add a new "in-between" MapPoint (for later editing of connections)
              for (int i = 1; i < edgeSize; i++) {
                float newX = paddedStart.x + i * stepX;
                float newY = paddedStart.y + i * stepY;

                MapPoint toBeSaved = new MapPoint();
                toBeSaved.setColor(edgeColor);
                toBeSaved.setMap(map);
                toBeSaved.setHasTunnel(toTunnel);
                if (jokerPointCounter > 0) {
                  jokerPointCounter--;
                } else {
                  toJoker = false;
                }
                toBeSaved.setHasJoker(toJoker);

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


              //save last in-between MapPoint to paddedEndPoint
              Set<MapPoint> toBeAddedToPaddedEndPoint = paddedEndPoint.getNeighbors();
              Set<MapPoint> oldNeigbors = toNeighbor.getNeighbors();

              oldNeigbors.add(paddedEndPoint);
              toBeAddedToPaddedEndPoint.add(toNeighbor);
              toNeighbor.setNeighbors(oldNeigbors);
              paddedEndPoint.setNeighbors(toBeAddedToPaddedEndPoint);

              this.mapPointRepository.save(paddedEndPoint);
              this.mapPointRepository.save(toNeighbor);

              //connect padded points to destination and origin cities
              Set<MapPoint> originNeigbors = origin.getNeighbors();
              Set<MapPoint> destinationNeigbors = destination.getNeighbors();
              Set<MapPoint> paddedEndPointNeighbors = paddedEndPoint.getNeighbors();
              Set<MapPoint> paddedStartPointNeighbors = paddedStartPoint.getNeighbors();

              originNeigbors.add(paddedStartPoint);
              paddedStartPointNeighbors.add(origin);
              destinationNeigbors.add(paddedEndPoint);
              paddedEndPointNeighbors.add(destination);

              origin.setNeighbors(originNeigbors);
              paddedStartPoint.setNeighbors(paddedStartPointNeighbors);
              destination.setNeighbors(destinationNeigbors);
              paddedEndPoint.setNeighbors(paddedEndPointNeighbors);

              this.mapPointRepository.save(origin);
              this.mapPointRepository.save(destination);
              this.mapPointRepository.save(paddedStartPoint);
              this.mapPointRepository.save(paddedEndPoint);


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
    
    private Colorization selectColor(Colorization[] colorizations, java.util.Map<Colorization, Integer> colorMaxMap, java.util.Map<Colorization, Integer> cityColorCount, java.util.Map<Colorization,Integer> neighborColorCount) {
        List<Colorization> availableColors = new ArrayList<>(Arrays.stream(colorizations).toList());


        availableColors.removeIf(color -> cityColorCount.get(color) < 1 || colorMaxMap.get(color) < 1 || neighborColorCount.get(color) < 1);

        if(availableColors.isEmpty())
        {
            return Colorization.COLORLESS;
        }

        //random color is done by shuffling of current available colors
        Collections.shuffle(availableColors);
        return availableColors.get(0);

    }
    

    //used for generating a graph out of all or only "city" MapPoints of a map
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

  @Override
  public PDFDto createGameBoard(Long id) {

    Map map = mapRepository.getReferenceById(id);


    if (!map.getMapPoints().isEmpty()) {
      //keep track what MapPoints are already drawn
      Set<MapPoint> alreadyDrawn = new HashSet<>();

      //searching for each city MapPoint
      Set<MapPoint> cityMapPoints = new HashSet<>();
      for (MapPoint m : map.getMapPoints()
      ) {
        if (m.getColor() == Colorization.CITY) {
          cityMapPoints.add(m);
        }
      }

      // Create an empty SVG document to merge into
      SVGDocument mergedDoc = createEmptySVGDocument();

      //Size it to desired Format
      Element svgRoot = mergedDoc.getRootElement();
      svgRoot.setAttributeNS(null, "width", String.valueOf(FORMATWIDTH)+"mm");
      svgRoot.setAttributeNS(null, "height", String.valueOf(FORMATHEIGHT)+"mm");


      //create tickets
      createTicketCards(map.getId());

      //Render the big image in maperitive
      renderMap(map, FORMATWIDTH);



      //Adding the rendered image to the empty svg

      int calcWidth = (int) Math.floor(FORMATWIDTH * DPI / INCH_IN_MILLIMETERS);
      int calcHeight = (int) Math.floor(FORMATHEIGHT * DPI / INCH_IN_MILLIMETERS);

      String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
      String xlinkNS = "http://www.w3.org/1999/xlink" ;

      //Embed the image to the svg file by encoding it to base64
      Path imagePath = Paths.get(maperitivePath, "output", "map" + map.getId().toString() + ".png");

      byte[] imageBytes = new byte[0];
      try {
        imageBytes = Files.readAllBytes(imagePath);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      String base64Image = Base64.getEncoder().encodeToString(imageBytes);

      String uri =  "data:image/png;base64," + base64Image;
      Element img = mergedDoc.createElementNS(svgNS, "image");
      img.setAttributeNS(xlinkNS, "xlink:href", uri);
      img.setAttributeNS(null, "x", "0");
      img.setAttributeNS(null, "y", "0");
      img.setAttributeNS(null, "width", String.valueOf(calcWidth));
      img.setAttributeNS(null, "height", String.valueOf(calcHeight));
      svgRoot.appendChild(img);



      //go through each "city" MapPoint
      for (MapPoint m : cityMapPoints
      ) {

        //city MapPoint doesn't have to be drawn
        alreadyDrawn.add(m);

        Set<MapPoint> neighboringMPs = m.getNeighbors();

        //for each neighbor, there is a train-connection to be drawn
        for (MapPoint neighbor : neighboringMPs
        ) {

          MapPoint currentMP = neighbor;

          while (currentMP.getColor() != Colorization.CITY) {

            //remove already visited MP from neighbor Set
            Set<MapPoint> currentMPNeighbors = currentMP.getNeighbors();
            List<MapPoint> findNextMP = new ArrayList<>(currentMPNeighbors);
            findNextMP.removeAll(alreadyDrawn);

            //get the next MP
            Iterator findNext = findNextMP.iterator();
            if (findNext.hasNext()) {
              MapPoint nextMP = findNextMP.iterator().next();
              if (nextMP.getColor() != Colorization.CITY) {

                //find out which svg image is to be used
                String svgName = "static/";

                Colorization color = currentMP.getColor();
                boolean hasTunnel = currentMP.isHasTunnel();
                boolean hasJoker = nextMP.isHasJoker() || currentMP.isHasJoker();

                if (hasJoker) {
                  svgName += "joker.svg";
                } else {
                  switch (color) {
                    case PINK:
                      svgName += "pink";
                      break;
                    case RED:
                      svgName += "red";
                      break;
                    case BLUE:
                      svgName += "blue";
                      break;
                    case GREEN:
                      svgName += "green";
                      break;
                    case WHITE:
                      svgName += "white";
                      break;
                    case YELLOW:
                      svgName += "yellow";
                      break;
                    case ORANGE:
                      svgName += "orange";
                      break;
                    case BLACK:
                      svgName += "black";
                      break;
                    default:
                      svgName += "colorless";
                  }

                  if (hasTunnel) {
                    svgName = svgName + "_tunnel.svg";
                  } else {
                    svgName = svgName + ".svg";
                  }
                }

                //load svgDocument to append
                SVGDocument svgDoc = loadSVGDocument(svgName);

                //alter svg content to match the positioning of MapPoints and their connections
                Element rootOfDoc = svgDoc.getRootElement();
                NodeList nodes = rootOfDoc.getChildNodes();

                //get positions to calculate rotation
                float x = currentMP.getLocation().x;
                float y = currentMP.getLocation().y;

                float nextX = nextMP.getLocation().x;
                float nextY = nextMP.getLocation().y;



                //calculate the scaling factors for the different coordinate systems
                float nwX = map.getNorthWestBoundary().x;
                float seaX = map.getSouthEastBoundary().x;
                float nwY = map.getNorthWestBoundary().y;
                float seY = map.getSouthEastBoundary().y;

                //calculate the size of the board in pixels
                double width = FORMATWIDTH*SVGMMCONSTANT;
                double height = FORMATHEIGHT*SVGMMCONSTANT;

                double mapLonLeft = nwX;
                double mapLonRight = seaX;

                //longitude area of map
                double mapLonDelta = mapLonRight - mapLonLeft;

                //used for calculation in mercator projection
                double mapLatBottom = seY;
                double mapLatBottomDegree = mapLatBottom * Math.PI / 180;

                //calculate the position of current mp in pixels
                double[] pixelCoordinates = convertGeoToPixel(y, x, width,height,mapLonLeft, mapLonDelta, mapLatBottom, mapLatBottomDegree);
                //calculate the position for nextMp
                double[] pixelCoordinatesNext = convertGeoToPixel(nextY, nextX, width,height,mapLonLeft, mapLonDelta, mapLatBottom, mapLatBottomDegree);

                double svgX = pixelCoordinates[0];
                double svgY = pixelCoordinates[1];
                double svgNextX = pixelCoordinatesNext[0];
                double svgNextY = pixelCoordinatesNext[1];

                //alter the rotationAngle so that it fits the new translated coordinates
                double deltaX = svgNextX - svgX;
                double deltaY = svgNextY - svgY;

                double rotationAngle = Math.toDegrees(Math.atan2(deltaY, deltaX));


                String transformation = "translate(" + svgX + ", " + svgY + ") rotate(" + rotationAngle + ")";

                for (int i = 0; i < nodes.getLength(); i++) {
                  Node childNode = nodes.item(i);

                  if (childNode instanceof SVGOMElement) {
                    SVGOMElement childElement = (SVGOMElement) childNode;
                    childElement.setAttribute("transform", transformation);
                  }
                }

                appendSVGContent(mergedDoc, svgDoc);


              }
              alreadyDrawn.add(currentMP);
              currentMP = nextMP;
            } else {
              break;
            }
          }

          alreadyDrawn.add(neighbor);

        }

        //merge city svg
        SVGDocument citySVG = loadSVGDocument("static/city.svg");

        //change text of element
        Element text = citySVG.getElementById("textElement");
        text.setTextContent(m.getName());

        //alter svg content to match the positioning of MapPoints and their connections
        Element rootOfDoc = citySVG.getRootElement();
        NodeList nodes = rootOfDoc.getChildNodes();

        //get position of city
        float x = m.getLocation().x;
        float y = m.getLocation().y;

        //get the boundaries of the map
        float nwX = map.getNorthWestBoundary().x;
        float seaX = map.getSouthEastBoundary().x;
        float nwY = map.getNorthWestBoundary().y;
        float seY = map.getSouthEastBoundary().y;

        //calculate the size of the board in pixels
        double width = FORMATWIDTH*SVGMMCONSTANT;
        double height = FORMATHEIGHT*SVGMMCONSTANT;

        double mapLonLeft = nwX;
        double mapLonRight = seaX;

        //longitude area of map
        double mapLonDelta = mapLonRight - mapLonLeft;

        //used for calculation in mercator projection
        double mapLatBottom = seY;
        double mapLatBottomDegree = mapLatBottom * Math.PI / 180;

        double[] pixelCoordinates = convertGeoToPixel(y, x, width,height,mapLonLeft, mapLonDelta, mapLatBottom, mapLatBottomDegree);
        double svgX = pixelCoordinates[0];
        double svgY = pixelCoordinates[1];


        String transformation = "translate(" + svgX + ", " + svgY + ")";

        for (int i = 0; i < nodes.getLength(); i++) {
          Node childNode = nodes.item(i);

          if (childNode instanceof SVGOMElement) {
            SVGOMElement childElement = (SVGOMElement) childNode;
            childElement.setAttribute("transform", transformation);
          }
        }

        appendSVGContent(mergedDoc, citySVG);

      }

      saveSVGDocument(mergedDoc, "merged.svg");
      convertSVGtoPDF(mergedDoc, "merged.pdf");


    }

    //save the gameboard to persistence
    File gameBoard = new File("merged.pdf");

    try {
      byte[] gameBoardBytes = Files.readAllBytes(gameBoard.toPath());

      map.setGameBoard(gameBoardBytes);
      mapRepository.save(map);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    //create PDFDto to return
    PDFDto pdfDto = new PDFDto();
    pdfDto.setGameBoard(map.getGameBoard());
    pdfDto.setTicketCards(map.getTicketCards());
    pdfDto.setId(map.getId());

    return pdfDto;
  }

  @Override
  public PDFDto getGameBoard(Long id) {

    Map map = mapRepository.getReferenceById(id);

    PDFDto pdfDto = new PDFDto();
    if(map != null) {

      pdfDto.setId(map.getId());
      pdfDto.setGameBoard(map.getGameBoard());
      pdfDto.setTicketCards(map.getTicketCards());
    }

    return pdfDto;

  }

  private void createTicketCards(Long mapId) {
    Map map = mapRepository.getReferenceById(mapId);

    //calculate the ratio of the current map
    float nwX = map.getNorthWestBoundary().x;
    float seaX = map.getSouthEastBoundary().x;


    float mapWidth = calculateDistancesFromCoordinateSystem(nwX, seaX);

    //trainsize calculated off the set values
    float trainsize = mapWidth * TRAINLENGTH / FORMATWIDTH;

    //make a graph out of the city MapPoints
    Graph<MapPoint, DefaultWeightedEdge> graph = this.generateGraphFromMap(mapId);

    //Dijkstra for the distances between the MapPoints (equal to the point value of Ticket-Cards)
    DijkstraShortestPath<MapPoint, DefaultWeightedEdge> dijkstra = new DijkstraShortestPath<>(graph);


    Set<MapPoint> citiesSet = graph.vertexSet();
    List<MapPoint> cities = new ArrayList<>(citiesSet.stream().toList());

    //reduce to only cities
    cities.removeIf(mapPoint -> !mapPoint.getColor().equals(Colorization.CITY));

    //Cities should only be used up to 4 times
    java.util.Map<MapPoint, Integer> cityUsedCounter=new HashMap<>();

    for (MapPoint c:
         cities) {
      cityUsedCounter.put(c,0);

    }

    java.util.Map<Integer, Integer> pointsUsedCounter = new HashMap<>();

    //Points of ticketcards correspond to Ticket-To-Ride-Europe statistics
    pointsUsedCounter.put(5, 5);
    pointsUsedCounter.put(6, 5);
    pointsUsedCounter.put(7, 5);
    pointsUsedCounter.put(8, 13);
    pointsUsedCounter.put(9, 2);
    pointsUsedCounter.put(10, 5);
    pointsUsedCounter.put(11, 2);
    pointsUsedCounter.put(12, 2);
    pointsUsedCounter.put(13, 1);
    pointsUsedCounter.put(20, 3);
    pointsUsedCounter.put(21, 3);


    //counter for Tickets
    int ticketCounter = 0;

    // Create an empty SVG document to merge into
    SVGDocument mergedDoc = createEmptySVGDocument();

    //Size it to desired Format
    Element svgRoot = mergedDoc.getRootElement();
    svgRoot.setAttributeNS(null, "width", String.valueOf(CARD_PRINT_WIDTH)+"mm");
    svgRoot.setAttributeNS(null, "height", String.valueOf(CARD_PRINT_HEIGHT)+"mm");
    svgRoot.setAttributeNS(null, "viewBox", "0 0 "+CARD_PRINT_WIDTH+" "+CARD_PRINT_HEIGHT);

    //render a smaller image according to card format
    renderMap(map, CARD_FORMAT_WIDTH);

    int calcWidth = (int) Math.floor(CARD_FORMAT_WIDTH);
    int calcHeight = (int) Math.floor(CARD_FORMAT_HEIGHT);

    String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
    String xlinkNS = "http://www.w3.org/1999/xlink";

    //the image to be embedded to the ticket svg files encoded to base64
    Path imagePath = Paths.get(maperitivePath, "output", "map" + map.getId().toString() + ".png");

    byte[] imageBytes = new byte[0];
    try {
      imageBytes = Files.readAllBytes(imagePath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    String base64Image = Base64.getEncoder().encodeToString(imageBytes);

    String uri = "data:image/png;base64," + base64Image;


    //keep track of used connections
    List<MapPointConnection> connections = new ArrayList<>();


    //ensure each city is used at least once
    for (MapPoint m:
        cities) {

      if (ticketCounter < 45) {

        ShortestPathAlgorithm.SingleSourcePaths<MapPoint, DefaultWeightedEdge> pathsFromM = dijkstra.getPaths(m);


        for (MapPoint destinationPoint : cities
        ) {
          double currentWeight = pathsFromM.getWeight(destinationPoint);

          Integer trainCount = (int) Math.floor(currentWeight / trainsize);

          Integer pointValueTimesUsed = pointsUsedCounter.get(trainCount);
          if(pointValueTimesUsed == null)
          {
            pointValueTimesUsed = 0;
          }

          if (pointValueTimesUsed > 0) {
            Integer mTimesUsed = cityUsedCounter.get(m);
            Integer destinationTimesUsed = cityUsedCounter.get(destinationPoint);

            if (mTimesUsed == 0) {
              if (destinationTimesUsed == 0) {
                //update counters
                pointsUsedCounter.put(trainCount, --pointValueTimesUsed);
                cityUsedCounter.put(m, ++mTimesUsed);
                cityUsedCounter.put(destinationPoint, ++destinationTimesUsed);
                connections.add(new MapPointConnection(m,destinationPoint, trainCount));
                ticketCounter++;

              }
              }else {
                break;
            }
          }


        }
      }

    }


    //non paired cities ensure they are used once
    List<MapPoint> nonPaired = new ArrayList<>(cities);
    for (MapPoint m: nonPaired
         ) {
      if (ticketCounter < 45) {

        ShortestPathAlgorithm.SingleSourcePaths<MapPoint, DefaultWeightedEdge> pathsFromM = dijkstra.getPaths(m);

        for (MapPoint destinationPoint : cities
        ) {
          double currentWeight = pathsFromM.getWeight(destinationPoint);

          Integer trainCount = (int) Math.floor(currentWeight / trainsize);

          Integer pointValueTimesUsed = pointsUsedCounter.get(trainCount);
          if (pointValueTimesUsed == null) {
            pointValueTimesUsed = 0;
          }

          if (pointValueTimesUsed > 0) {
            Integer mTimesUsed = cityUsedCounter.get(m);
            Integer destinationTimesUsed = cityUsedCounter.get(destinationPoint);

            if (mTimesUsed == 0) {
              if (destinationTimesUsed < 3) {
                //update counters
                pointsUsedCounter.put(trainCount, --pointValueTimesUsed);
                cityUsedCounter.put(m, ++mTimesUsed);
                cityUsedCounter.put(destinationPoint, ++destinationTimesUsed);
                connections.add(new MapPointConnection(m, destinationPoint, trainCount));
                ticketCounter++;

              }
            } else {
              break;
            }
          }


        }
      }
    }


    //rest of the connections
    for (MapPoint m:
         cities) {

      if (ticketCounter < 45) {

        ShortestPathAlgorithm.SingleSourcePaths<MapPoint, DefaultWeightedEdge> pathsFromM = dijkstra.getPaths(m);


        for (MapPoint destinationPoint : cities
        ) {
          double currentWeight = pathsFromM.getWeight(destinationPoint);

          Integer trainCount = (int) Math.floor(currentWeight / trainsize);

          Integer pointValueTimesUsed = pointsUsedCounter.get(trainCount);
          if(pointValueTimesUsed == null)
          {
            pointValueTimesUsed = 0;
          }

          if (pointValueTimesUsed > 0) {
            Integer mTimesUsed = cityUsedCounter.get(m);
            Integer destinationTimesUsed = cityUsedCounter.get(destinationPoint);

            if (destinationTimesUsed < 3) {
              if (mTimesUsed < 3) {
                //check if no duplicates are in the list
                if(!connections.contains(new MapPointConnection(m, destinationPoint, trainCount)) && !connections.contains(new MapPointConnection(destinationPoint, m, trainCount))) {
                  //update counters
                  pointsUsedCounter.put(trainCount, --pointValueTimesUsed);
                  cityUsedCounter.put(m, ++mTimesUsed);
                  cityUsedCounter.put(destinationPoint, ++destinationTimesUsed);
                  connections.add(new MapPointConnection(m, destinationPoint, trainCount));
                  ticketCounter++;
                }

              } else {
                break;
              }
            }
          }


        }
      }

    }


    //the saved connections now have to be stored in one file
    int ticketNumber = 0;
    for (MapPointConnection mC: connections
         ) {

      MapPoint m = mC.getSource();
      MapPoint destinationPoint = mC.getDestination();
      int trainCount = mC.getShortestPath();


      // Create an empty SVG document to merge into
      SVGDocument ticketMerge = createEmptySVGDocument();

      //Size it to desired Format
      Element ticketSvgRoot = ticketMerge.getRootElement();
      ticketSvgRoot.setAttributeNS(null, "width", String.valueOf(CARD_FORMAT_WIDTH) + "mm");
      ticketSvgRoot.setAttributeNS(null, "height", String.valueOf(CARD_FORMAT_HEIGHT) + "mm");
      ticketSvgRoot.setAttributeNS(null, "viewBox", "0 0 " + CARD_FORMAT_WIDTH + " " + CARD_FORMAT_HEIGHT);


      //Adding the rendered image to the empty svg
      Element img = ticketMerge.createElementNS(svgNS, "image");
      img.setAttributeNS(xlinkNS, "xlink:href", uri);
      img.setAttributeNS(null, "x", "0");
      img.setAttributeNS(null, "y", "0");
      img.setAttributeNS(null, "width", String.valueOf(calcWidth));
      img.setAttributeNS(null, "height", String.valueOf(calcHeight));

      ticketSvgRoot.appendChild(img);


      //add indicator circles
      SVGDocument citySVG = loadSVGDocument("static/cardCircle.svg");
      SVGDocument destCitySVG = loadSVGDocument("static/cardCircle.svg");

      //alter svg content to match the positioning of MapPoints and their connections
      Element rootOfCityDoc = citySVG.getRootElement();
      NodeList cityNodes = rootOfCityDoc.getChildNodes();
      Element rootOfDestCityDoc = destCitySVG.getRootElement();
      NodeList destCityNodes = rootOfDestCityDoc.getChildNodes();

      //get position of cities
      float x = m.getLocation().x;
      float y = m.getLocation().y;
      float destx = destinationPoint.getLocation().x;
      float desty = destinationPoint.getLocation().y;


      //calculate the scaling factors for the different coordinate systems
      float nwY = map.getNorthWestBoundary().y;
      float seY = map.getSouthEastBoundary().y;

      //calculate the size of the card
      double width = CARD_FORMAT_WIDTH;
      double height = CARD_FORMAT_HEIGHT;

      double mapLonLeft = nwX;
      double mapLonRight = seaX;

      //longitude area of map
      double mapLonDelta = mapLonRight - mapLonLeft;

      //used for calculation in mercator projection
      double mapLatBottom = seY;
      double mapLatBottomDegree = mapLatBottom * Math.PI / 180;

      double[] pixelCoordinates = convertGeoToPixel(y, x, width,height,mapLonLeft, mapLonDelta, mapLatBottom, mapLatBottomDegree);
      double svgX = pixelCoordinates[0];
      double svgY = pixelCoordinates[1];
      double[] pixelCoordinatesNext = convertGeoToPixel(desty, destx, width,height,mapLonLeft, mapLonDelta, mapLatBottom, mapLatBottomDegree);
      double svgDestX = pixelCoordinatesNext[0];
      double svgDestY = pixelCoordinatesNext[1];

      //control the values to fit the card boundaries and fix circle to middle (circle radius is 5 mm)
      svgX = svgX - 5;
      if (svgX > CARD_FORMAT_WIDTH - 10) {
        svgX = CARD_FORMAT_WIDTH - 10;
      }
      if (svgX < 0)
      {
        svgX = 0;
      }

      svgY = svgY - 5;
      if (svgY > CARD_FORMAT_HEIGHT - 10) {
        svgY = CARD_FORMAT_HEIGHT - 10;
      }
      if (svgY < 0)
      {
        svgY = 0;
      }

      svgDestX = svgDestX - 5;
      if (svgDestX > CARD_FORMAT_WIDTH - 10) {
        svgDestX = CARD_FORMAT_WIDTH - 10;
      }
      if (svgDestX < 0)
      {
        svgDestX = 0;
      }

      svgDestY = svgDestY - 5;
      if (svgDestY > CARD_FORMAT_HEIGHT - 10) {
        svgDestY = CARD_FORMAT_HEIGHT - 10;
      }
      if (svgDestY < 0)
      {
        svgDestY = 0;
      }

      String cityTransformation = "translate(" + svgX + ", " + svgY + ")";

      for (int i = 0; i < cityNodes.getLength(); i++) {
        Node childNode = cityNodes.item(i);

        if (childNode instanceof SVGOMElement) {
          SVGOMElement childElement = (SVGOMElement) childNode;
          childElement.setAttribute("transform", cityTransformation);
        }
      }

      appendSVGContent(ticketMerge, citySVG);

      String cityDestTransformation = "translate(" + svgDestX + ", " + svgDestY + ")";

      for (int i = 0; i < destCityNodes.getLength(); i++) {
        Node childNode = destCityNodes.item(i);

        if (childNode instanceof SVGOMElement) {
          SVGOMElement childElement = (SVGOMElement) childNode;
          childElement.setAttribute("transform", cityDestTransformation);
        }
      }

      appendSVGContent(ticketMerge, destCitySVG);


      //append the ticketCard svg with updated texts
      SVGDocument ticketSvg = loadSVGDocument("static/ticketCard.svg");

      //change the city text value
      Element cityText = ticketSvg.getElementById("cityText");
      Element cityText2 = ticketSvg.getElementById("cityText2");
      String cityConnection = m.getName() + " - " + destinationPoint.getName();
      if(cityConnection.length() > 20)
      {
        cityText.setTextContent(m.getName() + " -");
        cityText2.setTextContent(destinationPoint.getName());
      } else {
        cityText.setTextContent(cityConnection);
        cityText2.setTextContent("");
      }

      //change the counter value
      Element counterText = ticketSvg.getElementById("counterText");
      counterText.setTextContent(String.valueOf(ticketNumber+1));

      //change point value
      Element pointText = ticketSvg.getElementById("pointText");
      pointText.setTextContent(String.valueOf(trainCount));

      appendSVGContent(ticketMerge, ticketSvg);

      //calculate how many cards are in each row
      int cardPerRow = CARD_PRINT_WIDTH / CARD_FORMAT_WIDTH;
      //calculate the position of the ticket
      double xOffset = ticketNumber % cardPerRow * CARD_FORMAT_WIDTH;
      double yOffset = (ticketNumber / cardPerRow) * CARD_FORMAT_HEIGHT;

      Element rootOfDoc = ticketMerge.getRootElement();
      NodeList nodes = rootOfDoc.getChildNodes();

      //append the ticket to the overall ticket svg
      String transformation;

      //this step is necessary to move the circle indicators with the offset + their position on the card itself
      for (int i = 0; i < nodes.getLength(); i++) {
        Node childNode = nodes.item(i);

        if (childNode instanceof SVGOMElement) {
          SVGOMElement childElement = (SVGOMElement) childNode;
          if (i >= 1 && i <= 8) {
            transformation = "translate(" + (xOffset + svgX) + ", " + (yOffset + svgY) + ")";
          } else if (i > 8 && i <= 17) {
            transformation = "translate(" + (xOffset + svgDestX) + ", " + (yOffset + svgDestY) + ")";
          } else {
            transformation = "translate(" + xOffset + ", " + yOffset + ")";
          }
          childElement.setAttribute("transform", transformation);
        }
      }


      appendSVGContent(mergedDoc, ticketMerge);
      ticketNumber++;
    }

    saveSVGDocument(mergedDoc, "tickets.svg");
    convertSVGtoPDF(mergedDoc, "tickets.pdf");

    //save the tickets to persistence
    File ticketCards = new File("tickets.pdf");

    try {
      byte[] ticketCardsBytes = Files.readAllBytes(ticketCards.toPath());
      map.setTicketCards(ticketCardsBytes);
      mapRepository.save(map);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  //used for easier management in the creation of TicketCards
  private class MapPointConnection{
    private MapPoint source;
    private MapPoint destination;

    private int shortestPath;

    public MapPointConnection(MapPoint source, MapPoint destination, int shortestPath) {
      this.source = source;
      this.destination = destination;
      this.shortestPath = shortestPath;
    }

    public MapPoint getSource() {
      return source;
    }

    public MapPoint getDestination() {
      return destination;
    }

    public int getShortestPath() {
      return shortestPath;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      MapPointConnection that = (MapPointConnection) o;
      return shortestPath == that.shortestPath && Objects.equals(source, that.source) && Objects.equals(destination, that.destination);
    }

    @Override
    public int hashCode() {
      return Objects.hash(source, destination, shortestPath);
    }
  }

  //this method allows us to calculate Distances from two points of our geographical coordinate System.
  //This method should include all the special cases where the points are not in the same quadrant of the graph
  private float calculateDistancesFromCoordinateSystem(float origin, float distanceToOrigin) {
    if (origin >= 0 && distanceToOrigin >= 0) {
      return Math.abs(origin - distanceToOrigin);
    } else if (origin < 0 && distanceToOrigin < 0) {
      return Math.abs(origin) - Math.abs(distanceToOrigin);
    } else {
      return Math.abs(origin) + Math.abs(distanceToOrigin);
    }
  }


  //creating a new and empty SVGDocument where everything is merged into
  private static SVGDocument createEmptySVGDocument() {
    String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
    DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
    Document doc = impl.createDocument(svgNS, "svg", null);
    return (SVGDocument) doc;
  }

  //load an existing SVGDocument from the classPath
  private static SVGDocument loadSVGDocument(String svgFile) {
    String parser = XMLResourceDescriptor.getXMLParserClassName();
    SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
    Resource resource = new ClassPathResource(svgFile);

    try {
      return (SVGDocument) factory.createDocument(resource.getURI().toString());
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  //here the svg documents get merged/appended to the initial one
  private static void appendSVGContent(SVGDocument targetDoc, SVGDocument sourceDoc) {

//    Element x = sourceDoc.getDocumentElement();
//    Node y = targetDoc.importNode(x, true);
//    targetDoc.getDocumentElement().appendChild(y);

    Element srcElement;
    Element dstElement;

    srcElement = sourceDoc.getRootElement();

    dstElement = targetDoc.getRootElement();

    NodeList nodeList = srcElement.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      Node importedNode = targetDoc.importNode(node, true);
      dstElement.appendChild(importedNode);
    }

  }

  //the document has to be saved somewhere
  private static void saveSVGDocument(SVGDocument svgDocument, String outputFileName) {
    try {
      SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(svgDocument);
      SVGGraphics2D g2d = new SVGGraphics2D(ctx, false);

      try (Writer writer = new OutputStreamWriter(new FileOutputStream(outputFileName), "UTF-8")) {
        g2d.stream(svgDocument.getDocumentElement(), writer);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void convertSVGtoPDF(Document document, String pdfFilePath) {
    /*try{
    Transcoder transcoder = new PDFTranscoder();
    TranscoderInput transcoderInput = new TranscoderInput(document);
    FileOutputStream fOStream = new FileOutputStream(pdfFilePath);
    TranscoderOutput transcoderOutput = new TranscoderOutput(fOStream);
      transcoder.transcode(transcoderInput, transcoderOutput);
      fOStream.flush();
      fOStream.close();
    } catch (TranscoderException e) {
      throw new RuntimeException(e);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }*/
    PDFTranscoder transcoder = new PDFTranscoder();

    try {
      DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();
      InputStream inputStream = MapServiceImpl.class.getResourceAsStream("/fop.xml");
      Configuration cfg = cfgBuilder.build(inputStream);
      ContainerUtil.configure(transcoder, cfg);

    TranscoderInput transcoderInput = new TranscoderInput(document);
    FileOutputStream fOStream = new FileOutputStream(pdfFilePath);
    TranscoderOutput transcoderOutput = new TranscoderOutput(fOStream);
    transcoder.transcode(transcoderInput, transcoderOutput);
  } catch (TranscoderException | FileNotFoundException | ConfigurationException e) {
      throw new RuntimeException(e);
    }

  }

  //render the background image of the map through maperitive with set formatWidth
  private void renderMap(Map map, int formatWidth)
  {

    // getting boundingBox values
    double nwX = map.getNorthWestBoundary().x;
    double nwY = map.getNorthWestBoundary().y;
    double seaX = map.getSouthEastBoundary().x;
    double seY = map.getSouthEastBoundary().y;

    //getting min and max values of long & lat for maperitive
    double minLong = Math.min(nwX, seaX);
    double maxLong = Math.max(nwX,seaX);
    double minLat = Math.min(nwY, seY);
    double maxLat = Math.max(nwY,seY);

    Long id = map.getId();

    int calcWidth = (int) Math.floor(formatWidth * DPI / INCH_IN_MILLIMETERS);

    // create a maperitive script for automatic rendering
    String scriptContent = String.format(Locale.US,
        "zoom-bounds bounds=%.15f,%.15f,%.15f,%.15f\n" +
            "set-print-bounds-geo bounds=%.15f,%.15f,%.15f,%.15f\n" +
            "export-bitmap width=%d file="+maperitivePath+"/output/map%d.png\n" +
            "set-setting name=map.decoration.grid value=false\n" +
            "set-setting name=map.decoration.scale value=false\n" +
            "set-setting name=map.decoration.attribution value=false",
        minLong,minLat,maxLong,maxLat,
        minLong,minLat,maxLong,maxLat,
        calcWidth, id
        );

    //save the script
    String path = maperitivePath + "/Scripts/map" + id + ".mscript";
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
      writer.write(scriptContent);
    } catch (IOException e) {
      e.printStackTrace();
    }

    //run maperitive with default script + custom script for rendering of map
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.directory(new File(maperitivePath));
    processBuilder.command("sudo","./Maperitive.sh","-defscr", "-exa", path);

    Process process = null;
    try {
      process = processBuilder.start();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    try {
      int exitCode = process.waitFor();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  //converting geographical Data (longitude and latitude) into pixel data for mercator projection
  private double[] convertGeoToPixel(double latitude, double longitude,
                                     double mapWidth, //in pixels
                                     double mapHeight, //in pixels
                                     double mapLonLeft, //in degrees
                                     double mapLonDelta, //in degrees
                                     double mapLatBottom, //in degrees
                                     double mapLatBottomDegree) { //in Radians

    //x value is calculated based on the position and the size of map
    double x = (longitude - mapLonLeft) * (mapWidth / mapLonDelta);

    //for the latitutude it has to be calculated to degrees first
    latitude = latitude * Math.PI / 180;

    //y is calculated based on radius and its offset from the equator
    double worldMapWidth = ((mapWidth / mapLonDelta) * 360) / (2 * Math.PI);
    double mapOffsetY = (worldMapWidth / 2 * Math.log((1 + Math.sin(mapLatBottomDegree)) / (1 - Math.sin(mapLatBottomDegree))));
    double y = mapHeight - ((worldMapWidth / 2 * Math.log((1 + Math.sin(latitude)) / (1 - Math.sin(latitude)))) - mapOffsetY);

    double[] dArray = new double[2];
    dArray[0] = x;
    dArray[1] = y;

    return dArray;
  }
}
