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
   * @param id id of the corresponding Map
   * @param cityDtos list of the chosen cities
   * @param save if the MapPoints should be saved, or they are just for viewing purposes (when still selecting cities)
   * @return the MapPoints used for visualizing the connections between cities
   */
  List<MapPointDto> createMapPoints(Long id, List<CityDto> cityDtos, boolean save);

  /**
   * Colorizes the MapPoints of a map with the chosen cities and saves them to the map
   *
   * @param id id of the corresponding Map
   * @param cityDtos list of the chosen cities
   * @return the MapPoints used for visualizing the colored connections between cities
   */
  List<MapPointDto> colorizeMapPoints(Long id, List<CityDto> cityDtos);


  /**
   * Gets the Map from local storage
   *
   * @param id of the Map
   * @return a CreateMapDto
   */
    CreateMapDto get(Long id);

  /**
   * Gets all MapPoints from a certain Map
   *
   * @param id of the Map
   * @return a List of MapPointDtos
   */
    List<MapPointDto> getMapPoints(Long id);
}
