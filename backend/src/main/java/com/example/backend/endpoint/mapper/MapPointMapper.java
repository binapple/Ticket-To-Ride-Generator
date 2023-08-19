package com.example.backend.endpoint.mapper;


import com.example.backend.endpoint.dto.MapPointDto;
import com.example.backend.entitiy.MapPoint;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MapPointMapper {

    /**
     * Maps a list of MapPoints to a list of MapPointDtos.
     *
     * @param mapPoints a list of MapPoints to map
     * @return list of MapPointDtos
     */
    default List<MapPointDto> mapPointListToMapPointDtoListCustom(List<MapPoint> mapPoints) {

        List<MapPointDto> toReturn = new ArrayList<>();
        for (MapPoint m : mapPoints)
        {
            MapPointDto toAdd = new MapPointDto();
            toAdd.setConnectionIssue(m.isConnectionIssue());
            toAdd.setId(m.getId());
            toAdd.setLocation(m.getLocation());
            toAdd.setName(m.getName());
            toAdd.setColor(m.getColor());
            List<Long> neighborIds = new ArrayList<>();
            for (MapPoint n: m.getNeighbors()
                 ) {
                neighborIds.add(n.getId());
            }
            toAdd.setNeighbors(neighborIds);
            toReturn.add(toAdd);
        }
        return toReturn;
    }
}
