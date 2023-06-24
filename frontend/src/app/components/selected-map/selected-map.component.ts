import {Component, NgZone} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {
  Circle,
  circle,
  LatLng,
  latLng,
  Map,
  tileLayer,
} from "leaflet";
import {MapService} from "../../services/map.service";
import {MapDto} from '../../dtos/map';
import {Point2D} from "../../dtos/point2d";
import {City} from "../../dtos/city";


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
  savedMap = new MapDto(0, this.emptyPoint, this.emptyPoint, this.emptyPoint, this.emptyPoint, 0);
  mapLoaded = false;
  cities: City[] = [];
  selectedCities: City[] = [];
  layers = [
    circle([ 46.95, -122 ], { radius: 0 }),
  ];
  searchCity: City = new City('',0,new Point2D(0,0));
  citiesLoaded = false;

  constructor(private route : ActivatedRoute,
              private mapService : MapService,
              private router: Router,
              private zone: NgZone) {
  }

  ngOnInit(): void {
    let x;
    let y;
    this.route.queryParams.subscribe(params => {
      this.optionsSpec.zoom = JSON.parse(params["z"]);
      x = JSON.parse(params["x"]);
      y = JSON.parse(params["y"]);
    })

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
    const bounds = map.getBounds();
    const northWest = bounds.getNorthWest();
    const northEast = bounds.getNorthEast();
    const southWest = bounds.getSouthWest();
    const southEast = bounds.getSouthEast();
    const zoom = map.getZoom();

    const northEastBoundary: Point2D = new Point2D(northEast.lng, northEast.lat);
    const northWestBoundary: Point2D = new Point2D(northWest.lng, northWest.lat);
    const southEastBoundary: Point2D = new Point2D(southEast.lng, southEast.lat);
    const southWestBoundary: Point2D = new Point2D(southWest.lng, southWest.lat);


    this.savedMap.zoom = zoom;
    this.savedMap.northEastBoundary = northEastBoundary;
    this.savedMap.northWestBoundary = northWestBoundary;
    this.savedMap.southEastBoundary = southEastBoundary;
    this.savedMap.southWestBoundary = southWestBoundary;


    this.mapService.createMap(this.savedMap).subscribe({
      next: data => {
        this.savedMap = data;
        console.log(this.savedMap);
        this.mapLoaded = true;
      }
    });



  }

  backButton() {
    this.router.navigate(['map/']);
  }

  loadCities() {
    if (this.savedMap.id != null) {
      this.mapService.getCities(this.savedMap.id).subscribe({
        next: data => {
          this.cities = data;
          this.selectedCities = this.cities.slice(0, 50);
          this.cities = this.cities.slice(50, this.cities.length + 1);
          this.addCitiesToMap(this.selectedCities);
          this.citiesLoaded = true;
        }
      })
    }
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

  loadTowns() {
    if (this.savedMap.id != null) {
      this.mapService.getTowns(this.savedMap.id).subscribe({
        next: data => {
          this.cities = data;
          this.selectedCities = this.cities.slice(0,50);
          this.cities = this.cities.slice(50, this.cities.length+1);
          this.layers = [];
          this.addCitiesToMap(this.selectedCities);
        }
      })
    }
  }
}
