package com.srdp.harmonystride.util;

public class StringUtil {
    public static boolean isEmpty(String string){
        if(string == null || string.length()<=0){
            return true;
        }else{
            return false;
        }
    }

}
