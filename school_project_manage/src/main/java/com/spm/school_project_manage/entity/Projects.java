package com.spm.school_project_manage.entity;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class Projects {

  private long pId;
  private String pTitle;
  private String pDescription;
  private String pDirection;
  private long pDifficulty;
  private long uId;
  private long pMaxStudents;
  private String pCover;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updatedAt;
  private long valid;


  public long getPId() {
    return pId;
  }

  public void setPId(long pId) {
    this.pId = pId;
  }


  public String getPTitle() {
    return pTitle;
  }

  public void setPTitle(String pTitle) {
    this.pTitle = pTitle;
  }


  public String getPDescription() {
    return pDescription;
  }

  public void setPDescription(String pDescription) {
    this.pDescription = pDescription;
  }


  public String getPDirection() {
    return pDirection;
  }

  public void setPDirection(String pDirection) {
    this.pDirection = pDirection;
  }


  public long getPDifficulty() {
    return pDifficulty;
  }

  public void setPDifficulty(long pDifficulty) {
    this.pDifficulty = pDifficulty;
  }


  public long getUId() {
    return uId;
  }

  public void setUId(long uId) {
    this.uId = uId;
  }


  public long getPMaxStudents() {
    return pMaxStudents;
  }

  public void setPMaxStudents(long pMaxStudents) {
    this.pMaxStudents = pMaxStudents;
  }


  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }


  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }


  public long getValid() {
    return valid;
  }

  public void setValid(long valid) {
    this.valid = valid;
  }

  public String getpCover() {
    return pCover;
  }

  public void setpCover(String pCover) {
    this.pCover = pCover;
  }
}
