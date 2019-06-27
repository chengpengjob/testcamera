package com.company.testcamera.service;

import java.io.PrintWriter;

public class Test1 {

    public static void main(String[] args) {
        Process p =null;

        String cmd_video = "ffmpeg";
        String cmd_url = "-i rtsp://admin:12345@172.16.10.201/h264";

        String str = cmd_video + cmd_url +" -c:v copy -c:a copy -bsf:a aac_adtstoasc -f flv -s 1280x720 -segment_list_flags +live -segment_time 2 -hls_list_size 5 rtmp://localhost:1935/hls/movie";

        String [] command = {"cmd",};

        try {
            p = Runtime.getRuntime().exec(command);
            PrintWriter stdin = new PrintWriter(p.getOutputStream());
            stdin.println(str);
            stdin.close();
        }catch (Exception e){
            throw  new RuntimeException("编译出现错误：" + e.getMessage());

        }
    }
}
