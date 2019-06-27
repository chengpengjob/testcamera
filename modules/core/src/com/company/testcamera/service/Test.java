package com.company.testcamera.service;

import com.mysql.jdbc.PerVmServerConfigCacheFactory;

import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) {

        String cmd_video = "ffmpeg";
        String cmd_url = "-i rtsp://admin:12345@172.16.10.201/h264";

        List<String> convert = new ArrayList<String>();

        convert.add(cmd_video);
        convert.add(cmd_url);
        convert.add(" -c:v copy -c:a copy -bsf:a aac_adtstoasc -f flv -s 1280x720 -segment_list_flags +live -segment_time 2 -hls_list_size 5 rtmp://localhost:1935/hls/movie");

        boolean mark = true;
        ProcessBuilder builder = new ProcessBuilder();

        try {
            builder.command(convert);
            builder.redirectErrorStream(true);
            builder.start();
        }catch(Exception e){
            mark = false;
            System.out.println(e);
            e.printStackTrace();
        }

    }
}
