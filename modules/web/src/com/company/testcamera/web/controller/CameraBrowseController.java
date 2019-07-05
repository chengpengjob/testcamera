package com.company.testcamera.web.controller;

import com.company.testcamera.web.camera.MidleMap;
import com.company.testcamera.web.camera.SyncPipe;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequestMapping("/controller")
public class CameraBrowseController {
    @GetMapping("/camera/{id}/teardown")
    public void teardownCamById(@PathVariable Integer id){
        SyncPipe syncPipe =(SyncPipe) MidleMap.getnum(id);
        syncPipe.cancel();

        String fileDir = "F:/testcamera/deploy/tomcat/webapps";
        File dir = new File(fileDir+"/HLS-demo/m3u8/Gear" + id);
    }

    private static void removeDir(File dir){
        File[] files = dir.listFiles();
        for(File file : files){
            if(file.isDirectory()){
                removeDir(file);
            }else{
                System.out.println(file+":"+file.delete());
            }

        }
        System.out.println(dir+":"+dir.delete());
    }
}
