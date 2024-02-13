package com.example.backend.entitiy;

import com.example.backend.type.ProgressStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;

@Entity(name = "pdf")
public class PDF {
  @Id
  @GeneratedValue(strategy =  GenerationType.IDENTITY)
  private Long id;

  private ProgressStatus status;

  @Lob
  @Column(name = "gameBoard", columnDefinition = "BLOB")
  private byte[] gameBoard;

  @Lob
  @Column(name = "ticketCards", columnDefinition = "BLOB")
  private byte[] ticketCards;

  @OneToOne(mappedBy = "pdf")
  private Map map;

  public Map getMap() {
    return map;
  }

  public void setMap(Map map) {
    this.map = map;
  }

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

  public ProgressStatus getStatus() {
    return status;
  }

  public void setStatus(ProgressStatus status) {
    this.status = status;
  }
}
