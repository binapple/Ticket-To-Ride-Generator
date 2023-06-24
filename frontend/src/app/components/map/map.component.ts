import {Component} from '@angular/core';
import {latLng, LatLng, tileLayer,} from 'leaflet';
import {NavigationExtras, Router} from "@angular/router";
import {MapService} from "../../services/map.service";


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

    const navigationExtras: NavigationExtras = {
      queryParams: {
        "z": JSON.stringify(this.zoom),
        "x": JSON.stringify(this.center.lat),
        "y": JSON.stringify(this.center.lng),
      }
    }

    this.router.navigate(['/map/selected'], navigationExtras);

  }
}
