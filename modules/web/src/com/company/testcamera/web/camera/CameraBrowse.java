package com.company.testcamera.web.camera;

import com.company.testcamera.entity.Camera;
import com.company.testcamera.service.CameraService;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.screen.Subscribe;
import org.apache.poi.ss.formula.functions.T;

import javax.inject.Inject;
import java.io.*;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.*;
import java.nio.file.Files;

public class CameraBrowse extends AbstractLookup {

    @Inject
    private GroupTable<Camera> camerasTable;

    @Inject
    private GroupDatasource<Camera, UUID> camerasDs;

    @Inject
    private Button viewBtn;
    @Inject
    private Link watchBtn;

    /* @Inject
     private  Button watchBtn;*/
    @Inject
    private DataManager dataManager;


    @Inject
    private Button closeBtn;


    GetSyncPipe getSyncPipe = new GetSyncPipe();


    @Override
    protected void init(InitEvent initEvent) {
        super.init(initEvent);
        camerasTable.addSelectionListener(e -> {
            Camera singleSelected = camerasTable.getSingleSelected();
            Integer cameraId = singleSelected.getCameraId();

            watchBtn.setUrl("http://localhost:8080/HLS-demo/master.html" + "?id=" + cameraId);
        });

        Map<Integer,Object> map = new HashMap<>();


        BaseAction view = new BaseAction("viewAction") {
            @Override
            public void actionPerform(Component component) {

                Camera singleSelected = camerasTable.getSingleSelected();
                String account = singleSelected.getAccount();
                String password = singleSelected.getPassword();
                String address = singleSelected.getCameraAddress();
                Integer id = singleSelected.getCameraId();

                String fileDir = "F:/testcamera/deploy/tomcat/webapps";
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

                map.put(id,syncPipe);

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
        camerasTable.addAction(view);

        BaseAction watch = new BaseAction("watchAction") {

            @Override
            public void actionPerform(Component component) {
              /* Camera singleSelected = camerasTable.getSingleSelected();
                Integer cameraId = singleSelected.getCameraId();
                openWindow("testcamera_Videoscreen", WindowManager.OpenType.DIALOG, ParamsMap.of("videoId", cameraId));*/
            }

            @Override
            protected boolean isPermitted() {
                Camera singleSelected = camerasTable.getSingleSelected();
                return singleSelected == null ? false : true;
            }
        };
        camerasTable.addAction(watch);

        BaseAction close = new BaseAction("closeAction") {
            @Override
            public void actionPerform(Component component) {
                Camera singleSelected = camerasTable.getSingleSelected();
                String voaddress = singleSelected.getVideoAddress();

                if (getSyncPipe.getSyncPipe() == null && voaddress == null) {
                    showMessageDialog("Warning", "请先生成地址！", MessageType.WARNING.modal(true).closeOnClickOutside(true));

                } else {
                    singleSelected.setVideoAddress(null);
                    dataManager.commit(singleSelected);
                    camerasTable.refresh();


                        StopProcess s = new StopProcess((SyncPipe) map.get(singleSelected.getCameraId()));
                        s.start();
                        getSyncPipe.setSyncPipeNull();


                   /* StopProcess s = new StopProcess(getSyncPipe.getSyncPipe());
                    s.start();
                    getSyncPipe.setSyncPipeNull();*/
                }
            }

            @Override
            protected boolean isPermitted() {
                Camera singleSelected = camerasTable.getSingleSelected();
                return singleSelected == null ? false : true;
            }
        };
        closeBtn.setAction(close);
        camerasTable.addAction(close);

    }

    class SyncPipe extends Thread {

        String rtsp = null;
        Process p = null;
        List<String> list = null;
        File videoAddress = null;

        public SyncPipe(List<String> list) {
            this.list = list;
        }

        public SyncPipe(List<String> list, File videoAddress) {
            this.list = list;
            this.videoAddress = videoAddress;
        }


        public synchronized void connectToCamera() {
            try {
                //在指定目录执行命令
                ProcessBuilder processBuilder = new ProcessBuilder(list);
                processBuilder.directory(videoAddress);
                p = processBuilder.start();
                new PrintStream(parse(p.getErrorStream()));
                new PrintStream(parse(p.getInputStream()));
            } catch (Exception e) {
                System.out.println("error 1");
                e.printStackTrace();
            }
        }

        //inputStream转outputStream
        private ByteArrayOutputStream parse(InputStream in) throws Exception {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String temp = null;
            while ((temp = br.readLine()) != null) {
                System.out.println(new String(temp.getBytes("utf-8"), "utf-8"));
                swapStream.write(temp.getBytes());
            }
            return swapStream;
        }

        public void cancel() {
            if (p.isAlive()) {
                p.destroy();
            }
            System.out.println("close thread");
            interrupt();
        }

        @Override
        public void run() {
            connectToCamera();
        }

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

    class GetSyncPipe {
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
    }
}