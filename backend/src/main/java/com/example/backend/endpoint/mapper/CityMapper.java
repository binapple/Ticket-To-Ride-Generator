package com.example.backend.endpoint.mapper;

import java.util.List;

import com.example.backend.endpoint.dto.CityDto;
import com.example.backend.entitiy.City;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CityMapper {

  /**
   * Maps a CityDto to a City entity.
   *
   * @param cityDto dto to be mapped
   * @return City entity
   */
  City cityDtoToCity(CityDto cityDto);

  /**
   * Maps a City entitiy to a CityDto.
   *
   * @param city city entity to be mapped to dto
   * @return representation of city as dto
   */
  CityDto cityToCityDto(City city);

  /**
   * Maps a list of Cities to a list of CityDtos.
   *
   * @param cities a list of city entities to map
   * @return list of CityDtos
   */
  List<CityDto> cityListToCityDtoList(List<City> cities);



}
