import {Component, OnInit} from '@angular/core';
import {MapDto} from "../../dtos/map";
import {Point2D} from "../../dtos/point2d";
import {ActivatedRoute} from "@angular/router";
import {PDFDto} from "../../dtos/pdf";
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";
import {MapService} from "../../services/map.service";

@Component({
  selector: 'app-game-board',
  templateUrl: './game-board.component.html',
  styleUrls: ['./game-board.component.css']
})
export class GameBoardComponent implements OnInit{

  pdfDto: PDFDto = new PDFDto(0,undefined, undefined);
  emptyPoint: Point2D = new Point2D(0, 0);
  savedMap = new MapDto(0, this.emptyPoint, this.emptyPoint, this.emptyPoint, this.emptyPoint, this.emptyPoint, 0);
  gameBoardSource: any;
  ticketCardsSource: any;

  constructor(private route: ActivatedRoute,
              private sanitizer: DomSanitizer,
              private mapService: MapService
  )
  {}

  ngOnInit() {

    this.route.paramMap.subscribe(params => {

      const id = params.get('id');

      if(id !== null)
      {
        this.savedMap.id = Number(id);
      }
    });

    this.pdfDto = history.state.data;

    if(this.pdfDto !== undefined) {
      const gameBoardFileURL = 'data:application/pdf;base64,' + this.pdfDto.gameBoard;
      const ticketCardsFileURL = 'data:application/pdf;base64, '+ this.pdfDto.ticketCards;

      const gameBoardFileURLSanitized = this.sanitizer.bypassSecurityTrustResourceUrl(gameBoardFileURL);
      const ticketCardsFileURLSanitized = this.sanitizer.bypassSecurityTrustResourceUrl(ticketCardsFileURL);

      this.gameBoardSource=gameBoardFileURLSanitized;
      this.ticketCardsSource=ticketCardsFileURLSanitized;

    }
    else
    {
      this.loadPDFs()
    }



  }

  loadPDFs() {
    this.mapService.getGameBoard(this.savedMap.id).subscribe(
      {
        next: data => {

          console.log(data);
          if(data !== undefined)
          {
            this.pdfDto = data;
            const gameBoardFileURL = 'data:application/pdf;base64,' + this.pdfDto.gameBoard;
            const ticketCardsFileURL = 'data:application/pdf;base64, '+ this.pdfDto.ticketCards;

            const gameBoardFileURLSanitized = this.sanitizer.bypassSecurityTrustResourceUrl(gameBoardFileURL);
            const ticketCardsFileURLSanitized = this.sanitizer.bypassSecurityTrustResourceUrl(ticketCardsFileURL);

            this.gameBoardSource=gameBoardFileURLSanitized;
            this.ticketCardsSource=ticketCardsFileURLSanitized;
          }
        }
      }
    )
  }

}
