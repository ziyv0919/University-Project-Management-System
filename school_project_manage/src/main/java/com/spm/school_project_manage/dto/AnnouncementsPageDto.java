package com.spm.school_project_manage.dto;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class AnnouncementsPageDto {

  private long aId;
  private String aTitle;
  private String aContent;
  private long uId;
  private long aTarget;
  private String uName;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updatedAt;
  private long valid;


  public long getAId() {
    return aId;
  }

  public void setAId(long aId) {
    this.aId = aId;
  }


  public String getATitle() {
    return aTitle;
  }

  public void setATitle(String aTitle) {
    this.aTitle = aTitle;
  }


  public String getAContent() {
    return aContent;
  }

  public void setAContent(String aContent) {
    this.aContent = aContent;
  }


  public long getUId() {
    return uId;
  }

  public void setUId(long uId) {
    this.uId = uId;
  }


  public long getATarget() {
    return aTarget;
  }

  public void setATarget(long aTarget) {
    this.aTarget = aTarget;
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

  public String getUName() {
    return uName;
  }

  public void setUName(String uName) {
    this.uName = uName;
  }

}
