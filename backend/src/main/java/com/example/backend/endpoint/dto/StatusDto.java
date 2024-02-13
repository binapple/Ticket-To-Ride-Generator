package com.example.backend.endpoint.dto;

import com.example.backend.type.ProgressStatus;

public class StatusDto {

  private String message;
  private ProgressStatus progressStatus;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public ProgressStatus getProgressStatus() {
    return progressStatus;
  }

  public void setProgressStatus(ProgressStatus progressStatus) {
    this.progressStatus = progressStatus;
  }
}
