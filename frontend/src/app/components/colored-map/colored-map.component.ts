import {Component, OnInit} from '@angular/core';
import {MapPoint} from "../../dtos/map-point";
import {circle, LatLng, latLng, polyline, tileLayer} from "leaflet";
import {ActivatedRoute} from "@angular/router";
import {Point2D} from "../../dtos/point2d";
import {MapDto} from "../../dtos/map";
import {MapService} from "../../services/map.service";
import {Colorization} from "../../dtos/colorization";

@Component({
  selector: 'app-colored-map',
  templateUrl: './colored-map.component.html',
  styleUrls: ['./colored-map.component.css']
})
export class ColoredMapComponent implements OnInit {

  mapPoints: MapPoint[] = [];

  //minimal map options
  optionsSpec: any = {
    layers: [{url: 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', attribution: 'Open Street Map'}],
    zoom: 4,
    center: [48, 16]
  };
  mapL!: L.Map;

  // Leaflet options
  options = {
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

  emptyPoint: Point2D = new Point2D(0, 0);
  savedMap = new MapDto(0, this.emptyPoint, this.emptyPoint, this.emptyPoint, this.emptyPoint, this.emptyPoint, 0);
  layers = [
    circle([46.95, -122], {radius: 0}),
    polyline([[0, 0]]),
  ];
  mappedMPs = new Map<number, MapPoint>();


  constructor(private route: ActivatedRoute,
              private mapService: MapService) {
  }

  ngOnInit() {
    this.mapPoints = history.state.data;
    this.route.paramMap.subscribe(params => {

      const id = params.get('id');

      if (id != null) {
        this.savedMap.id = Number(id);
        this.mapService.getMap(this.savedMap.id).subscribe({
          next: data => {
            this.savedMap = data;
            this.optionsSpec.center = [this.savedMap.center.y, this.savedMap.center.x];
            this.optionsSpec.zoom = this.savedMap.zoom;
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
            this.mapL.setView(this.options.center, this.options.zoom);
          }
        })
        if (this.mapPoints == undefined) {
          this.getMapPoints(Number(id));
        } else {
          this.drawMapPoints();
        }
      }

    });

  }


  onMapReady(mapL: L.Map): void {
    this.mapL = mapL;
  }

  drawMapPoints() {

    console.log(this.savedMap);
    console.log(this.mapPoints);
    console.log(this.layers);
    this.layers = [];

    if (this.savedMap.id != null) {
      this.mapPoints.forEach(mp => {
        mp.isDrawn = false;
        this.mappedMPs.set(mp.id, mp);
      });

      this.mapPoints.forEach(mp => {
        const startPoint = mp.location;
        let destPoint = new Point2D(0, 0);
        mp.neighbors.forEach(i => {
            const destMp = this.mappedMPs.get(i);
            if (!destMp?.isDrawn) {
              let colorization = mp.color;
              if (destMp !== undefined) {
                destPoint = destMp.location;
                colorization = destMp.color;
              }

              const latLngDestPoint = new LatLng(destPoint.y, destPoint.x);
              const latLngstartPoint = new LatLng(startPoint.y, startPoint.x);
              const LatLngs = [latLngDestPoint, latLngstartPoint];

              let color = '#ffffff';

              switch (colorization) {
                case Colorization.CITY:
                  color = '#ffffff';
                  break;
                case Colorization.COLORLESS:
                  color = '#92999f';
                  break;
                case Colorization.BLUE:
                  color = '#00a7e9';
                  break;
                case Colorization.RED:
                  color = '#d03030';
                  break;
                case Colorization.GREEN:
                  color = '#88c34d';
                  break;
                case Colorization.YELLOW:
                  color = '#fee64e';
                  break;
                case Colorization.ORANGE:
                  color = '#e48f3c';
                  break;
                case Colorization.BLACK:
                  color = '#3c525d';
                  break;
                case Colorization.PINK:
                  color = '#bf87b6';
                  break;
                case Colorization.WHITE:
                  color = '#e2e1e7';
                  break;
                default:
                  break;
              }


              this.layers.push(polyline(LatLngs, {color: color, interactive: false}));
            }
          });
        const circleLatLng = new LatLng(mp.location.y, mp.location.x);
        this.layers.push(circle(circleLatLng, 0,{color: '#ff0000'}));
        mp.isDrawn = true;
        this.mappedMPs.set(mp.id, mp);
      });
        console.log(this.layers);
    }
  }

  private getMapPoints(id: number) {
    this.mapService.getMapPoints(id).subscribe({
      next: data => {
        this.mapPoints = data;
        this.drawMapPoints();
      }
    })
  }
}
