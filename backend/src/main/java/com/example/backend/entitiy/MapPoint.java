package com.example.backend.entitiy;

import com.example.backend.type.Colorization;
import jakarta.persistence.*;

import java.awt.geom.Point2D;
import java.util.Set;

@Entity (name = "mapPoint")
public class MapPoint {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private Point2D.Float location;

    private String name;

    private Colorization color = Colorization.CITY;

    @ManyToMany(mappedBy = "neighbors")
    private Set<MapPoint> neighbor;

    @ManyToMany
    @JoinTable(name = "MAPPOINT_NEIGHBORS",
        joinColumns = {@JoinColumn(name = "MAPPOINT_ID")},
        inverseJoinColumns = {@JoinColumn(name = "NEIGHBOR_ID")})
    private Set<MapPoint> neighbors;
    @ManyToOne
    @JoinColumn(name = "map_id")
    private Map map;

    private boolean connectionIssue;
    private boolean hasTunnel;
    private boolean hasJoker;

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

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public Set<MapPoint> getNeighbor() {
        return neighbor;
    }

    public void setNeighbor(Set<MapPoint> neighbor) {
        this.neighbor = neighbor;
    }

    public Set<MapPoint> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(Set<MapPoint> neighbors) {
        this.neighbors = neighbors;
    }

    public boolean isHasTunnel() {
        return hasTunnel;
    }

    public void setHasTunnel(boolean hasTunnel) {
        this.hasTunnel = hasTunnel;
    }

    public boolean isHasJoker() {
        return hasJoker;
    }

    public void setHasJoker(boolean hasJoker) {
        this.hasJoker = hasJoker;
    }
}
