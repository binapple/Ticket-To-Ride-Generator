package com.example.backend.endpoint.dto;

import java.awt.geom.Point2D;

public class CityDto {

  private Long id;

  private String name;

  private Point2D.Float location;
  private Long population;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Point2D.Float getLocation() {
    return location;
  }

  public void setLocation(Point2D.Float location) {
    this.location = location;
  }

  public Long getPopulation() {
    return population;
  }

  public void setPopulation(Long population) {
    this.population = population;
  }

  @Override
  public String toString() {
    return "CityDto{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", location=" + location +
        ", population=" + population +
        '}';
  }
}
