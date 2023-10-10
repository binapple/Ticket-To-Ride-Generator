package com.example.backend.service.impl;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.backend.endpoint.dto.MapPointDto;
import com.example.backend.endpoint.mapper.MapPointMapper;
import com.example.backend.entitiy.MapPoint;
import com.example.backend.repository.MapPointRepository;
import com.example.backend.service.MapPointService;
import com.example.backend.type.Colorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MapPointServiceImpl implements MapPointService {

  private final MapPointRepository mapPointRepository;
  private final MapPointMapper mapPointMapper;

  @Autowired
  public MapPointServiceImpl(MapPointRepository mapPointRepository, MapPointMapper mapPointMapper) {
    this.mapPointRepository = mapPointRepository;
    this.mapPointMapper = mapPointMapper;
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

}
