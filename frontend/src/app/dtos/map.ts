import {Point2D} from "./point2d";

export class MapDto {

  id: number;
  northWestBoundary: Point2D;
  southWestBoundary: Point2D;
  northEastBoundary: Point2D;
  southEastBoundary: Point2D;
  zoom: number;



  constructor(id: number, northWestBoundary: Point2D, southWestBoundary: Point2D, northEastBoundary: Point2D, southEastBoundary: Point2D, zoom: number) {
    this.northWestBoundary = northWestBoundary;
    this.southWestBoundary = southWestBoundary;
    this.northEastBoundary = northEastBoundary;
    this.southEastBoundary = southEastBoundary;
    this.zoom = zoom;
    this.id = id;
  }
}
