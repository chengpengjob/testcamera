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
    /*@Autowired
    public CameraBrowseController(CameraService cameraService) {
        this.cameraService = cameraService;
    }*/

    @GetMapping("/camera/{id}/teardown")
    public void teardownCamById(@PathVariable Integer id) {
        if (MidleCount.reduce(id) == 0) {
            SyncPipe syncPipe = (SyncPipe) MidleMap.getnum(id);
            syncPipe.cancel();
        }
        /*SyncPipe syncPipe = (SyncPipe) MidleMap.getnum(id);
        syncPipe.cancel();*/
    }

    @GetMapping("/camera/{id}/setup")
    public void setupCameraById(HttpServletRequest request, @PathVariable Integer id, HttpSession session) {
        //CameraService cameraService = AppBeans.get(CameraService.NAME);
        //DataManager dataManager = AppBeans.get(DataManager.NAME);


        GetSyncPipe getSyncPipe = new GetSyncPipe();
        Map<Integer, Object> map = new HashMap<>();
        String fileDir = "F:/testcamera/deploy/tomcat/webapps";
        File videoAddress = new File(fileDir + "/HLS-demo/m3u8/Gear" + id);

        if (!videoAddress.exists()) {
            videoAddress.mkdir();
        }

        /*String[] files = videoAddress.list();
        for (String fileName : files) {
            File deleteTs = new File(videoAddress.getAbsolutePath(), fileName);
            try {
                Files.deleteIfExists(deleteTs.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }*/
        File file = new File(videoAddress.getAbsolutePath());
        File[] listFiles = file.listFiles();
        if(listFiles.length < 1) {

       /*Camera camera = (Camera) dataManager.loadList(LoadContext.create(Camera.class).setQuery(
                LoadContext.createQuery("select e from testcamera_Camera e where e.cameraId like :id")
                        .setParameter("id", "a%")
                        .setMaxResults(10))
                .setView("_local"));*/
            //dataManager.load(Camera.class).query("select e from testcamera_Camera e where e.cameraId like :id").view("_local");

            Configuration configuration = AppBeans.get(Configuration.class);
            WebAuthConfig webAuthConfig = configuration.getConfig(WebAuthConfig.class);
            LoginService loginService = AppBeans.get(LoginService.class);
            UserSession userSession = loginService.getSystemSession(webAuthConfig.getTrustedClientPassword());
            try {
                AppContext.setSecurityContext(new SecurityContext(userSession));
                // call middleware services here
                Camera camera = cameraService.getCameraById(id);

                String account = camera.getAccount();
                String password = camera.getPassword();
                String address = camera.getCameraAddress();

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
                syncPipe.start();
                getSyncPipe.setSyncPipe(syncPipe);

            } finally {
                AppContext.setSecurityContext(null);
            }

        }


    }
}
