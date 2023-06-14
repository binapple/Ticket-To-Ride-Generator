package com.example.backend.integration;

import java.awt.geom.Point2D;

import com.example.backend.endpoint.dto.CreateMapDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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

  @Test
  public void givenNothing_addMapReturnsNewMap() throws Exception {
    CreateMapDto createMapDto = new CreateMapDto();

    createMapDto.setNorthWestBoundary(new Point2D.Float(66.791909f, -34.628906f));
    createMapDto.setNorthEastBoundary(new Point2D.Float(66.791909f, 66.621094f));
    createMapDto.setSouthWestBoundary(new Point2D.Float(18.729502f, -34.628906f));
    createMapDto.setSouthEastBoundary(new Point2D.Float(18.729502f, 66.621094f));

    MvcResult mvcResult = this.mockMvc.perform(post("/api/maps")
             .contentType(MediaType.APPLICATION_JSON)
             .content(objectMapper.writeValueAsString(createMapDto)))
        .andExpect(status().isCreated())
        .andDo(print())
        .andReturn();

    MockHttpServletResponse response = mvcResult.getResponse();
}


}
