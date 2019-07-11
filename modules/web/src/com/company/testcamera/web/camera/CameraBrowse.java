package com.company.testcamera.web.camera;

import com.company.testcamera.entity.Camera;
import com.company.testcamera.service.CameraService;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.core.sys.SecurityContext;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
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

    int count = 0;


    @Override
    protected void init(InitEvent initEvent) {

        Map<Integer, Object> map = new HashMap<>();

        super.init(initEvent);
        final Integer[] cameraId = {1};
        camerasTable.addSelectionListener(e -> {
            if (e == null) return;
            Camera singleSelected = camerasTable.getSingleSelected();
            cameraId[0] = singleSelected.getCameraId();
            watchBtn.addClickListener(ex ->{
                App app = App.getInstance();
                WebScreens windowManager = app.getWindowManager();
                windowManager.showWebPage("http://localhost:8080/HLS-demo/index.html?id=" + cameraId[0]+"",null);
                MidleCount.add(cameraId[0], count);
            });


            //watchBtn.setUrl(");
            //watchBtn.setUrl("http://localhost:8888/HLS-demo/master.html" + "?id=" + cameraId);
        });
        //Map<Integer, Object> map = new HashMap<>();


       /* BaseAction view = new BaseAction("viewAction") {
            @Override
            public void actionPerform(Component component) {

                Camera singleSelected = camerasTable.getSingleSelected();
                String account = singleSelected.getAccount();
                String password = singleSelected.getPassword();
                String address = singleSelected.getCameraAddress();
                Integer id = singleSelected.getCameraId();

                String fileDir = "F:/testcamera/deploy/tomcat/webapps";
                //String fileDir = "C:/Program Files/Tomcat/apache-tomcat-8.5.39/webapps";
                File videoAddress = new File(fileDir + "/HLS-demo/m3u8/Gear" + id);

                if (!videoAddress.exists()) {
                    videoAddress.mkdirs();
                }

                String[] files = videoAddress.list();
                for (String fileName : files) {
                    File deleteTs = new File(videoAddress.getAbsolutePath(), fileName);
                    try {
                        Files.deleteIfExists(deleteTs.toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                String vaddress = videoAddress.getAbsolutePath();
                singleSelected.setVideoAddress(vaddress);
                dataManager.commit(singleSelected);
                camerasTable.refresh();

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
            }

            @Override
            protected boolean isPermitted() {
                Camera singleSelected = camerasTable.getSingleSelected();
                return singleSelected == null ? false : true;
            }
        };
        viewBtn.setAction(view);
        camerasTable.addAction(view);*/

       /*BaseAction watch = new BaseAction("watchAction") {

            @Override
            public void actionPerform(Component component) {
            }

            @Override
            protected boolean isPermitted() {
                Camera singleSelected = camerasTable.getSingleSelected();
                return singleSelected == null ? false : true;
            }
        };
        camerasTable.addAction(watch);*/

       /* BaseAction close = new BaseAction("closeAction") {
            @Override
            public void actionPerform(Component component) {
                Camera singleSelected = camerasTable.getSingleSelected();
                String voaddress = singleSelected.getVideoAddress();

                if ((SyncPipe) map.get(singleSelected.getCameraId()) == null && voaddress == null) {
                    showMessageDialog("Warning", "请先生成地址！", MessageType.WARNING.modal(true).closeOnClickOutside(true));

                } else {
                    singleSelected.setVideoAddress(null);
                    dataManager.commit(singleSelected);
                    camerasTable.refresh();


                    StopProcess s = new StopProcess((SyncPipe) map.get(singleSelected.getCameraId()));
                    s.start();
                    getSyncPipe.setSyncPipeNull();
                }
            }

            @Override
            protected boolean isPermitted() {
                Camera singleSelected = camerasTable.getSingleSelected();
                return singleSelected == null ? false : true;
            }
        };
        closeBtn.setAction(close);
        camerasTable.addAction(close);*/

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

    /*class GetSyncPipe {
        public SyncPipe syncPipe = null;

        public SyncPipe getSyncPipe() {
            return syncPipe;
        }

        public void setSyncPipe(SyncPipe syncPipe) {
            this.syncPipe = syncPipe;
        }

        public void setSyncPipeNull() {
            this.syncPipe = null;
        }
    }*/
}