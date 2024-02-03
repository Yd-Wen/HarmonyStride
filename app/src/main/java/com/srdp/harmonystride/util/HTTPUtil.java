package com.srdp.harmonystride.util;

import com.google.gson.Gson;

import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HTTPUtil {
    public static String IP = "http://192.168.1.7:8080";
    public static Gson gson = new Gson();

    public static void POST(String url, String header, Map map, Callback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                FormBody.Builder builder = new FormBody.Builder();
                for(Object k : map.keySet()){
                    builder.add((String) k,(String) map.get(k));
                }

                OkHttpClient client=new OkHttpClient();
                final Request request=new Request.Builder()
                        .url(IP + url)
                        .post(builder.build())
                        .build();
                Call call=client.newCall(request);
                call.enqueue(callback);
            }
        }).start();

    }
}
