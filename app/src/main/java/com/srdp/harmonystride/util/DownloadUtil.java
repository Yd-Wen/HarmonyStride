package com.srdp.harmonystride.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.srdp.harmonystride.entity.AppInfo;
import com.srdp.harmonystride.entity.Result;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.rong.imlib.NativeObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DownloadUtil {
    public static final int DOWNLOAD_PREPARE = 0;
    public static final int DOWNLOAD_START = 1;
    public static final int DOWNLOAD_DOING = 2;
    public static final int DOWNLOAD_SUCCESS = 3;
    public static final int DOWNLOAD_FAIL = 4;
    public static boolean IS_STOP = false;

    private Handler handler = null;
    AppInfo appInfo = null;

    public static final String apkName = "HarmonyStride.apk";
    public static File apkFile;
    public static final String jsonName = "HarmonyStride-v";

    private static DownloadUtil instance = null;
    private Context mContext;
    private OkHttpClient client;

    public static DownloadUtil getInstance(Context mContext, Handler handler){
        if (instance == null) {
            synchronized (DownloadUtil.class) {
                if (instance == null)
                    instance = new DownloadUtil(mContext, handler);
            }
        }
        return instance;
    }

    private DownloadUtil(Context mContext, Handler handler) {
        this.mContext = mContext;
        this.handler = handler;
        apkFile = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), apkName);
        client = new OkHttpClient();
    }

    public void checkUpdate() throws PackageManager.NameNotFoundException {
        int localVersion = getVersionCode(mContext);
        String fileName = jsonName + (localVersion+1) + ".json";
        HTTPUtil.check(fileName, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new Gson();
                String responseBody = response.body().string();
                Result result = gson.fromJson(responseBody, Result.class);
                if(result.getCode() == 1){
                    JsonObject appInfoJson = gson.fromJson(responseBody, JsonObject.class).getAsJsonObject("data");
                    appInfo = gson.fromJson(appInfoJson, AppInfo.class);
                    if(localVersion < appInfo.getVersionCode()) {
                        appInfo.setOldVersionName(getVersionName(mContext));
                        Message msg = new Message();
                        msg.what = DOWNLOAD_PREPARE;
                        msg.obj = appInfo;
                        handler.sendMessage(msg);
                    }
                }
            }
        });
    }

    /**
     * 获取应用的版本号versionCode
     */
    public static int getVersionCode(Context context) {
        PackageManager manager = context.getPackageManager();
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * 获取应用的版本号versionName
     */
    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = "";
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * @param url 下载连接
     */
    public void download(String url){
        // 创建请求
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    fos = new FileOutputStream(apkFile);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        Message msg = new Message();
                        if(IS_STOP)  msg.what = DOWNLOAD_FAIL;
                        else {
                            msg.what = DOWNLOAD_DOING;
                            msg.obj = progress;
                        }
                        handler.sendMessage(msg);
                    }
                    fos.flush();
                    Message msg = new Message();
                    msg.what = DOWNLOAD_SUCCESS;
                    handler.sendMessage(msg);

                } catch (Exception e) {
                    LogUtil.e("error", e.toString());
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    /**
     * 安装APK内容
     */
    public void installAPK(Context mContext, File apkFile) {
        try {
            if (!apkFile.exists()) {
                return;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//安装完成后打开新版本
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // 给目标应用一个临时授权
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判断版本大于等于7.0
                //如果SDK版本>=24，即：Build.VERSION.SDK_INT >= 24，使用FileProvider兼容安装apk
                String packageName = mContext.getApplicationContext().getPackageName();
                String authority = new StringBuilder(packageName).append(".provider").toString();
                Uri apkUri = FileProvider.getUriForFile(mContext, authority, apkFile);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            }
            mContext.startActivity(intent);
            //android.os.Process.killProcess(android.os.Process.myPid());//安装完之后会提示”完成” “打开”。

        } catch (Exception e) {
        }
    }

}
