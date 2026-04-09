package com.spm.school_project_manage.vo;


public class ProgressReportsPageVo {

  private long prId;
  private long psId;
  private long pId;
  private String uName;
  private long uId;
  private String pTitle;
  private String prTitle;
  private String prContent;
  private String prTeacherComment;
  private long prScore;
  private String createdAt;
  private String updatedAt;
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


  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }


  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
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
