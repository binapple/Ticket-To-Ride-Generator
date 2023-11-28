export class PDFDto {

  id: number;
  gameBoard: any;
  ticketCards: any;


  constructor(id: number, gameBoard: any, ticketCards: any) {
    this.id = id;
    this.gameBoard = gameBoard;
    this.ticketCards = ticketCards;
  }
}
