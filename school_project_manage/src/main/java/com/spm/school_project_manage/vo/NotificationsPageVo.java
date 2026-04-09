package com.spm.school_project_manage.vo;

public class NotificationsPageVo {
    private long nId;
    private long uId;
    private long nType;
    private String nTitle;
    private String nContent;
    private long nIsRead;
    private String createdAt;
    private String updatedAt;
    private long valid;
    private String uName;

    public long getnId() {
        return nId;
    }

    public void setnId(long nId) {
        this.nId = nId;
    }

    public long getuId() {
        return uId;
    }

    public void setuId(long uId) {
        this.uId = uId;
    }

    public long getnType() {
        return nType;
    }

    public void setnType(long nType) {
        this.nType = nType;
    }

    public String getnTitle() {
        return nTitle;
    }

    public void setnTitle(String nTitle) {
        this.nTitle = nTitle;
    }

    public String getnContent() {
        return nContent;
    }

    public void setnContent(String nContent) {
        this.nContent = nContent;
    }

    public long getnIsRead() {
        return nIsRead;
    }

    public void setnIsRead(long nIsRead) {
        this.nIsRead = nIsRead;
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
}