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

  constructor(private httpClient: HttpClient) {
  }


  /**
   * creates a map in the system
   *
   * @param map map to create in the system
   *
   * @returns an mapDto representation of the registered map
   */
  createMap(map: MapDto): Observable<MapDto> {
    return this.httpClient.post<MapDto>(this.baseURI + '/maps', map);
  }

  /**
   * loads a map from the system
   *
   * @param id of map to fetch from the system
   *
   * @returns an mapDto representation of the registered map
   */
  getMap(id: number): Observable<MapDto> {
    return this.httpClient.get<MapDto>(this.baseURI + '/maps/' + id);
  }

  /**
   * gets all cities belonging to map
   *
   * @param id of map saved on server
   *
   * @returns a list of cites
   */
  getCities(id: number): Observable<City[]> {
    return this.httpClient.get<City[]>(this.baseURI + '/maps/cities/' + id);
  }

  /**
   * gets all towns belonging to map
   *
   * @param id of map saved on server
   *
   * @returns a list of cities (towns are also saved as cities)
   */
  getTowns(id: number): Observable<City[]> {
    return this.httpClient.get<City[]>(this.baseURI + '/maps/towns/' + id);
  }

  /**
   * shows current chosen cities as MapPoints. MapPoints are used for later calculations
   *
   * @param id of map saved on server
   * @param cities list of chosen cities
   *
   * @returns a list of mapPoints
   */
  showMapPoints(id: number, cities: City[]): Observable<MapPoint[]> {
    return this.httpClient.post<MapPoint[]>(this.baseURI + '/maps/selection/' + id, cities);
  }

  /**
   * colorizes the prior chosen MapPoints and saves them to the map
   *
   * @param id of map saved on server
   * @param cities list of chosen cities
   *
   * @returns a list of mapPoints
   */
  colorizeMapPoints(id: number, cities: City[]): Observable<MapPoint[]> {
    return this.httpClient.post<MapPoint[]>(this.baseURI + '/maps/colorization/' + id, cities);
  }

  /**
   * gets all MapPoints of a certain map
   *
   * @param id of map saved on server
   *
   * @returns a list of mapPoints corresponding to the map
   */
  getMapPoints(id: number): Observable<MapPoint[]> {
    return this.httpClient.get<MapPoint[]>(this.baseURI + '/maps/mapPoints/' + id);
  }
}
