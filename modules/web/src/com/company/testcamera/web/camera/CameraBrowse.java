package com.company.testcamera.web.camera;

import com.company.testcamera.entity.Camera;
import com.company.testcamera.service.CameraService;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.core.sys.SecurityContext;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.web.App;
import com.haulmont.cuba.web.sys.WebScreens;
import com.vaadin.event.ContextClickEvent;
import com.vaadin.server.ClientConnector;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class CameraBrowse extends AbstractLookup {

    @Inject
    private GroupTable<Camera> camerasTable;

    @Inject
    private GroupDatasource<Camera, UUID> camerasDs;


    @Inject
    private Button viewBtn;
    @Inject
    private Button watchBtn;

    /* @Inject
     private  Button watchBtn;*/
    @Inject
    private DataManager dataManager;

    @Inject
    private Button closeBtn;


    GetSyncPipe getSyncPipe = new GetSyncPipe();

    //int count = 0;
    //final Integer[] cameraId = {1};
    final String[] cameraNumber = {"1"};

    @Override
    protected void init(InitEvent initEvent) {

        Map<Integer, Object> map = new HashMap<>();

        super.init(initEvent);
        camerasTable.addSelectionListener(e -> {
            if (e == null) return;
            Camera singleSelected = camerasTable.getSingleSelected();
            //cameraId[0] = singleSelected.getCameraId();
            cameraNumber[0] = singleSelected.getCameraNumber();
            //watchBtn.setUrl(");
            //watchBtn.setUrl("http://localhost:8888/HLS-demo/master.html" + "?id=" + cameraId);
        });


    }
    @Subscribe("watchBtn")
    private void onWatchBtnClick(Button.ClickEvent event) {
        App app = App.getInstance();
        WebScreens windowManager = app.getWindowManager();

        //windowManager.showWebPage("http://10.200.0.106:8080/HLS-demo/index.html?number=" + cameraNumber[0]+"",null);
        windowManager.showWebPage("http://localhost:8080/HLS-demo/index.html?number=" + cameraNumber[0]+"",null);

        //windowManager.showWebPage("http://localhost:8888/HLS-demo/index.html?number=" + cameraNumber[0]+"",null);
        //windowManager.showWebPage("http://localhost:8080/HLS-demo/index.html?id=" + cameraId[0]+"",null);
        //windowManager.showWebPage("http://localhost:8888/HLS-demo/index.html?id=" + cameraId[0]+"",null);
        //MidleCount.add(cameraId[0], count);

        camerasDs.refresh();

    }

    class StopProcess extends Thread {
        SyncPipe s = null;

        public StopProcess(SyncPipe s) {
            this.s = s;
        }

        @Override
        public void run() {
           /* try {
                System.out.println("sleep");;
                sleep(60000);
            } catch (InterruptedException e) {
                System.out.println("error 2");
                e.printStackTrace();
            }*/
            System.out.println("close");
            s.cancel();
        }
    }

    public class Docmd extends Thread {

        @Override
        public void run() {
            /*try {
                sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            List<String> list = new ArrayList<>();
            list.add("ffplay");
            list.add("http://localhost:8888/hls/movie.m3u8");

            SyncPipe syncPipe = new SyncPipe(list);
            syncPipe.start();
        }
    }
}