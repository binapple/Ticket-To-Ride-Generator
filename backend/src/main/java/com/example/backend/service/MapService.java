package com.example.backend.service;

import java.io.UnsupportedEncodingException;

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
   * Gets the 50 largest Cities of the Map from OpenStreetMap/Overpass API
   *
   * @param id of the Map
   * @return a list of Cities
   */
  void getCities(Long id);

}
