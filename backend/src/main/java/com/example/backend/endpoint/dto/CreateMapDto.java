package com.example.backend.endpoint.dto;

import java.awt.geom.Point2D;

import com.example.backend.type.MapStatus;

public class CreateMapDto {

  private Long id;
  private Point2D.Float northWestBoundary;

  private Point2D.Float southWestBoundary;

  private Point2D.Float northEastBoundary;

  private Point2D.Float southEastBoundary;

  private Point2D.Float center;

  private int zoom;

  private MapStatus status;

  private int formatWidth;

  private int formatHeight;

  private int dpi;

  private String name;

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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
        ", status=" + status +
        ", formatWidth=" + formatWidth +
        ", formatHeight=" + formatHeight +
        ", dpi=" + dpi +
        ", name='" + name + '\'' +
        '}';
  }
}
