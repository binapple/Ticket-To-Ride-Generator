import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {MapPointDto} from "../dtos/map-point";

@Injectable({
  providedIn: 'root'
})
export class MapPointService {
  private baseURI = 'http://34.125.122.35:8080/api';

  constructor(private httpClient: HttpClient) {
  }

  /**
   * loads a mapPoint from the backend
   *
   * @param id of mapPoint to fetch from the backend
   *
   * @returns mapPointDto representation of the found MapPoint
   */
  getMapPoint(id: number): Observable<MapPointDto> {
    return this.httpClient.get<MapPointDto>(this.baseURI + '/mapPoints/' + id);
  }

  /**
   * Deletes a mapPoint from the backend
   *
   * @param id of mapPoint to be deleted from the backend
   */
  deleteConnection(id: number): Observable<void> {
    return this.httpClient.delete<void>(this.baseURI + '/mapPoints/' + id);
  }

  /**
   * Creates a new Connection and thus new MapPoints between two existing city MapPoints
   * New Connections are always colorless and have no tunnels or jokers
   *
   * @param cityMapPoints List of CityMapPoints
   * @return a List of all newly created MapPoints
   */
  addConnection(cityMapPoints: MapPointDto[]): Observable<MapPointDto[]> {
    return this.httpClient.put<MapPointDto[]>(this.baseURI + '/mapPoints', cityMapPoints);
  }

  /**
   * updates one MapPointDto from the backend
   *
   * @param mapPointDto representation of a MapPoint to update
   *
   * @returns MapPointDto with updated values
   */
  updateMapPoint(mapPointDto: MapPointDto): Observable<MapPointDto> {
    return this.httpClient.put<MapPointDto>(this.baseURI + '/mapPoints/' + mapPointDto.id, mapPointDto);
  }


}
