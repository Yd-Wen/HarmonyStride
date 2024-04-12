package com.srdp.harmonystride.util;

import com.google.gson.Gson;
import com.srdp.harmonystride.entity.User;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RongIMUtil {
    public static final OkHttpClient client = new OkHttpClient();
    //public static final String IP = "http://yindongwen.top:8080"; //服务器IP地址
    public static final String IP = "http://10.152.221.238:8080"; //服务器IP地址

    public static final Gson gson = new Gson();

    //异步执行POST方法
    public static void POST(RequestBody requestBody, String url, okhttp3.Callback callback){
        Request request = new Request.Builder()
                .url(IP + url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }


    public static void register(User user, okhttp3.Callback callback){
        String url = "/imuser/register";
        // 创建一个FormBody.Builder实例
        FormBody.Builder formBuilder = new FormBody.Builder();
        // 添加键值对到Builder中
        formBuilder.add("userId", user.getAccount());
        formBuilder.add("name", user.getNickname());
        formBuilder.add("portraitUri", user.getAvatarUrl());
        // 创建RequestBody
        RequestBody requestBody = formBuilder.build();
        //发起POST请求
        POST(requestBody, url, callback);
    }

    public static void getToken(String account, okhttp3.Callback callback){
        String url = "/imuser/token";
        // 创建一个FormBody.Builder实例
        FormBody.Builder formBuilder = new FormBody.Builder();
        // 添加键值对到Builder中
        formBuilder.add("userId", account);
        // 创建RequestBody
        RequestBody requestBody = formBuilder.build();
        //发起POST请求
        POST(requestBody, url, callback);
    }


}
