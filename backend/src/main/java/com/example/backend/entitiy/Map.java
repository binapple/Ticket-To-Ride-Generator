package com.example.backend.entitiy;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

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

  @Column(name = "center")
  private Point2D.Float center;

  @Column(columnDefinition = "integer default 5")
  private int zoom;

  @Lob
  @Column(name = "gameBoard", columnDefinition = "BLOB")
  private byte[] gameBoard;

  @Lob
  @Column(name = "ticketCards", columnDefinition = "BLOB")
  private byte[] ticketCards;

  @ManyToMany
  @JoinTable(
      name = "map_city",
      joinColumns = @JoinColumn(name = "map_id"),
      inverseJoinColumns =  @JoinColumn(name = "city_id"))
  private Set<City> cities = new HashSet<>();

  @OneToMany (mappedBy = "map")
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

  public void removeCity(City city)
  {
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

  public byte[] getGameBoard() {
    return gameBoard;
  }

  public void setGameBoard(byte[] gameBoard) {
    this.gameBoard = gameBoard;
  }

  public byte[] getTicketCards() {
    return ticketCards;
  }

  public void setTicketCards(byte[] ticketCards) {
    this.ticketCards = ticketCards;
  }
}
