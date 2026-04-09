package com.spm.school_project_manage.dto;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class ProgressReportsPageDto {

  private long prId;
  private long psId;
  private long pId;
  private long uId;
  private String uName;
  private String pTitle;
  private String prTitle;
  private String prContent;
  private String prTeacherComment;
  private long prScore;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updatedAt;
  private long valid;


  public long getPrId() {
    return prId;
  }

  public void setPrId(long prId) {
    this.prId = prId;
  }

  public String getPrTitle() {
    return prTitle;
  }

  public void setPrTitle(String prTitle) {
    this.prTitle = prTitle;
  }


  public String getPrContent() {
    return prContent;
  }

  public void setPrContent(String prContent) {
    this.prContent = prContent;
  }


  public String getPrTeacherComment() {
    return prTeacherComment;
  }

  public void setPrTeacherComment(String prTeacherComment) {
    this.prTeacherComment = prTeacherComment;
  }


  public long getPrScore() {
    return prScore;
  }

  public void setPrScore(long prScore) {
    this.prScore = prScore;
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

  public String getuName() {
    return uName;
  }

  public void setuName(String uName) {
    this.uName = uName;
  }

  public String getpTitle() {
    return pTitle;
  }

  public void setpTitle(String pTitle) {
    this.pTitle = pTitle;
  }

  public long getPsId() {
    return psId;
  }

  public void setPsId(long psId) {
    this.psId = psId;
  }

  public long getpId() {
    return pId;
  }

  public void setpId(long pId) {
    this.pId = pId;
  }

  public long getuId() {
    return uId;
  }

  public void setuId(long uId) {
    this.uId = uId;
  }
}
