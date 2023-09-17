import {Component, NgZone, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Circle, circle, LatLng, latLng, Map as map, polyline, tileLayer,} from "leaflet";
import {MapService} from "../../services/map.service";
import {MapDto} from '../../dtos/map';
import {Point2D} from "../../dtos/point2d";
import {City} from "../../dtos/city";
import {MapPoint} from "../../dtos/map-point";
import {Colorization} from "../../dtos/colorization";


@Component({
  selector: 'app-selected-map',
  templateUrl: './selected-map.component.html',
  styleUrls: ['./selected-map.component.css']
})
export class SelectedMapComponent implements OnInit{

  //minimal map options
  optionsSpec: any = {
    layers: [{ url: 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', attribution: 'Open Street Map' }],
    zoom: 4,
    center: [ 48, 16 ]
  };
  mapL!: L.Map;

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
  savedMap = new MapDto(0, this.emptyPoint, this.emptyPoint, this.emptyPoint, this.emptyPoint, this.emptyPoint, 0);
  mapLoaded = false;
  cities: City[] = [];
  selectedCities: City[] = [];
  mapPoints: MapPoint[] = [];
  mappedMPs = new Map<number,MapPoint>();
  layers = [
    circle([ 46.95, -122 ], { radius: 0 }),
    polyline([[0,0]]),
  ];
  searchCity: City = new City('',0,new Point2D(0,0));
  citiesLoaded = false;

  constructor(private route : ActivatedRoute,
              private mapService : MapService,
              private router: Router,
              private zone: NgZone) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {

      const id = params.get('id');

      if(id != null)
      {
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
      }
    });
  }

  onMapReady(mapL: map) {
    this.mapL = mapL;
    this.mapLoaded = true;


  }

  backButton() {
    this.router.navigate(['map/']);
  }

  loadCities() {
    if (this.savedMap.id != null) {

      this.mapService.getMapPoints(this.savedMap.id).subscribe(
        {
          next: mpData => {
            this.mapPoints = mpData;

              this.mapService.getCities(this.savedMap.id).subscribe({
                next: data => {
                  this.cities = data;
                  if(this.mapPoints.length === 0) {
                    this.selectedCities = this.cities.slice(0, 50);
                    this.cities = this.cities.slice(50, this.cities.length + 1);
                  }
                  else
                  {
                    this.mapPoints.forEach(mp => {
                      if(mp.color === Colorization.CITY) {
                        const selectedCity = this.cities.find(city=>city.name == mp.name);
                        if(selectedCity !== undefined)
                        {
                          this.selectedCities.push(selectedCity);
                          const index = this.cities.indexOf(selectedCity);
                          if (index > -1) {
                            this.cities.splice(index, 1);
                          }
                        }
                      }
                    })
                  }
                  this.addCitiesToMap();
                  this.loadMapPoints();
                  this.citiesLoaded = true;
                }
              });


          }
        }
      );


    }
  }

  private addCitiesToMap()
  {
    this.selectedCities.forEach(city =>
    {
      const location = city.location;
      const latLng = new LatLng(location.y, location.x);

      this.layers.push(circle(latLng,5000, {color:'#d94e4e'}).on("dblclick",

        e => {
          this.zone.run(() => this.circleClick(e.target));
        }

      ).bindTooltip(city.name,{permanent: true, direction: "auto"}));
    });

    this.cities.forEach(city =>
      {
        const location = city.location;
        const latLng = new LatLng(location.y, location.x);

        this.layers.push(circle(latLng,0, {color:'#d94e4e'}).on("dblclick",

          e => {
            this.zone.run(() => this.circleClick(e.target));
          }

        ).bindTooltip(city.name,{permanent: false, direction: "auto"}));
      });
  }

  circleClick(circle: Circle)
  {
      const circleCityName = circle.getTooltip()?.getContent();
      if(circleCityName != undefined)
      {
        let toBeChanged = this.selectedCities.find(c => c.name === circleCityName.toString());

        if (toBeChanged != undefined) {
          this.removeCity(toBeChanged);
        } else {
          toBeChanged = this.cities.find(c => c.name === circleCityName.toString());

          if (toBeChanged != undefined) {
            this.addCity(toBeChanged);
          }
        }
      }
  }

  removeCity(city: City) {
    const index = this.selectedCities.indexOf(city);
    if(index > -1) {
      this.selectedCities.splice(index,1);
    }
    this.cities.push(city);

    this.layers = [];
    this.addCitiesToMap();
    this.loadMapPoints();
  }


  addCity(city: City) {
    const index = this.cities.indexOf(city);
    if(index > -1) {
      this.cities.splice(index,1);
    }
    this.selectedCities.push(city);

    this.layers = [];
    this.addCitiesToMap();
    this.loadMapPoints();
  }

  loadTowns() {
    if (this.savedMap.id != null) {
      this.mapService.getTowns(this.savedMap.id).subscribe({
        next: data => {
          this.cities = data;
          this.selectedCities = this.cities.slice(0,50);
          this.cities = this.cities.slice(50, this.cities.length+1);
          this.layers = [];
          this.addCitiesToMap();
          this.loadMapPoints();
        }
      })
    }
  }

  loadMapPoints() {
    if (this.savedMap.id != null && this.selectedCities.length > 0)
    {
      this.mapService.showMapPoints(this.savedMap.id, this.selectedCities).subscribe({
          next: data => {
            this.mapPoints = data;

            this.mapPoints.forEach(mp =>
            {
              this.mappedMPs.set(mp.id,mp);
            })

            this.mapPoints.forEach(mp => {
              const startPoint = mp.location;
              let destPoint = new Point2D(0,0);
              mp.neighbors.forEach( i => {
                const destMp = this.mappedMPs.get(i);
                if (!destMp?.isDrawn)
                {

                  if (destMp !== undefined) {
                    destPoint = destMp.location;
                  }

                const latLngDestPoint = new LatLng(destPoint.y, destPoint.x);
                const latLngstartPoint = new LatLng(startPoint.y, startPoint.x);
                const LatLngs = [latLngDestPoint, latLngstartPoint];
                let color = '#808080';
                if (mp.connectionIssue && destMp?.connectionIssue) {
                  color = '#ff0000';
                }
                this.layers.push(polyline(LatLngs, {color: color,interactive:false}));

              }
                }
              )

              mp.isDrawn = true;

            })
            console.log(this.layers);
          }
        }
      )
    }
  }

  colorMap() {
    this.mapService.colorizeMapPoints(this.savedMap.id, this.selectedCities).subscribe({
      next: data => {
        this.mapPoints = data;
        this.router.navigate(['/map/colorized/'+this.savedMap.id], {state:{data: this.mapPoints}})
      }
    });

  }
}
