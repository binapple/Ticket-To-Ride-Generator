import {Component, OnInit} from '@angular/core';
import {MapDto} from "../../dtos/map";
import {Point2D} from "../../dtos/point2d";
import {ActivatedRoute, Router} from "@angular/router";
import {PDFDto} from "../../dtos/pdf";
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";
import {MapService} from "../../services/map.service";
import {Browser} from "leaflet";
import win = Browser.win;

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

  //progress bar
  currentStep = 4;
  progressWidth = 100;

  //blob URLs
  gameBoardBlobURL: any;
  ticketCardsBlobURL: any;

  //check if blobs are loaded
  gameBoardBlobLoaded = false;
  ticketCardsBlobLoaded = false;

  constructor(private route: ActivatedRoute,
              private sanitizer: DomSanitizer,
              private mapService: MapService,
              private router: Router
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
      console.log(this.pdfDto)
      const gameBoardFileURL = 'data:application/pdf;base64,' + this.pdfDto.gameBoard;
      const ticketCardsFileURL = 'data:application/pdf;base64, '+ this.pdfDto.ticketCards;

      let gameBlob: any;
      fetch(gameBoardFileURL).then(res => res.blob()).then(b => { gameBlob = b;}).then(() => {
        this.gameBoardBlobURL = URL.createObjectURL(gameBlob);
        this.gameBoardBlobLoaded = true;

      });

      let ticketBlob: any;
      fetch(ticketCardsFileURL).then(res => res.blob()).then(b => { ticketBlob = b;}).then(() => {
        this.ticketCardsBlobURL = URL.createObjectURL(ticketBlob);
        this.ticketCardsBlobLoaded = true;

      });

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

            let gameBlob: any;
            fetch(gameBoardFileURL).then(res => res.blob()).then(b => { gameBlob = b;}).then(() => {
              this.gameBoardBlobURL = URL.createObjectURL(gameBlob);
              this.gameBoardBlobLoaded = true;

            });

            let ticketBlob: any;
            fetch(ticketCardsFileURL).then(res => res.blob()).then(b => { ticketBlob = b;}).then(() => {
              this.ticketCardsBlobURL = URL.createObjectURL(ticketBlob);
              this.ticketCardsBlobLoaded = true;

            });

            const gameBoardFileURLSanitized = this.sanitizer.bypassSecurityTrustResourceUrl(gameBoardFileURL);
            const ticketCardsFileURLSanitized = this.sanitizer.bypassSecurityTrustResourceUrl(ticketCardsFileURL);

            this.gameBoardSource=gameBoardFileURLSanitized;
            this.ticketCardsSource=ticketCardsFileURLSanitized;
          }
        }
      }
    )
  }

  backButton() {
    this.router.navigate(['map/']);
  }

}
