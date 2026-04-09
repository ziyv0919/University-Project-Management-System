package com.spm.school_project_manage.vo;


public class ProjectApplicationsPageVo {

  private long paId;
  private long uId;
  private String uName;
  private long pId;
  private String pTitle;
  private long paStatus;
  private String createdAt;
  private String updatedAt;
  private long valid;


  public long getPaId() {
    return paId;
  }

  public void setPaId(long paId) {
    this.paId = paId;
  }


  public long getUId() {
    return uId;
  }

  public void setUId(long uId) {
    this.uId = uId;
  }


  public long getPId() {
    return pId;
  }

  public void setPId(long pId) {
    this.pId = pId;
  }


  public long getPaStatus() {
    return paStatus;
  }

  public void setPaStatus(long paStatus) {
    this.paStatus = paStatus;
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
}
