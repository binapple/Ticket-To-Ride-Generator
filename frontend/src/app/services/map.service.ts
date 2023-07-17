import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {MapDto} from '../dtos/map';
import {Observable} from "rxjs";
import {City} from "../dtos/city";
import {MapPoint} from "../dtos/map-point";

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
   * gets all cities belonging to map
   *
   * @param id of map saved on server
   *
   * @returns a list of cites
   */
  getCities (id: number): Observable<City[]> {
    return this.httpClient.get<City[]>(this.baseURI + '/maps/cities/' + id);
  }

  /**
   * gets all towns belonging to map
   *
   * @param id of map saved on server
   *
   * @returns a list of cities (towns are also saved as cities)
   */
  getTowns(id: number):Observable<City[]> {
    return this.httpClient.get<City[]>(this.baseURI + '/maps/towns/' + id);
  }

  /**
   * sets current chosen cities as MapPoints. MapPoints are used for later calculations
   *
   * @param id of map saved on server
   * @param cities list of chosen cities
   *
   * @returns a list of mapPoints
   */
  createMapPoints(id: number, cities: City[]):Observable<MapPoint[]> {
    return this.httpClient.post<MapPoint[]>(this.baseURI + '/maps/selection/' +id,cities);
  }
}
