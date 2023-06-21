import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {MapDto} from '../dtos/map';
import {Observable} from "rxjs";
import {City} from "../dtos/city";

@Injectable({
  providedIn: 'root'
})
export class MapService {

  private baseURI = 'http://localhost:8080/api';
  constructor(private httpClient: HttpClient) { }


  /**
   * creates a map in the system
   *
   * @param map map to create in the system
   *
   * @returns an map representation of the registered trainer
   */
  createMap (map: MapDto): Observable<MapDto> {
    return this.httpClient.post<MapDto>(this.baseURI + '/maps', map);
  }


  /**
   * gets all timeslots belonging to own trainer
   *
   * @param id of map saved on server
   *
   * @returns an OverviewTimeslot representation of the saved timeslots
   */
  getCities (id: number): Observable<City[]> {
    return this.httpClient.get<City[]>(this.baseURI + '/maps/cities/' + id);
  }
}
