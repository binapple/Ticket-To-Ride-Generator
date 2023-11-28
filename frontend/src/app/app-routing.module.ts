import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {MapComponent} from './components/map/map.component';
import {SelectedMapComponent} from "./components/selected-map/selected-map.component";
import {ColoredMapComponent} from "./components/colored-map/colored-map.component";
import {GameBoardComponent} from "./components/game-board/game-board.component";

const routes: Routes = [
  { path: '', redirectTo: '/map', pathMatch: 'full' },
  { path: 'map', children: [
      { path: '', component: MapComponent },
      { path: 'selected', children: [
          { path: ':id', component: SelectedMapComponent }
        ] },
      { path: 'colorized', children: [
          { path: ':id', component: ColoredMapComponent }
        ] },
      { path: 'gameBoard', children: [
          { path: ':id', component: GameBoardComponent }
        ] }
    ] }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
