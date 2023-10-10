package com.example.backend.endpoint.mapper;


import com.example.backend.endpoint.dto.CreateMapDto;
import com.example.backend.entitiy.Map;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MapMapper {

  /**
   * Maps a createMapDto to a Map entity.
   *
   * @param createMapDto dto to be mapped
   * @return map entity
   */
  Map createMapDtoToMap(CreateMapDto createMapDto);

  /**
   * Maps a map entity to a CreateMapDto.
   *
   * @param map map entity to be mapped to dto
   * @return representation of map as dto
   */
  CreateMapDto mapToCreateMapDto(Map map);
}
