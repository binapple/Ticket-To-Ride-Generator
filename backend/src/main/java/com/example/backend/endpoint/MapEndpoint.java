package com.example.backend.endpoint;

import java.util.Comparator;
import java.util.List;

import com.example.backend.endpoint.dto.CityDto;
import com.example.backend.endpoint.dto.CreateMapDto;
import com.example.backend.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  @GetMapping(value = "/cities/{id}")
  @ResponseStatus(HttpStatus.OK)
  public List<CityDto> getCities(@PathVariable Long id) {

    List<CityDto> cityDtoList = mapService.getCities(id);

    if (cityDtoList != null) {

      cityDtoList = mapService.getInitialCities(id);

      cityDtoList.sort(
          new Comparator<CityDto>() {
            @Override
            public int compare(CityDto o1, CityDto o2) {
              return o2.getPopulation().compareTo(o1.getPopulation());
            }
          }
      );
    }

    return cityDtoList;
  }
}
