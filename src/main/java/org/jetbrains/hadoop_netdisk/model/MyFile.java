package org.jetbrains.hadoop_netdisk.model;

import java.sql.Date;

/**
 * @auther hasaker
 * @create_date 2019-06-19 18:27
 * @description
 */
public class MyFile {
    private String owner;
    private String fileMD5HashCode;
    private String fullName;
    private String fileName;
    private double fileSize;
    private Date uploadDate;
    private boolean isDeleted;
    private Date deleteDate;
    private boolean isShared;
    private Date shareDate;
    private int shareExpireTime;
    private boolean shareEncrypt;
    private String shareEncryptCode;
    private int downloadCount;

    public MyFile(String owner, String fileMD5HashCode, String fullName, String fileName, double fileSize) {
        this.owner = owner;
        this.fileMD5HashCode = fileMD5HashCode;
        this.fullName = fullName;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public String getFileMD5HashCode() {
        return fileMD5HashCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
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

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(double fileSize) {
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

    public int getDownloadCount() {
        return this.downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }
}
