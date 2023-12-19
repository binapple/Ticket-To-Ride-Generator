import {Component, NgZone, OnInit} from '@angular/core';
import {MapPointDto} from "../../dtos/map-point";
import {Circle, circle, LatLng, latLng, polyline, tileLayer} from "leaflet";
import {ActivatedRoute, Router} from "@angular/router";
import {Point2D} from "../../dtos/point2d";
import {MapDto} from "../../dtos/map";
import {MapService} from "../../services/map.service";
import {Colorization} from "../../dtos/colorization";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {MapPointEditModalComponent} from "./map-point-edit-modal/map-point-edit-modal.component";
import {MapPointService} from "../../services/map-point.service";
import {dateComparator} from "@ng-bootstrap/ng-bootstrap/datepicker/datepicker-tools";

@Component({
  selector: 'app-colored-map',
  templateUrl: './colored-map.component.html',
  styleUrls: ['./colored-map.component.css']
})
export class ColoredMapComponent implements OnInit {

  mapPoints: MapPointDto[] = [];

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
  mappedMPs = new Map<number, MapPointDto>();

  //used for new connections
  cityMapPoints: MapPointDto[] = [];

  //used for loading indicator
  loadingGameBoard = false;
  creatingConnection = false;

  //progress bar
  currentStep = 3;
  progressWidth = 75;

  //drag mapPoints
  activeMapPoint: MapPointDto = {
    color: Colorization.CITY,
    connectionIssue: false,
    hasJoker: false,
    hasTunnel: false,
    id: 0,
    isDrawn: false,
    location: {
      x: 0,
      y: 0,
    },
    name: "",
    neighbors: []
  };

  constructor(private route: ActivatedRoute,
              private mapService: MapService,
              private mapPointService: MapPointService,
              private router: Router,
              private zone: NgZone,
              private  modalService: NgbModal) {
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
          this.drawMapPoints(false);
        }
      }

    });

  }


  onMapReady(mapL: L.Map): void {
    this.mapL = mapL;
    this.mapL.on('mouseup',function(e){
      mapL.removeEventListener('mousemove');
    })
  }

  drawMapPoints(drawOnlyCityCircles: boolean) {

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
              if (colorization !== Colorization.CITY) {
                const lineWeight = 10;

                if (destMp !== undefined) {
                  if (destMp.hasTunnel) {
                    this.layers.push(polyline(LatLngs, {color: '#000', interactive: false, weight: lineWeight + 2}));
                  }
                }
                this.layers.push(polyline(LatLngs, {color: color, interactive: false, weight: lineWeight,}));
                if (destMp !== undefined) {
                  if (destMp.hasJoker || mp.hasJoker) {
                    this.layers.push(polyline(LatLngs, {color: '#000', interactive: false, weight: lineWeight / 2}));
                  }
                }
              }
            }
          });



          const circleLatLng = new LatLng(mp.location.y, mp.location.x);
          if (mp.color !== Colorization.CITY) {
            if(!drawOnlyCityCircles) {
             const newCircle = circle(circleLatLng, 0, {color: '#d94e4e'}).on("dblclick",

                e => {
                  this.zone.run(() => this.circleClick(e.target, mp.id));
                }
              );

              newCircle.on({
                mousedown: e => {
                  this.zone.run(() => {this.mapL.on('mousemove', function (e) {
                    newCircle.setLatLng(e.latlng);
                  });
                   // this.circleDrag(mp.id, newCircle.getLatLng());
                  });
                },
                mouseup: event => {
                  this.zone.run( () => {
                    this.circleDrag(mp.id, newCircle.getLatLng());
                  })
                }
              });

              this.layers.push(newCircle);

            }

          } else {
            if(!drawOnlyCityCircles) {
              const radius = (Math.abs(this.savedMap.northEastBoundary.x - this.savedMap.southWestBoundary.x)) * 10 / 1189;
              this.layers.push(circle(circleLatLng, radius * 1000, {
                color: '#000000',
                fillOpacity: 100,
                fill: true,
                fillColor: '#92999f'
              }));
            }
            else {
              const radius = (Math.abs(this.savedMap.northEastBoundary.x - this.savedMap.southWestBoundary.x)) * 10 / 1189;
              const newCircle = circle(circleLatLng, radius * 1000,{color: '#d94e4e'}).on("dblclick",

                e => {
                  this.zone.run(() => this.cityClick(mp));
                }
              ).bindTooltip(mp.name);

              this.layers.push(newCircle);
            }
          }

          mp.isDrawn = true;
          this.mappedMPs.set(mp.id, mp);

      });
    }
  }

  private getMapPoints(id: number) {
    this.mapService.getMapPoints(id).subscribe({
      next: data => {
        this.mapPoints = data;
        this.drawMapPoints(false);
      }
    })
  }

  backButton() {
    this.router.navigate(['map/']);
  }

  circleClick(circle: Circle, id: number) {

    this.mapPointService.getMapPoint(id).subscribe({
      next: data => {
        const modal = this.modalService.open(MapPointEditModalComponent)
        modal.componentInstance.mapPoint = data;
        modal.closed.subscribe(
          (edited: boolean) => {
            if(edited) {
              this.getMapPoints(this.savedMap.id);
            }
          }
        );


      }
    });

  }

  circleDrag(id: number, latLng: LatLng) {
    this.mapPointService.getMapPoint(id).subscribe(
      {
        next: data => {
          this.activeMapPoint = data;

          this.activeMapPoint.location.x = latLng.lng;
          this.activeMapPoint.location.y = latLng.lat;
          this.mapPointService.updateMapPoint(this.activeMapPoint).subscribe(
            {
              next: data => {
                this.activeMapPoint = data;
                this.getMapPoints(this.savedMap.id);
              }
            }
          )
        }
      }
    )
  }

  createConnection() {
    if(!this.creatingConnection) {
      this.creatingConnection = true;
      this.drawMapPoints(true);
    }
    else {
      this.creatingConnection = false;
      this.cityMapPoints = [];
      this.drawMapPoints(false);
    }
  }

  private cityClick(mp: MapPointDto) {
    console.log(mp);
    if(this.cityMapPoints.length < 1)
    {
      this.cityMapPoints.push(mp);
    }
    else
    {
      this.cityMapPoints.push(mp);
      this.mapPointService.addConnection(this.cityMapPoints).subscribe(
        {
          next: data => {
            this.mapPoints.concat(data);
            this.cityMapPoints = [];
            this.creatingConnection = false;
            this.getMapPoints(this.savedMap.id);
          }
        }
      )
    }
  }

  getGameBoard() {
    this.loadingGameBoard = true;
    this.mapService.getGameBoard(this.savedMap.id).subscribe({
      next: data => {
        this.loadingGameBoard = false;
        this.router.navigate(['/map/gameBoard/'+this.savedMap.id], {state:{data: data}})
      }
    });

  }

  stepBack(){
    this.router.navigate(['map/selected/'+this.savedMap.id]);
  }
}
