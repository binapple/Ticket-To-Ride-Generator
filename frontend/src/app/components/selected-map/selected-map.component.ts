import { Component } from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {LatLng, latLng, LatLngBounds, Map, PanOptions, tileLayer, ZoomOptions} from "leaflet";

@Component({
  selector: 'app-selected-map',
  templateUrl: './selected-map.component.html',
  styleUrls: ['./selected-map.component.css']
})
export class SelectedMapComponent {

  //minimal map options
  optionsSpec: any = {
    layers: [{ url: 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', attribution: 'Open Street Map' }],
    zoom: 4,
    center: [ 48, 16 ]
  };

  //used for coordinates
  bounds:LatLngBounds = new LatLngBounds(new LatLng(0,0), new LatLng(0,0));
  northWest: LatLng = new LatLng(0,0);
  northEast: LatLng = new LatLng(0,0);
  southWest: LatLng = new LatLng(0,0);
  southEast: LatLng = new LatLng(0,0);

  // Leaflet options
  options = {
    layers: [ tileLayer(this.optionsSpec.layers[0].url, { attribution: this.optionsSpec.layers[0].attribution }) ],
    zoom: this.optionsSpec.zoom,
    zoomControl: false,
    dragging: false,
    touchZoom: false,
    doubleClickZoom: false,
    scrollWheelZoom: false,
    boxZoom: false,
    keyboard: false,
    tap: false,
    center: latLng(this.optionsSpec.center)
  };
  constructor(private route : ActivatedRoute) {
  }

  ngOnInit(): void {
    this.optionsSpec.zoom = this.route.snapshot.paramMap.get('z');
    const x = this.route.snapshot.paramMap.get('x');
    const y = this.route.snapshot.paramMap.get('y');
    this.optionsSpec.center = [ x, y ];
    this.options = {
      layers: [tileLayer(this.optionsSpec.layers[0].url, {attribution: this.optionsSpec.layers[0].attribution})],
      zoom: this.optionsSpec.zoom,
      zoomControl: false,
      dragging: false,
      touchZoom: false,
      doubleClickZoom: false,
      scrollWheelZoom: false,
      boxZoom: false,
      keyboard: false,
      tap: false,
      center: latLng(this.optionsSpec.center)
    };
  }

  onMapReady(map: Map) {
    this.bounds = map.getBounds();
    this.northWest = this.bounds.getNorthWest();
    this.northEast = this.bounds.getNorthEast();
    this.southWest = this.bounds.getSouthWest();
    this.southEast = this.bounds.getSouthEast();
    console.log("nW: " + this.northWest + " sE: " + this.southEast);
  }

}
