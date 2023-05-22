import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {MapComponent} from './components/map/map.component';
import {SelectedMapComponent} from "./components/selected-map/selected-map.component";

const routes: Routes = [
  { path: 'map', children: [
      { path: '', component: MapComponent },
      { path: 'selected', component: SelectedMapComponent }
    ] }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
