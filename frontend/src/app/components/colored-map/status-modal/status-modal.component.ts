import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {ProgressStatus, StatusDto} from "../../../dtos/status";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {interval, Subscription, switchMap} from "rxjs";
import {MapService} from "../../../services/map.service";

@Component({
  selector: 'app-status-modal',
  templateUrl: './status-modal.component.html',
  styleUrls: ['./status-modal.component.css']
})
export class StatusModalComponent implements OnDestroy, OnInit{

  statusDto: StatusDto = {
    message: 'Starting Process',
    progressStatus: ProgressStatus.GameBoardPDF
  }
  @Input() id = 0;
  progressValue = 0;

  maxProgress = 7;
  period = 6000;

  private progress: Subscription = new Subscription;

  constructor(private activeModal: NgbActiveModal,
              private mapService: MapService
              ) { }

  ngOnInit() {
    this.startUpdates();
  }

  startUpdates(): void {
    //stop previous subscription to prevent multiple simultaneous requests
    this.stopUpdates();

    this.progress = interval(this.period)
      .pipe(
        switchMap(() => this.mapService.getStatus(this.id))
      )
      .subscribe({
        next :data => {
          this.statusDto = data;
          switch (this.statusDto.progressStatus) {
            case ProgressStatus.TicketRender:
              this.progressValue = 1;
              break;
            case ProgressStatus.TicketSVG:
              this.progressValue = 2;

              break;
            case ProgressStatus.TicketPDF:
              this.progressValue = 3;

              break;
            case ProgressStatus.GameBoardRender:
              this.progressValue = 4;
              this.period = 10000;
              this.switchInterval();
              break;
            case ProgressStatus.GameBoardSVG:
              this.progressValue = 5;
              this.period = 60000;
              this.switchInterval();
              break;
            case ProgressStatus.GameBoardPDF:
              this.progressValue = 6;
              this.period = 60000;
              this.switchInterval();
              break;
            case ProgressStatus.Finished:
              this.progressValue = 7;
              break;
            case ProgressStatus.NotStarted:
              this.progressValue = 0;
              break;

          }
          console.log(this.statusDto);
          console.log(this.progressValue);
        }
      }

      );

  }

  switchInterval():void{
    this.stopUpdates();
    this.startUpdates();
  }

  stopUpdates(): void {
    this.progress.unsubscribe();
  }

  ngOnDestroy() {
    this.stopUpdates();
  }

  closeModal(): void {
    this.stopUpdates();
    this.activeModal.dismiss('Cross click');
  }
}
