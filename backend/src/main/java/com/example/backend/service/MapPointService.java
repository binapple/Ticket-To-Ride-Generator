package com.example.backend.service;

import java.awt.geom.Point2D;
import java.util.List;

import com.example.backend.endpoint.dto.MapPointDto;
import com.example.backend.entitiy.MapPoint;

public interface MapPointService {

  /**
   * Gets the MapPoint from local storage
   *
   * @param id of the MapPoint
   * @return a MapPointDto
   */
  MapPointDto get(Long id);

  /**
   * updates the MapPoints values with certain conditions when updating different values:
   *
   * -name -> changes the name of the MapPoint
   * -location -> changes the location of the MapPoint
   * -Colorization -> the whole connection gets a different color
   * -tunnel -> removing or adding Tunnels also changes the whole connection
   * -joker -> can be added/removed at will if COLORLESS (not for whole connection)
   *
   * ! Also, important: Colorization.CITY colored MapPoints are special as they were the original
   * chosen cities. For these MapPoints only name and location can be changed!
   *
   * @param mapPointDto representation of a MapPoint to update
   * @return a MapPointDto with updated values
   */
  MapPointDto update(MapPointDto mapPointDto);

  /**
   * Creates a new Connection and thus new MapPoints between two existing city MapPoints
   * New Connections are always colorless and have no tunnels or jokers
   *
   * @param cityMapPoints List of CityMapPoints
   * @return a List of all newly created MapPoints
   */
  List<MapPointDto> addConnection(List<MapPointDto> cityMapPoints);

  /**
   * Deletes the MapPoint from local storage
   *
   * @param id of the MapPoint to be deleted
   */
  void deleteConnection(Long id);
}
