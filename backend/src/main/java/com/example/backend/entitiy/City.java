package com.example.backend.entitiy;

import java.awt.geom.Point2D;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity (name = "city")
public class City {
  @Id
  @GeneratedValue(strategy =  GenerationType.IDENTITY)
  private Long id;

  private Point2D.Float location;

  private String name;
  private Long population;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Point2D.Float getLocation() {
    return location;
  }

  public void setLocation(Point2D.Float location) {
    this.location = location;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getPopulation() {
    return population;
  }

  public void setPopulation(Long population) {
    this.population = population;
  }
}
