package com.spm.school_project_manage.vo;


public class FilesPageVo {

  private long fId;
  private long pId;
  private String pTitle;
  private String fFileUrl;
  private String fOriginalName;
  private long fSize;
  private String createdAt;
  private String updatedAt;
  private long valid;


  public long getFId() {
    return fId;
  }

  public void setFId(long fId) {
    this.fId = fId;
  }


  public long getPId() {
    return pId;
  }

  public void setPId(long pId) {
    this.pId = pId;
  }


  public String getFFileUrl() {
    return fFileUrl;
  }

  public void setFFileUrl(String fFileUrl) {
    this.fFileUrl = fFileUrl;
  }


  public String getFOriginalName() {
    return fOriginalName;
  }

  public void setFOriginalName(String fOriginalName) {
    this.fOriginalName = fOriginalName;
  }


  public long getFSize() {
    return fSize;
  }

  public void setFSize(long fSize) {
    this.fSize = fSize;
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

  public String getpTitle() {
    return pTitle;
  }

  public void setpTitle(String pTitle) {
    this.pTitle = pTitle;
  }
}
