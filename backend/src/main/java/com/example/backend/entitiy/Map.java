package com.example.backend.entitiy;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

@Entity (name = "map")
public class Map {
  @Id
  @GeneratedValue(strategy =  GenerationType.IDENTITY)
  private Long id;

  @Column(name = "north_west_boundary")
  private Point2D.Float northWestBoundary;

  @Column(name = "south_west_boundary")
  private Point2D.Float southWestBoundary;

  @Column(name = "north_east_boundary")
  private Point2D.Float northEastBoundary;

  @Column(name = "south_east_boundary")
  private Point2D.Float southEastBoundary;

  @Column(columnDefinition = "integer default 5")
  private int zoom;
  @ManyToMany
  @JoinTable(
      name = "map_city",
      joinColumns = @JoinColumn(name = "map_id"),
      inverseJoinColumns =  @JoinColumn(name = "city_id"))
  private Set<City> cities = new HashSet<>();

  public void setId(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public Point2D.Float getNorthWestBoundary() {
    return northWestBoundary;
  }

  public void setNorthWestBoundary(Point2D.Float northWestBoundary) {
    this.northWestBoundary = northWestBoundary;
  }

  public Point2D.Float getSouthWestBoundary() {
    return southWestBoundary;
  }

  public void setSouthWestBoundary(Point2D.Float southWestBoundary) {
    this.southWestBoundary = southWestBoundary;
  }

  public Point2D.Float getNorthEastBoundary() {
    return northEastBoundary;
  }

  public void setNorthEastBoundary(Point2D.Float northEastBoundary) {
    this.northEastBoundary = northEastBoundary;
  }

  public Point2D.Float getSouthEastBoundary() {
    return southEastBoundary;
  }

  public void setSouthEastBoundary(Point2D.Float southEastBoundary) {
    this.southEastBoundary = southEastBoundary;
  }

  public int getZoom() {
    return zoom;
  }

  public void setZoom(int zoom) {
    this.zoom = zoom;
  }

  public Set<City> getCities() {
    return cities;
  }

  public void setCities(Set<City> cities) {
    this.cities = cities;
  }

  public void addCity(City city) {
    this.cities.add(city);
    city.getMaps().add(this);
  }

  public void removeCity(City city)
  {
    this.cities.remove(city);
    city.getMaps().remove(this);
  }

}
