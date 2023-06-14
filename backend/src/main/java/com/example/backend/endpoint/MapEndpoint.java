package com.example.backend.endpoint;

import com.example.backend.endpoint.dto.CreateMapDto;
import com.example.backend.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/maps")
public class MapEndpoint {

  private final MapService mapService;

  @Autowired
  public MapEndpoint(MapService mapService) {  this.mapService = mapService;  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CreateMapDto createMap(@RequestBody CreateMapDto createMapDto) {
    return mapService.create(createMapDto);
  }

}
