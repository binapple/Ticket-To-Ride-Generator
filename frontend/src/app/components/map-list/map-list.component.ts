import {Component, OnInit} from '@angular/core';
import {MapDto} from "../../dtos/map";
import {MapService} from "../../services/map.service";
import {MapStatus} from "../../dtos/map-status";
import {latLng, LatLngBounds, Layer, tileLayer} from "leaflet";
import * as L from 'leaflet';

@Component({
  selector: 'app-map-list',
  templateUrl: './map-list.component.html',
  styleUrls: ['./map-list.component.css']
})
export class MapListComponent implements OnInit{

  savedMaps: MapDto[] = [];

  //leaflet
  options = {
    zoomControl: false,
    dragging: false,
    touchZoom: false,
    doubleClickZoom: false,
    scrollWheelZoom: false,
    boxZoom: false,
    keyboard: false,
    tap: false,
  }

  constructor(private mapService: MapService) {
  }
  ngOnInit() {
    this.mapService.getAllMaps().subscribe(
      {
        next: data =>{
          this.savedMaps = data;
        }
      }
    )
  }

  convertStatusToText(status: MapStatus):string{
    switch (status)
    {
      case MapStatus.COLORIZED:
        return "Cities selected";
      case MapStatus.CREATED:
        return "Gameboard created";
      case MapStatus.SELECTED:
        return "Map selected";
    }
  }

  getBounds(map: MapDto) {
    const northWest = map.northWestBoundary;
    const southEast = map.southEastBoundary;

    const bounds = L.latLngBounds(latLng(northWest.y,northWest.x),latLng(southEast.y, southEast.x));

    return bounds;
  }

  protected readonly tileLayer = tileLayer;
  protected readonly MapStatus = MapStatus;
}
