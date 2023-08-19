package com.example.backend.endpoint.dto;

import java.awt.geom.Point2D;

public class CreateMapDto {

  private Long id;
  private Point2D.Float northWestBoundary;

  private Point2D.Float southWestBoundary;

  private Point2D.Float northEastBoundary;

  private Point2D.Float southEastBoundary;

  private Point2D.Float center;

  private int zoom;

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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public int getZoom() {
    return zoom;
  }

  public void setZoom(int zoom) {
    this.zoom = zoom;
  }

  public Point2D.Float getCenter() {
    return center;
  }

  public void setCenter(Point2D.Float center) {
    this.center = center;
  }

  @Override
  public String toString() {
    return "CreateMapDto{" +
            "id=" + id +
            ", northWestBoundary=" + northWestBoundary +
            ", southWestBoundary=" + southWestBoundary +
            ", northEastBoundary=" + northEastBoundary +
            ", southEastBoundary=" + southEastBoundary +
            ", center=" + center +
            ", zoom=" + zoom +
            '}';
  }
}
