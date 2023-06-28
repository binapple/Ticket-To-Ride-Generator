package com.example.backend.entitiy;

import jakarta.persistence.*;

import java.awt.geom.Point2D;

@Entity (name = "mapPoint")
public class MapPoint {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private Point2D.Float location;

    private String name;

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
}
