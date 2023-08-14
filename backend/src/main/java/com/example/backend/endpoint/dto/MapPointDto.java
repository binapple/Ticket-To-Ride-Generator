package com.example.backend.endpoint.dto;

import com.example.backend.type.Colorization;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.awt.geom.Point2D;
import java.util.List;


public class MapPointDto {

    private Long id;

    private String name;

    private Point2D.Float location;

    private Colorization color;

    private List<Long> neighbors;

    private boolean connectionIssue;

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

    public List<Long> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(List<Long> neighbors) {
        this.neighbors = neighbors;
    }

    public boolean isConnectionIssue() {
        return connectionIssue;
    }

    public void setConnectionIssue(boolean connectionIssue) {
        this.connectionIssue = connectionIssue;
    }

    public Colorization getColor() {
        return color;
    }

    public void setColor(Colorization color) {
        this.color = color;
    }
}
