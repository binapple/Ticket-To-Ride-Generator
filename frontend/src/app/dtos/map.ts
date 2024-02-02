import {Point2D} from "./point2d";
import {MapStatus} from "./map-status";

export class MapDto {

  id: number;
  northWestBoundary: Point2D;
  southWestBoundary: Point2D;
  northEastBoundary: Point2D;
  southEastBoundary: Point2D;
  center: Point2D;
  zoom: number;
  status: MapStatus;
  formatWidth: number;
  formatHeight: number;
  dpi: number;
  name: string;



  constructor(id: number, northWestBoundary: Point2D, southWestBoundary: Point2D, northEastBoundary: Point2D, southEastBoundary: Point2D, center: Point2D, zoom: number, status: MapStatus, formatWidth: number, formatHeight: number, dpi: number) {
    this.northWestBoundary = northWestBoundary;
    this.southWestBoundary = southWestBoundary;
    this.northEastBoundary = northEastBoundary;
    this.southEastBoundary = southEastBoundary;
    this.center = center;
    this.zoom = zoom;
    this.id = id;
    this.status = status;
    this.formatHeight = formatHeight;
    this.formatWidth = formatWidth;
    this.dpi = dpi;
    this.name = "Map"+this.id;
  }
}
