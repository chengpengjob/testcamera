package com.company.testcamera.web.camera;

import java.util.HashMap;
import java.util.Map;

public class MidleMap {

    static Map<Integer,Object> m = new HashMap<>();

    public static void getHashMap(int cameraId,Object num){
        m.put(cameraId,num);
    }

    public static Object getnum(int cameraId){
        Object p = null;

        for(Map.Entry<Integer,Object> entry : m.entrySet()){
            if(entry.getKey() == cameraId){
                p = entry.getValue();
            }
        }
        return p;

    }
}
