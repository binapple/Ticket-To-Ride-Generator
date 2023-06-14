package com.example.backend.endpoint.mapper;


import com.example.backend.endpoint.dto.CreateMapDto;
import com.example.backend.entitiy.Map;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MapMapper {

  /**
   * Maps a createMapDto to a Map entity.
   *
   * @param createMapDto dto to map
   * @return map entity
   */
  Map createMapDtoToMap(CreateMapDto createMapDto);

  /**
   * Maps a map entitiy to a CreateMapDto.
   *
   * @param map map entity
   * @return CreateMapDto representation of map
   */
  CreateMapDto mapToCreateMapDto(Map map);
}
