
export class StatusDto {
  message: string;
  progressStatus: ProgressStatus;


  constructor(message: string, status: ProgressStatus) {
    this.message = message;
    this.progressStatus = status;
  }
}

export enum ProgressStatus {
  NotStarted = "NotStarted",
  TicketRender = "TicketRender",
  TicketSVG = "TicketSVG",
  TicketPDF = "TicketPDF",
  GameBoardRender = "GameBoardRender",
  GameBoardSVG = "GameBoardSVG",
  GameBoardPDF = "GameBoardPDF",
  Finished = "Finished"
}
