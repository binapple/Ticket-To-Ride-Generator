package com.example.backend.entitiy;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity (name = "city")
public class City {
  @Id
  @GeneratedValue(strategy =  GenerationType.IDENTITY)
  private Long id;

  private Point2D.Float location;

  private String name;
  private Long population;

  @ManyToMany (mappedBy = "cities")
  private Set<Map> maps = new HashSet<>();

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

  public Set<Map> getMaps() {
    return maps;
  }

  public void setMaps(Set<Map> maps) {
    this.maps = maps;
  }

}
