package com.example.backend.entitiy;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Set;

@Entity (name = "mapPoint")
public class MapPoint {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private Point2D.Float location;

    private String name;

    @OneToMany
    private List<MapPoint> neighbors;

    private boolean connectionIssue;

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

    public List<MapPoint> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(List<MapPoint> neighbors) {
        this.neighbors = neighbors;
    }

    public boolean isConnectionIssue() {
        return connectionIssue;
    }

    public void setConnectionIssue(boolean connectionIssue) {
        this.connectionIssue = connectionIssue;
    }
}
