package com.company.testcamera.web.controller;

import com.company.testcamera.entity.Camera;
import com.company.testcamera.service.CameraService;
import com.company.testcamera.web.camera.GetSyncPipe;
import com.company.testcamera.web.camera.MidleCount;
import com.company.testcamera.web.camera.MidleMap;
import com.company.testcamera.web.camera.SyncPipe;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.core.sys.CubaThreadPoolTaskScheduler;
import com.haulmont.cuba.core.sys.SecurityContext;
import com.haulmont.cuba.core.sys.SecurityContextAwareRunnable;
import com.haulmont.cuba.restapi.Authentication;
import com.haulmont.cuba.security.app.LoginService;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.auth.WebAuthConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.annotation.Resources;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.Executors.*;

@RestController
@RequestMapping("/controller")
public class CameraBrowseController {

    @Autowired
    private CameraService cameraService;

    final SecurityContext securityContext = AppContext.getSecurityContext();

    ExecutorService executorService = Executors.newFixedThreadPool(5);

    static boolean ffpegStatus = false;

    @GetMapping("/camera/{cameraNumber}/teardown")
    public void teardownCamById(@PathVariable String cameraNumber) {

        Configuration configuration = AppBeans.get(Configuration.class);
        WebAuthConfig webAuthConfig = configuration.getConfig(WebAuthConfig.class);
        LoginService loginService = AppBeans.get(LoginService.class);
        UserSession userSession = loginService.getSystemSession(webAuthConfig.getTrustedClientPassword());
        try {
            AppContext.setSecurityContext(new SecurityContext(userSession));
            Camera camera = cameraService.getCameraByNumber(cameraNumber);
            int id = camera.getCameraId();
            int p = MidleCount.reduce(id);

            if (p == 0) {
                ffpegStatus = false;
                SyncPipe syncPipe = (SyncPipe) MidleMap.getnum(id);
                syncPipe.cancel();


                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        String fileDir = "F:/testcamera/deploy/tomcat/webapps";
                        //String fileDir = "C:/Program Files/Tomcat/apache-tomcat-8.5.39/webapps";
                        File videoAddress = new File(fileDir + "/HLS-demo/m3u8/Gear" + cameraNumber);

                        String[] files = videoAddress.list();
                        for (String fileName : files) {
                            File deleteTs = new File(videoAddress.getAbsolutePath(), fileName);
                            try {
                                Files.deleteIfExists(deleteTs.toPath());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                thread.start();
               /* File file = new File(videoAddress.getAbsolutePath());
                File[] listFiles = file.listFiles();
                if(listFiles.length > 0){
                    String[] filess = videoAddress.list();
                    for (String fileName : filess) {
                        File deleteTs = new File(videoAddress.getAbsolutePath(), fileName);
                        try {
                            Files.deleteIfExists(deleteTs.toPath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }*/

            }

        } finally {
            AppContext.setSecurityContext(null);
        }
       /* if (MidleCount.reduce(id) == 0) {
            SyncPipe syncPipe = (SyncPipe) MidleMap.getnum(id);
            try {
                syncPipe.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            syncPipe.cancel();

            String fileDir = "F:/testcamera/deploy/tomcat/webapps";
            //String fileDir = "C:/Program Files/Tomcat/apache-tomcat-8.5.39/webapps";
            File videoAddress = new File(fileDir + "/HLS-demo/m3u8/Gear" + id);

            String[] files = videoAddress.list();
            for (String fileName : files) {
                File deleteTs = new File(videoAddress.getAbsolutePath(), fileName);
                try {
                    Files.deleteIfExists(deleteTs.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/
    }

    @GetMapping("/camera/{cameraNumber}/setup")
    public synchronized Integer setupCameraById(@PathVariable String cameraNumber) {

        //cuba是通过点击，所以MidleCount.add是可用的，实际环境是通过链接直接访问，所以下面代码是来使MidleCount.add有效
        Configuration configurations = AppBeans.get(Configuration.class);
        WebAuthConfig webAuthConfigs = configurations.getConfig(WebAuthConfig.class);
        LoginService loginServices = AppBeans.get(LoginService.class);
        UserSession userSessions = loginServices.getSystemSession(webAuthConfigs.getTrustedClientPassword());
        try {
            AppContext.setSecurityContext(new SecurityContext(userSessions));
            Camera camera = cameraService.getCameraByNumber(cameraNumber);
            int id = camera.getCameraId();
            int num = 0;
            MidleCount.add(id,num);
        } finally {
            AppContext.setSecurityContext(null);
        }

        int count = 0 ;
        GetSyncPipe getSyncPipe = new GetSyncPipe();
        Map<Integer, Object> map = new HashMap<>();
        String fileDir = "F:/testcamera/deploy/tomcat/webapps";
        //String fileDir = "C:/Program Files/Tomcat/apache-tomcat-8.5.39/webapps";
        File videoAddress = new File(fileDir + "/HLS-demo/m3u8/Gear" + cameraNumber);

        if (!videoAddress.exists()) {
            videoAddress.mkdir();
        }
        //File file = new File(videoAddress.getAbsolutePath());
        //File[] listFiles = file.listFiles();
        //if (listFiles.length < 1) {
        if(ffpegStatus == false){
            count = count + 1;

            Configuration configuration = AppBeans.get(Configuration.class);
            WebAuthConfig webAuthConfig = configuration.getConfig(WebAuthConfig.class);
            LoginService loginService = AppBeans.get(LoginService.class);
            UserSession userSession = loginService.getSystemSession(webAuthConfig.getTrustedClientPassword());
            try {
                AppContext.setSecurityContext(new SecurityContext(userSession));
                //Camera camera = cameraService.getCameraById(id);
                Camera camera = cameraService.getCameraByNumber(cameraNumber);

                String account = camera.getAccount();
                String password = camera.getPassword();
                String address = camera.getCameraAddress();
                int id = camera.getCameraId();

                //String dir = "http://localhost:8080/HLS-demo/master.html?cameraId=";
                //String dir = "http://localhost:8080/HLS-demo/master.html?cameranumber=";

                //String dir = "http://10.200.0.106:8080/HLS-demo/index.html?cameranumber=";
                String dir = "http://localhost:8080/HLS-demo/index.html?cameranumber=";

                //String dir = "http://localhost:8888/HLS-demo/index.html?cameranumber=";
                //String dir = "http://localhost:8888/HLS-demo/master.html?cameraId=";
                //File video = new File(dir + id);
                File video = new File(dir + cameraNumber);

                String videoadd = video.getPath();
                //String videoass = camera.setVideoAddress(videoadd);
                cameraService.updateCameraById(id,videoadd);
                //cameraService.updateCameraByNumber(cameranumber,videoadd);

                StringBuffer buffer = new StringBuffer();
                buffer.append("rtsp://");
                buffer.append(account);
                buffer.append(":");
                buffer.append(password);
                buffer.append("@");
                buffer.append(address);

                List<String> list = new ArrayList<>();
                list.add("ffmpeg");
                list.add("-i");
                list.add(buffer.toString());
                list.add("-c:v");
                list.add("copy");
                list.add("-an");
                list.add("-ss");
                list.add("1");
                list.add("-map");
                list.add("0");
                list.add("-f");
                list.add("hls");
                list.add("-hls_flags");
                list.add("delete_segments+omit_endlist");
                list.add("-hls_allow_cache");
                list.add("0");
                list.add("-hls_segment_filename");
                list.add("output%03d.ts");
                list.add("playlist.m3u8");

                SyncPipe syncPipe = new SyncPipe(list, videoAddress);

                map.put(id, syncPipe);

                //把对应线程根据cameraid存到midlemap中
                for (Map.Entry<Integer, Object> entry : map.entrySet()) {
                    MidleMap.getHashMap(entry.getKey(), entry.getValue());
                }
                ffpegStatus = true;
                syncPipe.start();
                getSyncPipe.setSyncPipe(syncPipe);

            } finally {
                AppContext.setSecurityContext(null);
            }

        }
        return count;


    }
}
