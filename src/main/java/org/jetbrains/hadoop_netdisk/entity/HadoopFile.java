package org.jetbrains.hadoop_netdisk.entity;

import java.sql.Date;

/**
 * @auther hasaker
 * @create_date 2019-06-19 18:27
 * @description
 */
public class HadoopFile {
    private String hashCode;
    private String hdfsPath;
    private double fileSize;
    private Date uploadDate;
    private boolean isDeleted;
    private Date deleteDate;
    private boolean isShared;
    private Date shareDate;
    private Date shareExpireDate;
    private boolean shareEncrypt;
    private String shareEncryptCode;
    private int downloadCount;
    private String owner;

    public HadoopFile(String hashCode, String hdfsPath, double fileSize, String owner) {
        this.hashCode = hashCode;
        this.hdfsPath = hdfsPath;
        this.fileSize = fileSize;
        this.owner = owner;
    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public String getHdfsPath() {
        return hdfsPath;
    }

    public void setHdfsPath(String hdfsPath) {
        this.hdfsPath = hdfsPath;
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

    public Date getShareExpireDate() {
        return shareExpireDate;
    }

    public void setShareExpireDate(Date shareExpireDate) {
        this.shareExpireDate = shareExpireDate;
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
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
