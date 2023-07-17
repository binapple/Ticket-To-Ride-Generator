import {Point2D} from "./point2d";

export class MapPoint {
  id: number;
  name: string;
  location: Point2D;
  neighbors: number[];
  isDrawn: boolean;
  connectionIssue: boolean;


  constructor(id: number, name: string, location: Point2D, neighbors: number[], isDrawn: boolean, connectionIssue: boolean) {
    this.id = id;
    this.name = name;
    this.location = location;
    this.neighbors = neighbors;
    this.isDrawn = isDrawn;
    this.connectionIssue = connectionIssue;
  }
}
