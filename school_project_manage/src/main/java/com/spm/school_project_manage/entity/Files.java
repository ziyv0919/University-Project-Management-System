package com.spm.school_project_manage.entity;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class Files {

  private long fId;
  private long pId;
  private String fFileUrl;
  private String fOriginalName;
  private long fSize;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updatedAt;
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

}
