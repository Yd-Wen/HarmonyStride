package com.srdp.harmonystride.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class IMUtil {
    public static final OkHttpClient client = new OkHttpClient();
    public static final Gson gson = new Gson();

    public static final String Host = "https://a1.easemob.com";

    public static final String AppKey = "1161240210157052#harmonystride";
    public static final String ClientId = "YXA6vAnfbm_tQAyJMPmUYRVhKg";
    public static final String ClientSecret = "YXA6CjZwYDPFMWUeTAtxiND8IG9nG4Y";

    public static final String OrgName = "1161240210157052";
    public static final String AppName = "harmonystride";
    public static final String AppToken = "YWMtnSeCSNSmEe6dHSGpCpJUD-An2JkeUz4ehKSnb4LxwEy8Cd9ub-1ADIkw-ZRhFWEqAgMAAAGN5X3-hQAAAAB5JJMfS004XOWTBqzeUO-ItZ_mcWeAeEAX2PJ0OWK6LQ";
    public static final String Authorization = "Bearer " + AppToken;
    //public static final String Expiration = "";

    //异步执行POST方法
    public static void POST(RequestBody requestBody, String url, okhttp3.Callback callback){
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", Authorization)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void registerUser(String username, String password, okhttp3.Callback callback){
        //请求路径
        String  url = Host + "/" + OrgName + "/" + AppName + "/users";
        //获取请求体
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);
        String json = gson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        //发起请求
        POST(requestBody, url, callback);
    }

    public static void resetPassword(String username, String newPassword, okhttp3.Callback callback){
        //请求路径
        String  url = Host + "/" + OrgName + "/" + AppName + "/users" + "/" + username + "/password";
        //获取请求体
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("newpassword", newPassword);
        String json = gson.toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        //发起请求
        POST(requestBody, url, callback);
    }



}
