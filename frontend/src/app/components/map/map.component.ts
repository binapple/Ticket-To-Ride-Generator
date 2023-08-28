import {Component} from '@angular/core';
import {latLng, LatLng, tileLayer,} from 'leaflet';
import * as L from 'leaflet';
import {Router} from "@angular/router";
import {MapService} from "../../services/map.service";
import {Point2D} from "../../dtos/point2d";
import {MapDto} from "../../dtos/map";


@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent {
  //minimal map options
  optionsSpec: any = {
    layers: [{ url: 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', attribution: 'Open Street Map' }],
    zoom: 4,
    center: [ 48, 16 ]
  };


  // Leaflet bindings
  zoom = this.optionsSpec.zoom;
  center = latLng(this.optionsSpec.center);
  options = {
    layers: [ tileLayer(this.optionsSpec.layers[0].url, { attribution: this.optionsSpec.layers[0].attribution }) ],
    zoom: this.optionsSpec.zoom,
    center: latLng(this.optionsSpec.center)
  };

  // Form bindings
  formZoom = this.zoom;
  zoomLevels = [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 ];
  lat = this.center.lat;
  lng = this.center.lng;

  leafletMap!: L.Map;
  emptyPoint: Point2D = new Point2D(0,0);
  savedMap = new MapDto(0, this.emptyPoint, this.emptyPoint, this.emptyPoint, this.emptyPoint, this.emptyPoint, 0);


  constructor(private router : Router,
              private mapService: MapService) {
  }

  // Output binding for center
  onCenterChange(center: LatLng) {
    setTimeout(() => {
      this.lat = center.lat;
      this.lng = center.lng;
    });
  }

  onZoomChange(zoom: number) {
    setTimeout(() => {
      this.formZoom = zoom;
    });
  }

  doApply() {
    this.center = latLng(this.lat, this.lng);
    this.zoom = this.formZoom;
  }



  selectedMap() {
    this.doApply();

    const bounds = this.leafletMap.getBounds();
    const northWest = bounds.getNorthWest();
    const northEast = bounds.getNorthEast();
    const southWest = bounds.getSouthWest();
    const southEast = bounds.getSouthEast();
    const zoom = this.leafletMap.getZoom();
    const center = this.leafletMap.getCenter();

    const northEastBoundary: Point2D = new Point2D(northEast.lng, northEast.lat);
    const northWestBoundary: Point2D = new Point2D(northWest.lng, northWest.lat);
    const southEastBoundary: Point2D = new Point2D(southEast.lng, southEast.lat);
    const southWestBoundary: Point2D = new Point2D(southWest.lng, southWest.lat);
    const centerPoint: Point2D = new Point2D(center.lng, center.lat);

    this.savedMap.center = centerPoint;
    this.savedMap.zoom = zoom;
    this.savedMap.northEastBoundary = northEastBoundary;
    this.savedMap.northWestBoundary = northWestBoundary;
    this.savedMap.southEastBoundary = southEastBoundary;
    this.savedMap.southWestBoundary = southWestBoundary;


    this.mapService.createMap(this.savedMap).subscribe({
      next: data => {
        this.savedMap = data;
        console.log(this.savedMap);
        this.router.navigate(['/map/selected/'+this.savedMap.id]);
      }
    });



  }

  onMapReady(map: L.Map) {
    this.leafletMap = map;
  }
}
