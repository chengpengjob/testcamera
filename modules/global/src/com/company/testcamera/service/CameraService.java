package com.company.testcamera.service;

import com.company.testcamera.entity.Camera;

import java.util.List;

public interface CameraService {
    String NAME = "CameraService";
    Camera getCameraById(Integer cameraId);
    Camera updateCameraById(Integer cameraId,String videoAddress);

    Camera getCameraByNumber(String cameraNumber);
    Camera updateCameraByNumber(String cameraNumber,String videoAddress);
}
