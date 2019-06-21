package org.jetbrains.hadoop_netdisk.model;

import java.sql.Date;

/**
 * @auther hasaker
 * @create_date 2019-06-19 18:27
 * @description
 */
public class File {
    private String fullName;
    private String fileName;
    private String owner;
    private int fileSize;
    private Date uploadDate;
    private boolean isDeleted;
    private Date deleteDate;
    private boolean isShared;
    private Date shareDate;
    private int shareExpireTime;
    private boolean shareEncrypt;
    private String shareEncryptCode;

    public String getAbsPath() {
        return fullName;
    }

    public void setAbsPath(String fullName) {
        this.fullName = fullName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Date getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }

    public boolean isShared() {
        return isShared;
    }

    public void setShared(boolean shared) {
        isShared = shared;
    }

    public Date getShareDate() {
        return shareDate;
    }

    public void setShareDate(Date shareDate) {
        this.shareDate = shareDate;
    }

    public int getShareExpireTime() {
        return shareExpireTime;
    }

    public void setShareExpireTime(int shareExpireTime) {
        this.shareExpireTime = shareExpireTime;
    }

    public boolean isShareEncrypt() {
        return shareEncrypt;
    }

    public void setShareEncrypt(boolean shareEncrypt) {
        this.shareEncrypt = shareEncrypt;
    }

    public String getShareEncryptCode() {
        return shareEncryptCode;
    }

    public void setShareEncryptCode(String shareEncryptCode) {
        this.shareEncryptCode = shareEncryptCode;
    }

    public File(String fullName, String fileName, String owner, int fileSize, Date uploadDate,
                boolean isDeleted, Date deleteDate, boolean isShared, Date shareDate, int shareExpireTime,
                boolean shareEncrypt, String shareEncryptCode) {
        this.fullName = fullName;
        this.fileName = fileName;
        this.owner = owner;
        this.fileSize = fileSize;
        this.uploadDate = uploadDate;
        this.isDeleted = isDeleted;
        this.deleteDate = deleteDate;
        this.isShared = isShared;
        this.shareDate = shareDate;
        this.shareExpireTime = shareExpireTime;
        this.shareEncrypt = shareEncrypt;
        this.shareEncryptCode = shareEncryptCode;
    }

    @Override
    public String toString() {
        return "File{" +
                "fullName='" + fullName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", owner='" + owner + '\'' +
                ", fileSize=" + fileSize +
                ", uploadDate=" + uploadDate +
                ", isDeleted=" + isDeleted +
                ", deleteDate=" + deleteDate +
                ", isShared=" + isShared +
                ", shareDate=" + shareDate +
                ", shareExpireTime=" + shareExpireTime +
                ", shareEncrypt=" + shareEncrypt +
                ", shareEncryptCode='" + shareEncryptCode + '\'' +
                '}';
    }
}
