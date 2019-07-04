package com.company.testcamera.web.controller;

import com.company.testcamera.web.camera.MidleMap;
import com.company.testcamera.web.camera.SyncPipe;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/controller")
public class CameraBrowseController {
    @GetMapping("/camera/{id}/teardown")
    public void teardownCamById(@PathVariable Integer id){
        SyncPipe syncPipe =(SyncPipe) MidleMap.getnum(id);
        syncPipe.cancel();
    }
}
