package com.example.backend.service;

import java.util.List;

import com.example.backend.endpoint.dto.CityDto;
import com.example.backend.endpoint.dto.CreateMapDto;
import com.example.backend.endpoint.dto.MapPointDto;

public interface MapService {


  /**
   * Creates a new map in the system.
   *
   * @param createMapDto dto which contains all needed properties
   * @return an CreateMapDto of the registered customer
   */
  CreateMapDto create(CreateMapDto createMapDto);

  /**
   * Gets the cities of the Map from OpenStreetMap/Overpass API
   *
   * @param id of the Map
   * @return a list of Cities
   */
  List<CityDto> getInitialCities(Long id);

  /**
   * Gets the cities of the Map from local storage
   *
   * @param id of the Map
   * @return a list of Cities
   */
  List<CityDto> getCities(Long id);


  /**
   * Gets the towns of the Map from OpenStreetMap/Overpass API
   *
   * @param id of the Map
   * @return a list of Cities
   */
  List<CityDto> getTowns(Long id);

  /**
   * Creates the MapPoints for further processing of a map with the chosen cities
   *
   * @param id       id of the corresponding Map
   * @param cityDtos list of the chosen cities
   * @return the MapPoints used for visualizing the connections between cities
   */
  List<MapPointDto> createMapPoints(Long id, List<CityDto> cityDtos);
}
