package com.example.backend.endpoint;

import java.util.Comparator;
import java.util.List;

import com.example.backend.endpoint.dto.CityDto;
import com.example.backend.endpoint.dto.CreateMapDto;
import com.example.backend.endpoint.dto.MapPointDto;
import com.example.backend.endpoint.dto.PDFDto;
import com.example.backend.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  @CrossOrigin()
  @ResponseStatus(HttpStatus.CREATED)
  public CreateMapDto createMap(@RequestBody CreateMapDto createMapDto) {
    return mapService.create(createMapDto);
  }

  @GetMapping()
  @CrossOrigin()
  @ResponseStatus(HttpStatus.OK)
  public List<CreateMapDto> getAllMap()
  {
    return mapService.getAllMaps();
  }


  @GetMapping(value = "/{id}")
  @CrossOrigin()
  @ResponseStatus(HttpStatus.OK)
  public CreateMapDto getMap(@PathVariable Long id)
  {
    return mapService.get(id);
  }

  @PostMapping(value = "/selection/{id}")
  @CrossOrigin()
  @ResponseStatus(HttpStatus.OK)
  public List<MapPointDto> showMapPoints(@PathVariable Long id,@RequestBody List<CityDto> cityDtos) {
    return mapService.createMapPoints(id,cityDtos, false);
  }

  @PostMapping(value = "/colorization/{id}")
  @CrossOrigin()
  @ResponseStatus(HttpStatus.OK)
  public List<MapPointDto> colorizeMapPoints(@PathVariable Long id,@RequestBody List<CityDto> cityDtos) {
    return mapService.colorizeMapPoints(id,cityDtos);
  }

  @GetMapping(value = "/mapPoints/{id}")
  @CrossOrigin()
  @ResponseStatus(HttpStatus.OK)
  public List<MapPointDto> getMapPoints(@PathVariable Long id) {
    return mapService.getMapPoints(id);
  }

  @GetMapping(value = "/cities/{id}")
  @CrossOrigin()
  @ResponseStatus(HttpStatus.OK)
  public List<CityDto> getCities(@PathVariable Long id) {

    List<CityDto> cityDtoList = mapService.getCities(id);

    if (cityDtoList.isEmpty()) {
      cityDtoList = mapService.getInitialCities(id);
    }

    cityDtoList.sort(
        new Comparator<CityDto>() {
          @Override
          public int compare(CityDto o1, CityDto o2) {
            return o2.getPopulation().compareTo(o1.getPopulation());
          }
        }
    );

    return cityDtoList;
  }

  @GetMapping (value = "/towns/{id}")
  @CrossOrigin ()
  @ResponseStatus(HttpStatus.OK)
  public List<CityDto> getTowns(@PathVariable Long id) {
    List<CityDto> cityDtoList = mapService.getTowns(id);

    cityDtoList.sort(
        new Comparator<CityDto>() {
          @Override
          public int compare(CityDto o1, CityDto o2) {
            return o2.getPopulation().compareTo(o1.getPopulation());
          }
        }
    );

    return cityDtoList;
  }

  @PostMapping (value = "/gameBoard/create/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @CrossOrigin ()
  @ResponseStatus(HttpStatus.OK)
  public PDFDto createGameBoard(@PathVariable Long id, @RequestBody CreateMapDto mapDto) {
  return mapService.createGameBoard(id, mapDto.getDpi());
  }

  @GetMapping (value = "/gameBoard/{id}")
  @CrossOrigin ()
  @ResponseStatus(HttpStatus.OK)
  public PDFDto getGameBoard(@PathVariable Long id) {

    PDFDto pdfDto = mapService.getGameBoard(id);

    if(pdfDto.getGameBoard() == null || pdfDto.getTicketCards() == null || pdfDto.getId() == null)
    {
      pdfDto = mapService.createGameBoard(id, 500);
    }

    return pdfDto;
  }

}
