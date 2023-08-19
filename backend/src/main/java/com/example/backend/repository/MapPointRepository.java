package com.example.backend.repository;

import com.example.backend.entitiy.Map;
import com.example.backend.entitiy.MapPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MapPointRepository extends JpaRepository<MapPoint,Long> {

    /**
     * Find MapPoints by id of Map.
     *
     * @return associated MapPoints of the Map or null if no MapPoint of that Map is found
     */
    List<MapPoint> findMapPointsByMapId (long id);


}
