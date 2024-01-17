package com.example.backend.service.impl;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.example.backend.endpoint.dto.MapPointDto;
import com.example.backend.endpoint.mapper.MapPointMapper;
import com.example.backend.entitiy.Map;
import com.example.backend.entitiy.MapPoint;
import com.example.backend.repository.MapPointRepository;
import com.example.backend.repository.MapRepository;
import com.example.backend.service.MapPointService;
import com.example.backend.type.Colorization;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MapPointServiceImpl implements MapPointService {

  private final MapPointRepository mapPointRepository;
  private final MapPointMapper mapPointMapper;

  private final MapRepository mapRepository;

  @Autowired
  public MapPointServiceImpl(MapPointRepository mapPointRepository, MapPointMapper mapPointMapper, MapRepository mapRepository) {
    this.mapPointRepository = mapPointRepository;
    this.mapPointMapper = mapPointMapper;
    this.mapRepository = mapRepository;
  }

  @Override
  public MapPointDto get(Long id) {
    MapPoint mapPoint = mapPointRepository.getReferenceById(id);
    return mapPointMapper.mapPointToMapPointDto(mapPoint);
  }

  @Override
  public MapPointDto update(MapPointDto mapPointDto) {
    MapPoint mapPoint = mapPointRepository.getReferenceById(mapPointDto.getId());



    mapPoint.setLocation(mapPointDto.getLocation());
    mapPoint.setName(mapPointDto.getName());

    if(mapPoint.getColor() != Colorization.CITY) {

      // "normal" MapPoints should not become "city colored"
      if (mapPointDto.getColor() != Colorization.CITY) {

        //when changing color it has to be done for the whole connection
        List<MapPoint> connectionMapPoints = getAllConnectionNeighbors(mapPoint);

        for (MapPoint mP : connectionMapPoints
        ) {
          mP.setColor(mapPointDto.getColor());
        }

        //saving all changes to repo
        mapPointRepository.saveAll(connectionMapPoints);
      }

      //maybe irrelevant
      mapPoint.setConnectionIssue(mapPointDto.isConnectionIssue());


      mapPoint.setHasJoker(mapPointDto.isHasJoker());

      //changing tunnels leads to whole connection changing tunnels
      if(mapPoint.isHasTunnel() != mapPointDto.isHasTunnel())
      {
        List<MapPoint> connectionMapPoints = getAllConnectionNeighbors(mapPoint);

        for (MapPoint mP : connectionMapPoints
        ) {
          mP.setHasTunnel(mapPointDto.isHasTunnel());
        }

        //saving all changes to repo
        mapPointRepository.saveAll(connectionMapPoints);
      }

      //maybe irrelevant
      Set<MapPoint> neighbors = new HashSet<>();
      for (Long mpId : mapPointDto.getNeighbors()
      ) {
        MapPoint neighbor = mapPointRepository.getReferenceById(mpId);
        neighbors.add(neighbor);
      }
      mapPoint.setNeighbors(neighbors);



    }


    //ensure all changes are on the repo
    MapPoint savedMapPoint = mapPointRepository.save(mapPoint);
    return mapPointMapper.mapPointToMapPointDto(savedMapPoint);
  }

  //This method is used to fetch all MapPoints of one single connection
  //To ensure every MapPoint is found a DFS is used
  private List<MapPoint> getAllConnectionNeighbors(MapPoint startPoint)
  {

    List<MapPoint> connectionNeighbors = new ArrayList<>();
    Set<MapPoint> visitedMapPoints = new HashSet<>();

    depthFirstSearch(startPoint, connectionNeighbors, visitedMapPoints);


    return connectionNeighbors;
  }

  //To modify a connection "City"-MapPoints are not necessary and thus a condition to stop the recursion
  private void depthFirstSearch(MapPoint current, List<MapPoint> connectionNeighbors, Set<MapPoint> visitedMapPoints) {
    visitedMapPoints.add(current);

    if(current.getColor() == Colorization.CITY)
    {
      return;
    }

    connectionNeighbors.add(current);

    for (MapPoint neighbor: current.getNeighbors())
    {
      if(!visitedMapPoints.contains(neighbor))
      {
        depthFirstSearch(neighbor, connectionNeighbors, visitedMapPoints);
      }
    }


  }

  @Override
  public void deleteConnection(Long id) {

    Optional<MapPoint> oMP = mapPointRepository.findById(id);
    MapPoint mP = oMP.orElse(null);
    if(mP != null) {
      if (!(mP.getColor() == Colorization.CITY)) {

        List<MapPoint> toBeDeleted = getAllConnectionNeighbors(mP);

        //neighbors have to be updated so that removed MapPoints don't show up anymore
        for (MapPoint m : toBeDeleted
        ) {
          Set<MapPoint> neighbors = m.getNeighbors();
          for (MapPoint neighbor : neighbors
          ) {
            Set<MapPoint> neighborNeighbors = neighbor.getNeighbors();
            neighborNeighbors.remove(m);
            neighbor.setNeighbors(neighborNeighbors);
            mapPointRepository.save(neighbor);
          }
          //as m gets deleted anyway his neighbors gets set to empty
          m.setNeighbors(new HashSet<>());
          mapPointRepository.save(m);
        }

        mapPointRepository.deleteAll(toBeDeleted);
      } else {
        Set<MapPoint> mPNeighbors = mP.getNeighbors();
        List<MapPoint> mPNeighborsList = mPNeighbors.stream().toList();
        for (int i = 0; i < mPNeighborsList.size(); i++) {
          this.deleteConnection(mPNeighborsList.get(i).getId());
        }
        mapPointRepository.delete(mP);
      }
    }
  }

  @Override
  public List<MapPointDto> addConnection(List<MapPointDto> cityMapPoints) {

    //check if MapPoints are city MapPoints and if there is two of them
    MapPointDto mPDto1 = cityMapPoints.get(0);
    MapPointDto mPDto2 = cityMapPoints.get(1);
try {
  MapPoint mP1 = mapPointRepository.getReferenceById(mPDto1.getId());
  MapPoint mP2 = mapPointRepository.getReferenceById(mPDto2.getId());

  if(mP1.getColor() == Colorization.CITY && mP2.getColor() == Colorization.CITY)
  {
    List<MapPoint> newlyCreatedMapPoints = createConnection(mP1, mP2);
    return mapPointMapper.mapPointListToMapPointDtoListCustom(newlyCreatedMapPoints);
  }

} catch (EntityNotFoundException e)
{
  return null;
}





    return null;


  }

  private List<MapPoint> createConnection(MapPoint mP1, MapPoint mP2) {
    //List containing new MapPoints
    List<MapPoint> newMapPoints  = new ArrayList<>();

    //variables necessary for Interpolation
    MapPoint origin = mP1;
    MapPoint destination = mP2;
    Colorization edgeColor = Colorization.COLORLESS;

    //calculate the ratio of map to real size
    Map map = mP1.getMap();

    float nwX = map.getNorthWestBoundary().x;
    float seaX = map.getSouthEastBoundary().x;


    float mapWidth = calculateDistancesFromCoordinateSystem(nwX, seaX);

    //trainsize calculated off the set values
    float trainsize = mapWidth * MapServiceImpl.TRAINLENGTH / MapServiceImpl.FORMATWIDTH;
    //define a diameter, because cities have a circle around them
    float cityDiameter = mapWidth * MapServiceImpl.CITYDIAMETER / MapServiceImpl.FORMATWIDTH;


    double weight = origin.getLocation().distance(destination.getLocation());


    double edgeDivision = weight / trainsize;
    int edgeSize = (int) Math.floor(edgeDivision);




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
    paddedStartPoint.setLocation(paddedStart);
    paddedStartPoint.setNeighbors(new HashSet<>());

    MapPoint paddedEndPoint = new MapPoint();
    paddedEndPoint.setColor(edgeColor);
    paddedEndPoint.setMap(map);
    paddedEndPoint.setLocation(paddedEnd);
    paddedEndPoint.setNeighbors(new HashSet<>());

    this.mapPointRepository.save(paddedStartPoint);
    this.mapPointRepository.save(paddedEndPoint);

    //calculate the interpolation step sizes based on the padded start and end points
    float stepX = (paddedEnd.x - paddedStart.x) / edgeSize;
    float stepY = (paddedEnd.y - paddedStart.y) / edgeSize;

    //initial point set to padded start
    MapPoint toNeighbor = paddedStartPoint;


    //For each trainsized part of the Connection add a new "in-between" MapPoint (for later editing of connections)
    for (int i = 1; i < edgeSize; i++) {
      float newX = paddedStart.x + i * stepX;
      float newY = paddedStart.y + i * stepY;

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

      MapPoint saved = this.mapPointRepository.save(toBeSaved);
      //new MapPoint has to be added to return list
      newMapPoints.add(saved);
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
    MapPoint paddedStartPointSaved = this.mapPointRepository.save(paddedStartPoint);
    MapPoint paddedEndPointSaved = this.mapPointRepository.save(paddedEndPoint);

    newMapPoints.add(paddedStartPointSaved);
    newMapPoints.add(paddedEndPointSaved);

    return newMapPoints;


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
}
