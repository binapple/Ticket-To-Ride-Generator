package com.example.backend.service;

import java.util.List;

import com.example.backend.endpoint.dto.CityDto;
import com.example.backend.endpoint.dto.CreateMapDto;
import com.example.backend.endpoint.dto.MapPointDto;
import com.example.backend.endpoint.dto.PDFDto;
import com.example.backend.endpoint.dto.StatusDto;

public interface MapService {


  /**
   * Creates a new map in the system.
   *
   * @param createMapDto dto which contains all needed properties
   * @return an CreateMapDto of the saved map
   */
  CreateMapDto create(CreateMapDto createMapDto);

  /**
   * Get all maps saved in the system
   *
   * @return a list of CreateMapDtos of all saved maps
   */
  List<CreateMapDto> getAllMaps();

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

  /**
   * Creates the GameBoard and TicketCards for a saved Map and its corresponding MapPoints
   *
   * @param id of the Map
   * @param DPI print resolution of OSM image
   * @return a PDFDto containing the GameBoard and its corresponding TicketCards
   */
  PDFDto createGameBoard(Long id, int DPI);

   /**
   * Gets the GameBoard and Tickets as a PDFDto from as saved Map
   *
   * @param id of the Map
   * @return a PDFDto containing the GameBoard and its corresponding TicketCards
   */
  PDFDto getGameBoard(Long id);


  /**
   * Gets the status of a Maps progress when creating tickets and gameboards
   *
   * @param id of the Map
   * @return a StatusDto that contains a progress message
   */
  StatusDto getStatus(Long id);
}
