package com.srdp.harmonystride.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.srdp.harmonystride.entity.Certification;
import com.srdp.harmonystride.entity.Post;
import com.srdp.harmonystride.entity.User;
import java.util.Map;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HTTPUtil {
    public static final OkHttpClient client = new OkHttpClient();
    public static final String IP = "http://yindongwen.top:8080"; //服务器IP地址
    //public static final String IP = "http:/10.152.221.7:8080"; //服务器IP地址
    //public static final String IP = "http://10.152.221.238:8080";
    public static final Gson gson = new Gson();

    //异步执行GET方法
    public static void GET(String url, okhttp3.Callback callback) {
        LogUtil.d("GET", IP+url);
        Request request = new Request.Builder()
                .url(IP + url)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //异步执行POST方法
    public static void POST(RequestBody requestBody, String url, okhttp3.Callback callback){
        Request request = new Request.Builder()
                .url(IP + url)
                .post(requestBody)
                .build();
        LogUtil.d("isExist", IP+url);
        client.newCall(request).enqueue(callback);
    }

    //异步执行POST方法
    public static void POST(RequestBody requestBody, Boolean noIP, String ipUrl, okhttp3.Callback callback){
        if(noIP){
            Request request = new Request.Builder()
                    .url("http://yindongwen.top:8080" + ipUrl)
                    .post(requestBody)
                    .build();
            client.newCall(request).enqueue(callback);
        }
    }

    public static void POST(Map<String, Object> map, String url, okhttp3.Callback callback){
        FormBody.Builder builder = new FormBody.Builder();
        for(String i : map.keySet()){
            builder.add(i, (String) map.get(i));
        }
        RequestBody requestBody = builder.build();
        POST(requestBody, true, url, callback);
    }

    public static void isRegistered(String account, okhttp3.Callback callback){
        String  url = "/user/is_registered";
        // 创建一个FormBody.Builder实例
        FormBody.Builder formBuilder = new FormBody.Builder();
        // 添加键值对到Builder中
        formBuilder.add("account", account);
        // 创建RequestBody
        RequestBody requestBody = formBuilder.build();
        POST(requestBody, url, callback);
    }

    //验证用户
    public static void verify(String account, String password, okhttp3.Callback callback){
        String url = "/user/verify";
        url = url + "?" + "account=" + account + "&password=" + password;
        LogUtil.d("verify", url);
        GET(url, callback);
    }

    //插入
    public static void insert(User user, Callback callback){
        String  url = "/user/register";
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("account", user.getAccount());
        jsonObject.addProperty("password", user.getPassword());
        jsonObject.addProperty("avatar", user.getAvatar());
        jsonObject.addProperty("nickname", user.getNickname());
        jsonObject.addProperty("gender", user.getAvatar());
        jsonObject.addProperty("location", user.getAvatar());
        jsonObject.addProperty("introduction", user.getAvatar());
        jsonObject.addProperty("certify", user.getAvatar());
        jsonObject.addProperty("focus", user.getAvatar());

        String json = gson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        LogUtil.d("insert", url);
        POST(requestBody, url, callback);
    }

    public static void insert(Certification certification, Callback callback){
        String  url = "/certification/register";
        // 创建一个FormBody.Builder实例
//        FormBody.Builder formBuilder = new FormBody.Builder();
        // 添加键值对到Builder中
//        formBuilder.add("uid", String.valueOf(certification.getUid()));
//        formBuilder.add("type", certification.getType());
//        formBuilder.add("number", certification.getNumber());
//        formBuilder.add("imageurl", certification.getImageUrl());
        // 创建RequestBody
//        RequestBody requestBody = formBuilder.build();

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("uid", certification.getUid());
        jsonObject.addProperty("type", certification.getType());
        jsonObject.addProperty("number", certification.getNumber());
        jsonObject.addProperty("imageurl", certification.getImageUrl());

        String json = gson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        LogUtil.d("insert", url);
        POST(requestBody, url, callback);
    }

    //更新密码
    public static void resetPwd(String account, String password, Callback callback){
        String  url = "/user/reset_pwd";

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("account", account);
        jsonObject.addProperty("password", password);

        String json = gson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        LogUtil.d("resetPwd", url);
        POST(requestBody, url, callback);
    }

    //更新
    public static void update(Object object, Callback callback){
        Class cls = object.getClass();
        String  url = null;
        JsonObject jsonObject = new JsonObject();
        String json = null;
        String  path = "update";
        if(cls == User.class){
            url = "/user/" + path;
           //json = gson.toJson(object);
            User user = (User) object;
            jsonObject.addProperty("account", user.getAccount());
            jsonObject.addProperty("avatar", user.getAvatar());
            jsonObject.addProperty("nickname", user.getNickname());
            jsonObject.addProperty("gender", user.getGender());
            jsonObject.addProperty("location", user.getLocation());
            jsonObject.addProperty("introduction", user.getIntroduction());
            json = gson.toJson(jsonObject);
        }else if(cls == Certification.class){
            url = "/certification/" + path;
            Certification certification = (Certification) object;
            jsonObject.addProperty("uid", certification.getUid());
            jsonObject.addProperty("type", certification.getType());
            jsonObject.addProperty("number", certification.getNumber());
            jsonObject.addProperty("imageurl", certification.getImageUrl());
            jsonObject.addProperty("status", certification.getStatus());
            json = gson.toJson(jsonObject);
        }else if(cls == Post.class){
            url = "/post/" + path;
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        LogUtil.d("update", url + json);
        POST(requestBody, url, callback);
    }

    public static void getUserByAccount(String account, okhttp3.Callback callback){
        String url = "/user/find";
        url = url + "?" + "account=" + account;
        LogUtil.d("get user by account", url);
        GET(url, callback);
    }

    public static void deleteUserById(int id, okhttp3.Callback callback){
        JsonObject jsonObject = new JsonObject();

        String url = "/user/delete";
        //json = gson.toJson(object);
        jsonObject.addProperty("id", id);
        String json = gson.toJson(jsonObject);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        LogUtil.d("delete", url + json);
        POST(requestBody, url, callback);
    }

    //发送系统消息
//    public static void sendSystemMessage(String username, okhttp3.Callback callback){
//        String url = "/message/system";
//        url = url + "?" + "username=" + username ;
//        LogUtil.d("send system message", url);
//        GET(url, callback);
//    }

    //验证并获取认证信息
    public static void getCertify(int uid, okhttp3.Callback callback){
        String url = "/certification/find";
        url = url + "?" + "uid=" + uid ;
        LogUtil.d("verify", url);
        GET(url, callback);
    }

    public static void check(String fileName, okhttp3.Callback callback){
        String url = "/app/check" + "?" + "fileName=" + fileName;
        GET(url, callback);
    }

}
