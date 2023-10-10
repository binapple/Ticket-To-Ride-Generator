import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { MapComponent } from './components/map/map.component';
import { AppRoutingModule } from './app-routing.module';
import {LeafletModule} from "@asymmetrik/ngx-leaflet";
import {FormsModule} from "@angular/forms";
import { SelectedMapComponent } from './components/selected-map/selected-map.component';
import {HttpClientModule} from "@angular/common/http";
import {SearchCityPipe} from "./components/selected-map/search-city.pipe";
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ColoredMapComponent } from './components/colored-map/colored-map.component';
import { MapPointEditModalComponent } from './components/colored-map/map-point-edit-modal/map-point-edit-modal.component';


@NgModule({
  declarations: [
    AppComponent,
    MapComponent,
    SelectedMapComponent,
    SearchCityPipe,
    ColoredMapComponent,
    MapPointEditModalComponent

  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    LeafletModule,
    FormsModule,
    HttpClientModule,
    NgbModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
