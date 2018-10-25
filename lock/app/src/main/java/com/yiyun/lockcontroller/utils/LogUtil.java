package com.yiyun.lockcontroller.utils;


import android.util.Log;

public class LogUtil {
    private static final  String mString = "MYTAG";
    public static void log(String string){
        Log.d(mString,string);
    }
}
