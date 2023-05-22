import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { MapComponent } from './components/map/map.component';
import { AppRoutingModule } from './app-routing.module';
import {LeafletModule} from "@asymmetrik/ngx-leaflet";
import {FormsModule} from "@angular/forms";
import { SelectedMapComponent } from './components/selected-map/selected-map.component';


@NgModule({
  declarations: [
    AppComponent,
    MapComponent,
    SelectedMapComponent,

  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    LeafletModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
