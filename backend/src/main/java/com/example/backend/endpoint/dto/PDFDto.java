package com.example.backend.endpoint.dto;

public class PDFDto {

  private Long id;

  private byte[] gameBoard;

  private byte[] ticketCards;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public byte[] getGameBoard() {
    return gameBoard;
  }

  public void setGameBoard(byte[] gameBoard) {
    this.gameBoard = gameBoard;
  }

  public byte[] getTicketCards() {
    return ticketCards;
  }

  public void setTicketCards(byte[] ticketCards) {
    this.ticketCards = ticketCards;
  }
}
