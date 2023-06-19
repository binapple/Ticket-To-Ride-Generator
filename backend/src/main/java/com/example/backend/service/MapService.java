package com.example.backend.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.example.backend.endpoint.dto.CityDto;
import com.example.backend.endpoint.dto.CreateMapDto;

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


}
