package com.example.backend.endpoint;


import java.util.List;

import com.example.backend.endpoint.dto.MapPointDto;
import com.example.backend.service.MapPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/mapPoints")
public class MapPointEndpoint {

  private final MapPointService mapPointService;
  @Autowired
  public MapPointEndpoint(MapPointService mapPointService) {
    this.mapPointService = mapPointService;
  }

  @GetMapping(value = "/{id}")
  @CrossOrigin(origins = "http://localhost:4200")
  @ResponseStatus(HttpStatus.OK)
  public MapPointDto getMapPoint(@PathVariable Long id) {
    return mapPointService.get(id);
  }

  @PutMapping(value = "/{id}")
  @CrossOrigin(origins = "http://localhost:4200")
  @ResponseStatus(HttpStatus.OK)
  public MapPointDto updateMapPoint(@PathVariable Long id, @RequestBody MapPointDto mapPointDto)
  {
    return mapPointService.update(mapPointDto);
  }

  @PutMapping(value = "")
  @CrossOrigin(origins = "http://localhost:4200")
  @ResponseStatus(HttpStatus.OK)
  public List<MapPointDto> addConnection(@RequestBody List<MapPointDto> cityMapPoints) {
    return mapPointService.addConnection(cityMapPoints);
  }

}
