import {Point2D} from "./point2d";

export class City {
  id?: number;
  name: string;
  population: number;
  location: Point2D;

  constructor(name: string, population: number, location: Point2D) {
    this.name = name;
    this.population = population;
    this.location = location;
  }
}
