package com.example.backend.entitiy;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

import com.example.backend.type.MapStatus;
import jakarta.persistence.*;

@Entity (name = "map")
public class Map {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "north_west_boundary")
  private Point2D.Float northWestBoundary;

  @Column(name = "south_west_boundary")
  private Point2D.Float southWestBoundary;

  @Column(name = "north_east_boundary")
  private Point2D.Float northEastBoundary;

  @Column(name = "south_east_boundary")
  private Point2D.Float southEastBoundary;

  @Column(name = "center")
  private Point2D.Float center;

  @Column(columnDefinition = "integer default 5")
  private int zoom;

  @OneToOne(fetch = FetchType.LAZY)
  private PDF pdf;

  private MapStatus status;

  @Column(columnDefinition = "integer default 1189")
  private int formatWidth;
  @Column(columnDefinition = "integer default 841")
  private int formatHeight;
  @Column(columnDefinition = "integer default 500")
  private int dpi;

  private String name;

  @ManyToMany
  @JoinTable(
      name = "map_city",
      joinColumns = @JoinColumn(name = "map_id"),
      inverseJoinColumns = @JoinColumn(name = "city_id"))
  private Set<City> cities = new HashSet<>();

  @OneToMany(mappedBy = "map")
  private Set<MapPoint> mapPoints = new HashSet<>();

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

  public void removeCity(City city) {
    this.cities.remove(city);
    city.getMaps().remove(this);
  }

  public Set<MapPoint> getMapPoints() {
    return mapPoints;
  }

  public void setMapPoints(Set<MapPoint> mapPoints) {
    this.mapPoints = mapPoints;
  }

  public Point2D.Float getCenter() {
    return center;
  }

  public void setCenter(Point2D.Float center) {
    this.center = center;
  }

  public PDF getPdf() {
    return pdf;
  }

  public void setPdf(PDF pdf) {
    this.pdf = pdf;
  }

  public MapStatus getStatus() {
    return status;
  }

  public void setStatus(MapStatus status) {
    this.status = status;
  }

  public int getFormatWidth() {
    return formatWidth;
  }

  public void setFormatWidth(int formatWidth) {
    this.formatWidth = formatWidth;
  }

  public int getFormatHeight() {
    return formatHeight;
  }

  public void setFormatHeight(int formatHeight) {
    this.formatHeight = formatHeight;
  }

  public int getDpi() {
    return dpi;
  }

  public void setDpi(int dpi) {
    this.dpi = dpi;
  }

  public String getName() { return name; }

  public void setName(String name) { this.name = name;  }
}
