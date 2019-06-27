package com.company.testcamera.entity;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "TESTCAMERA_CAMERA")
@Entity(name = "testcamera_Camera")
public class Camera extends StandardEntity {
    @Column(name = "CAMERA_ID")
    protected Integer cameraId;
    @Column(name = "CAMERA_NUMBER")
    protected String cameraNumber;
    @Column(name = "CAMERA_ADDRESS")
    protected String cameraAddress;
    @Column(name = "ACCOUNT")
    protected String account;
    @Column(name = "PASSWORD")
    protected String password;
    @Column(name = "VIDEO_ADDRESS")
    protected String videoAddress;
    @Column(name = "THUMBNAIL_ADDRESS")
    protected String thumbnailAddress;

    public String getThumbnailAddress() {
        return thumbnailAddress;
    }

    public void setThumbnailAddress(String thumbnailAddress) {
        this.thumbnailAddress = thumbnailAddress;
    }

    public String getVideoAddress() {
        return videoAddress;
    }

    public void setVideoAddress(String videoAddress) {
        this.videoAddress = videoAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCameraAddress() {
        return cameraAddress;
    }

    public void setCameraAddress(String cameraAddress) {
        this.cameraAddress = cameraAddress;
    }

    public String getCameraNumber() {
        return cameraNumber;
    }

    public void setCameraNumber(String cameraNumber) {
        this.cameraNumber = cameraNumber;
    }

    public Integer getCameraId() {
        return cameraId;
    }

    public void setCameraId(Integer cameraId) {
        this.cameraId = cameraId;
    }
}