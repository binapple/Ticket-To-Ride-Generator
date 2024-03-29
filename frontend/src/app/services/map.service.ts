import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {MapDto} from '../dtos/map';
import {Observable} from "rxjs";
import {City} from "../dtos/city";
import {MapPointDto} from "../dtos/map-point";
import {PDFDto} from "../dtos/pdf";
import {StatusDto} from "../dtos/status";

@Injectable({
  providedIn: 'root'
})
export class MapService {

  private baseURI = 'http://127.0.0.1:8080/api';

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
   * loads all maps from the system
   *
   * @returns a mapDto List of the registered maps
   */
  getAllMaps(): Observable<MapDto[]> {
    return this.httpClient.get<MapDto[]>(this.baseURI + '/maps');
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
  showMapPoints(id: number, cities: City[]): Observable<MapPointDto[]> {
    return this.httpClient.post<MapPointDto[]>(this.baseURI + '/maps/selection/' + id, cities);
  }

  /**
   * colorizes the prior chosen MapPoints and saves them to the map
   *
   * @param id of map saved on server
   * @param cities list of chosen cities
   *
   * @returns a list of mapPoints
   */
  colorizeMapPoints(id: number, cities: City[]): Observable<MapPointDto[]> {
    return this.httpClient.post<MapPointDto[]>(this.baseURI + '/maps/colorization/' + id, cities);
  }

  /**
   * gets all MapPoints of a certain map
   *
   * @param id of map saved on server
   *
   * @returns a list of mapPoints corresponding to the map
   */
  getMapPoints(id: number): Observable<MapPointDto[]> {
    return this.httpClient.get<MapPointDto[]>(this.baseURI + '/maps/mapPoints/' + id);
  }

  /**
   *
   * creates the gameBoard and its ticketCards of a certain map
   *
   * @param id of map saved on server
   * @param DPI render resolution for OSM Map-Image
   *
   * @returns a PDFDto containing gameBoard and ticketCards of the map
   */
  createGameBoard(id: number, map: MapDto): Observable<PDFDto> {
    return this.httpClient.post<PDFDto>(this.baseURI + '/maps/gameBoard/create/'+id,map);
  }

  /**
   *
   * gets the gameBoard and its ticketCards of a certain map
   *
   * @param id of map saved on server
   *
   * @returns a PDFDto containing gameBoard and ticketCards of the map
   */
  getGameBoard(id: number): Observable<PDFDto> {
    return this.httpClient.get<PDFDto>(this.baseURI + '/maps/gameBoard/'+id);
  }


  /**
   * Gets the status of a Maps progress when creating tickets and gameboards
   *
   * @param id of the Map
   * @return a StatusDto that contains a progress message
   */
  getStatus(id:number): Observable<StatusDto> {
    return this.httpClient.get<StatusDto>(this.baseURI + '/maps/status/' +id);
  }
}
