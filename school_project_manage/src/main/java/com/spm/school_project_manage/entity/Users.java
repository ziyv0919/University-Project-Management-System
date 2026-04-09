package com.spm.school_project_manage.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class Users {

  private long uId;
  private String uUsername;
  private String uPassword;
  private String uName;
  private long uRole;
  private String uEmail;
  private String uPhone;
  private String uAvatar;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updatedAt;
  private long valid;


  public long getUId() {
    return uId;
  }

  public void setUId(long uId) {
    this.uId = uId;
  }


  public String getUUsername() {
    return uUsername;
  }

  public void setUUsername(String uUsername) {
    this.uUsername = uUsername;
  }


  public String getUPassword() {
    return uPassword;
  }

  public void setUPassword(String uPassword) {
    this.uPassword = uPassword;
  }


  public String getUName() {
    return uName;
  }

  public void setUName(String uName) {
    this.uName = uName;
  }


  public long getURole() {
    return uRole;
  }

  public void setURole(long uRole) {
    this.uRole = uRole;
  }


  public String getUEmail() {
    return uEmail;
  }

  public void setUEmail(String uEmail) {
    this.uEmail = uEmail;
  }


  public String getUPhone() {
    return uPhone;
  }

  public void setUPhone(String uPhone) {
    this.uPhone = uPhone;
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

  public String getuAvatar() {
    return uAvatar;
  }

  public void setuAvatar(String uAvatar) {
    this.uAvatar = uAvatar;
  }
}
