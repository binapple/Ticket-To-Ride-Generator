import {Component, NgZone} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {
  Circle,
  circle, control,
  LatLng,
  latLng,
  LatLngBounds,
  Layer,
  Map,
  marker,
  PanOptions,
  polygon,
  tileLayer,
  ZoomOptions
} from "leaflet";
import {MapService} from "../../services/map.service";
import {MapDto} from '../../dtos/map';
import {dateTimestampProvider} from "rxjs/internal/scheduler/dateTimestampProvider";
import {Point2D} from "../../dtos/point2d";
import {City} from "../../dtos/city";
import layers = control.layers;

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
  zoom = 0;


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

  emptyPoint: Point2D = new Point2D(0,0);
  savedMap = new MapDto(this.emptyPoint, this.emptyPoint, this.emptyPoint, this.emptyPoint, 0);
  mapLoaded = false;
  cities: City[] = [];
  selectedCities: City[] = [];
  layers = [
    circle([ 46.95, -122 ], { radius: 0 }),
  ];
  searchCity: City = new City('',0,new Point2D(0,0));

  constructor(private route : ActivatedRoute,
              private mapService : MapService,
              private router: Router,
              private zone: NgZone) {
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
    this.zoom = map.getZoom();

    this.mapLoaded = true;

  }

  backButton() {
    this.router.navigate(['map/']);
  }

  loadCities() {
    const northEastBoundary: Point2D = new Point2D(this.northEast.lng, this.northEast.lat);
    const northWestBoundary: Point2D = new Point2D(this.northWest.lng, this.northWest.lat);
    const southEastBoundary: Point2D = new Point2D(this.southEast.lng, this.southEast.lat);
    const southWestBoundary: Point2D = new Point2D(this.southWest.lng, this.southWest.lat);

    const newMap: MapDto = new MapDto(northWestBoundary,southWestBoundary,northEastBoundary,southEastBoundary,this.zoom);


    this.mapService.createMap(newMap).subscribe( {
        next: data => {
          this.savedMap = data;
          if (this.savedMap.id != null) {
            this.mapService.getCities(this.savedMap.id).subscribe({
              next: data => {
                this.cities = data;
                console.log(this.cities.length);
                this.selectedCities = this.cities.slice(0,50);
                this.cities = this.cities.slice(50, this.cities.length+1);
                console.log(this.cities.length);
                this.addCitiesToMap(this.selectedCities);

              }
            })
          }
        }
      }
    );

  }

  private addCitiesToMap(array: City[])
  {
    array.forEach(city =>
    {
      const location = city.location;
      const latLng = new LatLng(location.y, location.x);

      this.layers.push(circle(latLng,5000, {color:'#d94e4e'}).on("dblclick",

        e => {
          this.zone.run(() => this.circleClick(e.target));
        }

      ).bindTooltip(city.name,{permanent: true, direction: "auto"}));
    });
  }

  circleClick(circle: Circle)
  {
      const circleCityName = circle.getTooltip()?.getContent();
      if(circleCityName != undefined)
      {
        const toBeRemoved = this.selectedCities.find(c => c.name === circleCityName.toString())

        if (toBeRemoved != undefined) {
          this.removeCity(toBeRemoved);
        }
      }
    const index = this.layers.indexOf(circle);
    if(index > -1) {
      this.layers.splice(index,1);
    }
  }

  removeCity(city: City) {
    const index = this.selectedCities.indexOf(city);
    if(index > -1) {
      this.selectedCities.splice(index,1);
    }
    this.cities.push(city);

    this.layers = [];
    this.addCitiesToMap(this.selectedCities);
  }


  addCity(city: City) {
    const index = this.cities.indexOf(city);
    if(index > -1) {
      this.cities.splice(index,1);
    }
    this.selectedCities.push(city);

    this.layers = [];
    this.addCitiesToMap(this.selectedCities);
  }
}
