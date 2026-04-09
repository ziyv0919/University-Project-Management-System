package com.spm.school_project_manage.vo;


public class AnnouncementsPageVo {

  private long aId;
  private String aTitle;
  private String aContent;
  private long uId;
  private long aTarget;
  private String uName;
  private String createdAt;
  private String updatedAt;
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

  public String getUName() {
    return uName;
  }

  public void setUName(String uName) {
    this.uName = uName;
  }

}
