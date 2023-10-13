package com.example.backend.integration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.example.backend.endpoint.dto.CreateMapDto;
import com.example.backend.endpoint.dto.MapPointDto;
import com.example.backend.endpoint.mapper.MapPointMapper;
import com.example.backend.entitiy.Map;
import com.example.backend.entitiy.MapPoint;
import com.example.backend.repository.MapPointRepository;
import com.example.backend.repository.MapRepository;
import com.example.backend.service.MapPointService;
import com.example.backend.type.Colorization;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.annotations.TimeZoneColumn;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MapPointEndpointTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MapPointService mapPointService;

  @Autowired
  private MapRepository mapRepository;

  @Autowired
  private MapPointRepository mapPointRepository;

  @Autowired
  private MapPointMapper mapPointMapper;

  @Test
  public void givenMapPoint_whenGettingMapPoint_returnsMapPointFromBackend() throws Exception {

    //given
    MapPoint mP = new MapPoint();
    mP.setLocation(new Point2D.Float(0,0));
    mP.setColor(Colorization.CITY);
    mP.setName("test");
    mP.setId(1L);
    mP.setNeighbors(new HashSet<>());
    MapPoint saved = mapPointRepository.save(mP);
    MapPointDto toCheck = mapPointMapper.mapPointToMapPointDto(saved);


    //when

    byte[] body = mockMvc.perform(MockMvcRequestBuilders
                                      .get("/api/mapPoints/" + toCheck.getId())
                                      .contentType(MediaType.APPLICATION_JSON)
                         ).andExpect(status().isOk())
                         .andReturn().getResponse().getContentAsByteArray();

    //return

    List<MapPointDto> returned = objectMapper.readerFor(MapPointDto.class).<MapPointDto>readValues(body).readAll();
    MapPointDto found = returned.get(0);

    assertAll(
        () -> assertEquals(toCheck.getColor(), found.getColor()),
        () -> assertEquals(toCheck.getLocation(), found.getLocation()),
        () -> assertEquals(toCheck.getName(), found.getName()),
        () -> assertEquals(toCheck.getId(), found.getId()),
        () -> assertEquals(toCheck.getNeighbors(), found.getNeighbors())
    );

  }

  @Test
  public void givenMapPoint_whenUpdatingMapPoint_returnsUpdatedMapPointFromBackend() throws Exception {

    //given
    MapPoint mP = new MapPoint();
    mP.setLocation(new Point2D.Float(0,0));
    mP.setColor(Colorization.CITY);
    mP.setName("test");
    mP.setId(1L);
    mP.setNeighbors(new HashSet<>());
    MapPoint saved = mapPointRepository.save(mP);
    MapPointDto toUpdate = mapPointMapper.mapPointToMapPointDto(saved);


    //when updating

    toUpdate.setName("test2");

    byte[] body = mockMvc.perform(MockMvcRequestBuilders
                                      .put("/api/mapPoints/" + toUpdate.getId())
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content(objectMapper.writeValueAsString(toUpdate))
                         ).andExpect(status().isOk())
                         .andReturn().getResponse().getContentAsByteArray();

    //return

    List<MapPointDto> returned = objectMapper.readerFor(MapPointDto.class).<MapPointDto>readValues(body).readAll();
    MapPointDto found = returned.get(0);

    assertAll(
        () -> assertEquals(mP.getColor(), found.getColor()),
        () -> assertEquals(mP.getLocation(), found.getLocation()),
        //name should be changed
        () -> assertNotEquals(mP.getName(), found.getName()),
        () -> assertEquals(mP.getId(), found.getId())
    );

  }

  @Test
  public void givenTwoCityMapPoints_whenAddConnection_returnsAllNewMapPointsFromBackend() throws Exception {
    //given

    Map newMap = new Map();

    newMap.setNorthWestBoundary(new Point2D.Float(13.07f, 48.29f));
    newMap.setNorthEastBoundary(new Point2D.Float(17.35f, 48.29f));
    newMap.setSouthWestBoundary(new Point2D.Float(13.07f, 46.21f));
    newMap.setSouthEastBoundary(new Point2D.Float(17.35f, 46.21f));
    newMap.setZoom(4);

   Map savedMap = mapRepository.save(newMap);

    MapPoint mP1 = new MapPoint();
    mP1.setLocation(new Point2D.Float(1,1));
    mP1.setColor(Colorization.CITY);
    mP1.setName("test");
    mP1.setId(1L);
    mP1.setMap(savedMap);
    mP1.setNeighbors(new HashSet<>());
    MapPoint saved = mapPointRepository.save(mP1);
    MapPointDto mPDto1 = mapPointMapper.mapPointToMapPointDto(saved);

    MapPoint mP2 = new MapPoint();
    mP2.setLocation(new Point2D.Float(0,0));
    mP2.setColor(Colorization.CITY);
    mP2.setName("test");
    mP2.setId(1L);
    mP2.setMap(savedMap);
    mP2.setNeighbors(new HashSet<>());
    MapPoint saved2 = mapPointRepository.save(mP2);
    MapPointDto mPDto2 = mapPointMapper.mapPointToMapPointDto(saved2);

    List<MapPointDto> mPDtos = new ArrayList<>();
    mPDtos.add(mPDto1);
    mPDtos.add(mPDto2);

    //when
    byte[] body = mockMvc.perform(MockMvcRequestBuilders
                                      .put("/api/mapPoints")
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content(objectMapper.writeValueAsString(mPDtos))
                         ).andExpect(status().isOk())
                         .andReturn().getResponse().getContentAsByteArray();

    //then
    List<MapPointDto> returned = objectMapper.readerFor(MapPointDto.class).<MapPointDto>readValues(body).readAll();
    MapPointDto found = returned.get(0);
    MapPointDto found2 = returned.get(1);


    //there should be at least two MapPoints
    assertNotEquals(found,null);
    assertNotEquals(found2 ,null);

  }

}
