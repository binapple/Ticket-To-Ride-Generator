package com.example.backend.repository;

import java.util.List;

import com.example.backend.entitiy.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

  /**
   * Find Cities by mapID.
   *
   * @return associated cities or null if no cities for this map are found
   */
  List<City> findCitiesByMapsId(Long mapId);

  /**
   * Find Cities by name and population.
   *
   * @return associated cities or null if no city with that name and population is found
   */
  List<City> findCityByNameAndPopulation (String name, Long population);

}
