import {Point2D} from "./point2d";
import {Colorization} from "./colorization";

export class MapPointDto {
  id: number;
  name: string;
  location: Point2D;
  neighbors: number[];
  isDrawn = false;
  connectionIssue: boolean;
  hasTunnel: boolean;
  hasJoker: boolean;
  color: Colorization;

  constructor(id: number, name: string, location: Point2D, neighbors: number[], isDrawn: boolean, connectionIssue: boolean, hasTunnel: boolean, hasJoker: boolean, color: Colorization) {
    this.id = id;
    this.name = name;
    this.location = location;
    this.neighbors = neighbors;
    this.isDrawn = isDrawn;
    this.connectionIssue = connectionIssue;
    this.color = color;
    this.hasTunnel = hasTunnel;
    this.hasJoker = hasJoker;
  }
}
