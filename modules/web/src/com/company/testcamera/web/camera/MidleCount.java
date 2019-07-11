package com.company.testcamera.web.camera;

import java.util.HashMap;
import java.util.Map;

public class MidleCount {

    static Map<Integer,Integer> mp = new HashMap<Integer, Integer>();
    //static Hashtable hashtable = new Hashtable();

    public static void add(int cameraid ,int count){
        if(mp.containsKey(cameraid) == false){
            count = count + 1;
            mp.put(cameraid,count);
        }else{
            Integer a = mp.get(cameraid);
            count = a + 1;
            mp.put(cameraid,count);
        }
    }

    public static Integer reduce(int cameraid){
        Integer  b =mp.get(cameraid);
        Integer c = b - 1;
        mp.put(cameraid,c);
        return c;
    }
}
