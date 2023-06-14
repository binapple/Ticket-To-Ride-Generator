package com.example.backend.service.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import com.example.backend.endpoint.dto.CreateMapDto;
import com.example.backend.endpoint.mapper.MapMapper;
import com.example.backend.entitiy.Map;
import com.example.backend.repository.MapRepository;
import com.example.backend.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MapServiceImpl implements MapService {


  private final MapMapper mapMapper;
  private final MapRepository mapRepository;

  @Autowired
  public MapServiceImpl(MapMapper mapMapper, MapRepository mapRepository) {
    this.mapMapper = mapMapper;
    this.mapRepository = mapRepository;
  }

  @Override
  public CreateMapDto create(CreateMapDto createMapDto) {

    Map map = mapMapper.createMapDtoToMap(createMapDto);

    map = mapRepository.save(map);

    getCities(map.getId());

    return mapMapper.mapToCreateMapDto(map);
  }

  @Override
  public void getCities(Long id) {

    Map map = mapRepository.getReferenceById(id);
    String boundary = map.getSouthWestBoundary().x + "," +
        map.getSouthWestBoundary().y + "," +
        map.getNorthEastBoundary().x + "," +
        map.getNorthEastBoundary().y;

    String urlUnencoded = "[out:json]; node [\"place\"=\"city\"] (" + boundary + "); out body;";

    String urlEncoded = URLEncoder.encode(urlUnencoded);

    try {
      URL url = new URL("https://overpass-api.de/api/interpreter?data="+urlEncoded);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.connect();

      int responseCode = connection.getResponseCode();

      if(responseCode != 200){
        throw new RuntimeException("HttpResponseCode" + responseCode);
      }
      else {
        StringBuilder responseString = new StringBuilder();
        Scanner scans = new Scanner(url.openStream());

        while (scans.hasNext())
        {
          responseString.append(scans.nextLine());
        }

        scans.close();

        System.out.println(responseString);

      }

    } catch (MalformedURLException e) {
      throw new RuntimeException("MalformedURL "+e.getMessage());
    } catch (IOException e) {
      throw new RuntimeException("IOException "+e.getMessage());
    }


  }
}
