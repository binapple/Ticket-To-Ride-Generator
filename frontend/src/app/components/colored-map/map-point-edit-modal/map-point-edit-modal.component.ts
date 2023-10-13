import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {MapPointDto} from "../../../dtos/map-point";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Colorization} from "../../../dtos/colorization";
import {MapPointService} from "../../../services/map-point.service";

@Component({
  selector: 'app-map-point-edit-modal',
  templateUrl: './map-point-edit-modal.component.html',
  styleUrls: ['./map-point-edit-modal.component.css']
})
export class MapPointEditModalComponent {



  @Input() mapPoint: MapPointDto = {
    color: Colorization.CITY,
    connectionIssue: false,
    hasJoker: false,
    hasTunnel: false,
    id: 0,
    isDrawn: false,
    location: {
      x: 0,
      y: 0,
    },
    name: "",
    neighbors: []

  }


  availableColors = [
    { value: Colorization.COLORLESS, label: 'COLORLESS', disabled: false },
    { value: Colorization.BLUE, label: 'BLUE', disabled: false },
    { value: Colorization.RED, label: 'RED', disabled: false },
    { value: Colorization.GREEN, label: 'GREEN', disabled: false },
    { value: Colorization.YELLOW, label: 'YELLOW', disabled: false },
    { value: Colorization.ORANGE, label: 'ORANGE', disabled: false },
    { value: Colorization.BLACK, label: 'BLACK', disabled: false },
    { value: Colorization.PINK, label: 'PINK', disabled: false },
    { value: Colorization.WHITE, label: 'WHITE', disabled: false },
  ];

 constructor(public activeModal: NgbActiveModal,
             private mapPointService: MapPointService,) {
 }




  closeModal() {
    this.activeModal.dismiss('Close click');
  }

  saveChanges() {


   this.mapPointService.updateMapPoint(this.mapPoint).subscribe({
     next: data => {
       this.activeModal.close(true);
    }
   });
  }

  protected readonly Colorization = Colorization;

  deleteConnection() {
    this.mapPointService.deleteConnection(this.mapPoint.id).subscribe({
      next: data => {
        this.activeModal.close(true);
      }
    })
  }
}
