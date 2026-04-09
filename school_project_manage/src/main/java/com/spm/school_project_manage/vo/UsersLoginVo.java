package com.spm.school_project_manage.vo;



public class UsersLoginVo {
    private long uId;
    private String uUsername;
    private String uPassword;
    private String uName;
    private long uRole;
    private String uEmail;
    private String uPhone;
    private String uAvatar;
    private String createdAt;
    private String updatedAt;
    private String token;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
