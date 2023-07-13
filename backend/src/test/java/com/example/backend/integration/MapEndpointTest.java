package com.example.backend.integration;

import java.awt.geom.Point2D;
import java.util.List;

import com.example.backend.endpoint.dto.CityDto;
import com.example.backend.endpoint.dto.CreateMapDto;
import com.example.backend.service.MapService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MapEndpointTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MapService mapService;

  @Test
  public void givenNothing_addMapReturnsNewMap() throws Exception {
    CreateMapDto createMapDto = new CreateMapDto();

    createMapDto.setNorthWestBoundary(new Point2D.Float(66.791909f, -34.628906f));
    createMapDto.setNorthEastBoundary(new Point2D.Float(66.791909f, 66.621094f));
    createMapDto.setSouthWestBoundary(new Point2D.Float(18.729502f, -34.628906f));
    createMapDto.setSouthEastBoundary(new Point2D.Float(18.729502f, 66.621094f));
    createMapDto.setZoom(4);

    MvcResult mvcResult = this.mockMvc.perform(post("/api/maps")
             .contentType(MediaType.APPLICATION_JSON)
             .content(objectMapper.writeValueAsString(createMapDto)))
        .andExpect(status().isCreated())
        .andDo(print())
        .andReturn();

    MockHttpServletResponse response = mvcResult.getResponse();

    assertAll(
        () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
        () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
    );

    CreateMapDto createMapDtoResponse = objectMapper.readValue(response.getContentAsString(), CreateMapDto.class);

    assertAll(
        () -> assertEquals(createMapDto.getNorthEastBoundary(), createMapDtoResponse.getNorthEastBoundary()),
        () -> assertEquals(createMapDto.getSouthWestBoundary(), createMapDtoResponse.getSouthWestBoundary()),
        () -> assertEquals(createMapDto.getNorthWestBoundary(), createMapDtoResponse.getNorthWestBoundary()),
        () -> assertEquals(createMapDto.getSouthEastBoundary(), createMapDtoResponse.getSouthEastBoundary()),
        () -> assertEquals(createMapDto.getZoom(), createMapDtoResponse.getZoom())
    );

}

  @Test
  public void givenMap_whenGetCitiesIsCalled_thenCitiesAreReturnedAsList() throws Exception {
    CreateMapDto createMapDto = new CreateMapDto();

    createMapDto.setNorthWestBoundary(new Point2D.Float(-34.628906f, 66.791909f));
    createMapDto.setNorthEastBoundary(new Point2D.Float(66.621094f, 66.791909f));
    createMapDto.setSouthWestBoundary(new Point2D.Float(-34.628906f, 18.729502f));
    createMapDto.setSouthEastBoundary(new Point2D.Float(66.621094f, 18.729502f));
    createMapDto.setZoom(4);

    CreateMapDto saved = mapService.create(createMapDto);

    byte[] body = mockMvc.perform(MockMvcRequestBuilders
                                      .get("/api/maps/cities/"+saved.getId())
                                      .contentType(MediaType.APPLICATION_JSON)
                         ).andExpect(status().isOk())
                         .andReturn().getResponse().getContentAsByteArray();



    List<CityDto> cityDtos = objectMapper.readerFor(CityDto.class).<CityDto>readValues(body).readAll();

    assertAll(
        () -> assertNotEquals(cityDtos, null)
    );

  }

  @Test
  public void givenMap_whenCitiesLoadedAndGetTownsIsCalled_thenTownsAreCalled() throws Exception{

    CreateMapDto createMapDto = new CreateMapDto();

    createMapDto.setNorthWestBoundary(new Point2D.Float(13.07f, 48.29f));
    createMapDto.setNorthEastBoundary(new Point2D.Float(17.35f, 48.29f));
    createMapDto.setSouthWestBoundary(new Point2D.Float(13.07f, 46.21f));
    createMapDto.setSouthEastBoundary(new Point2D.Float(17.35f, 46.21f));
    createMapDto.setZoom(4);

    CreateMapDto saved = mapService.create(createMapDto);

    byte[] body = mockMvc.perform(MockMvcRequestBuilders
                                      .get("/api/maps/towns/"+saved.getId())
                                      .contentType(MediaType.APPLICATION_JSON)
                         ).andExpect(status().isOk())
                         .andReturn().getResponse().getContentAsByteArray();



    List<CityDto> cityDtos = objectMapper.readerFor(CityDto.class).<CityDto>readValues(body).readAll();

    assertAll(
        () -> assertNotEquals(cityDtos, null)
    );

  }


  @Test
  public void givenMapWithSelectedCities_whenSavingCities_thenCoordinatesSavedAsMapPoints() throws Exception {
    CreateMapDto createMapDto = new CreateMapDto();

    createMapDto.setNorthWestBoundary(new Point2D.Float(13.07f, 48.29f));
    createMapDto.setNorthEastBoundary(new Point2D.Float(17.35f, 48.29f));
    createMapDto.setSouthWestBoundary(new Point2D.Float(13.07f, 46.21f));
    createMapDto.setSouthEastBoundary(new Point2D.Float(17.35f, 46.21f));
    createMapDto.setZoom(4);

    CreateMapDto saved = mapService.create(createMapDto);

    byte[] body = mockMvc.perform(MockMvcRequestBuilders
                                      .get("/api/maps/cities/"+saved.getId())
                                      .contentType(MediaType.APPLICATION_JSON)
                         ).andExpect(status().isOk())
                         .andReturn().getResponse().getContentAsByteArray();

    byte[] body2 = mockMvc.perform(MockMvcRequestBuilders
                                      .get("/api/maps/towns/"+saved.getId())
                                      .contentType(MediaType.APPLICATION_JSON)
                         ).andExpect(status().isOk())
                         .andReturn().getResponse().getContentAsByteArray();



    List<CityDto> selectedCities = objectMapper.readerFor(CityDto.class).<CityDto>readValues(body2).readAll();


    MvcResult mvcResult = this.mockMvc.perform(post("/api/maps/selection/"+saved.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(selectedCities.subList(0,50))))
            .andExpect(status().isCreated())
            .andDo(print())
            .andReturn();



  }

}
