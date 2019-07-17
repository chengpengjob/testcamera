package com.company.testcamera.web.camera;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class MidleMap {

    static Map<Integer,Object> m = new HashMap<Integer, Object>();

    public static void getHashMap(Integer cameraId,Object num){
        m.put(cameraId,num);

    }

    public static Object getnum(Integer cameraId){
        Object p = null;
        for (Map.Entry<Integer, Object> entry : m.entrySet()) {
            if( entry.getKey() == cameraId){
                p = entry.getValue();
            }
        }
        return p;
    }
}
