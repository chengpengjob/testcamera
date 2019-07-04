package com.company.testcamera.web.camera;

import java.io.*;
import java.util.List;

public class SyncPipe extends Thread {

    String rtsp = null;
    Process p = null;
    List<String> list = null;
    File videoAddress = null;

    public SyncPipe(List<String> list){
        this.list = list;
    }

    public SyncPipe(List<String> list, File videoAddress){
        this.list = list;
        this.videoAddress = videoAddress;
    }

    public synchronized  void connectToCamera(){
        try {
            //在指定目录下执行命令
            ProcessBuilder processBuilder = new ProcessBuilder(list);
            processBuilder.directory(videoAddress);
            p = processBuilder.start();
            new PrintStream(parse(p.getErrorStream()));
            new PrintStream(parse(p.getInputStream()));

        }catch (Exception e){

        }
    }

    //inputStream转outputStream
    private ByteArrayOutputStream parse(InputStream in) throws Exception{
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String temp = null;
        while((temp = br.readLine()) != null){
            System.out.println(new String(temp.getBytes("utf-8"),"utf-8"));
            swapStream.write(temp.getBytes());

        }
        return swapStream;
    }

    public void cancel(){
        if(p.isAlive()){
            p.destroy();
        }
        System.out.println("close thread");
        interrupt();
    }

    @Override
    public void run(){
        connectToCamera();
    }
}
