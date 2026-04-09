package com.spm.school_project_manage.vo;


public class ProjectTagsPageVo {

  private long ptId;
  private long pId;
  private long tId;
  private String createdAt;
  private String updatedAt;
  private long valid;


  public long getPtId() {
    return ptId;
  }

  public void setPtId(long ptId) {
    this.ptId = ptId;
  }


  public long getPId() {
    return pId;
  }

  public void setPId(long pId) {
    this.pId = pId;
  }


  public long getTId() {
    return tId;
  }

  public void setTId(long tId) {
    this.tId = tId;
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

}
