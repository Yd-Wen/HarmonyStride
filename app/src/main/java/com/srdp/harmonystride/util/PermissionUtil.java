package com.srdp.harmonystride.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class PermissionUtil {
    public static final String TYPE_PHOTO_ALBUM = "相册";
    public static final String TYPE_OPEN_CAMERA = "拍照";
    private static final String[] paramExternal = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final String paramCamera = Manifest.permission.CAMERA;
    //外部存储权限请求码
    private static final int REQUEST_EXTERNAL_STORAGE_CODE = 901;
    //相机权限请求码
    private static final int REQUEST_CAMERA_CODE = 902;
    //打开相册请求码
    private static final int OPEN_ALBUM_CODE = 100;
    //打开摄像头请求码
    private static final int OPEN_CAMERA_CODE = 200;

    private static PermissionUtil instance = null;
    private Context context;
    private Activity activity;

    public static PermissionUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (ScreenSizeUtil.class) {
                if (instance == null)
                    instance = new PermissionUtil(context);
            }
        }
        return instance;
    }

    private PermissionUtil(Context context) {
        this.context = context;
        this.activity = (Activity) context;
    }

    //请求存储权限
    @AfterPermissionGranted(REQUEST_EXTERNAL_STORAGE_CODE)
    public void requestExternalPermission(){
        if(!EasyPermissions.hasPermissions(context, paramExternal)){
            //无权限 则进行权限请求
            EasyPermissions.requestPermissions(activity, "请求存储权限", REQUEST_EXTERNAL_STORAGE_CODE, paramExternal);
        }else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setType("image/*");
            activity.startActivityForResult(intent, OPEN_ALBUM_CODE);
        }
    }

    //请求相机权限
    @AfterPermissionGranted(REQUEST_CAMERA_CODE)
    public void requestCameraPermission(){
        if(!EasyPermissions.hasPermissions(context, paramCamera)){
            //无权限 则进行权限请求
            EasyPermissions.requestPermissions(activity, "请求相机权限", REQUEST_EXTERNAL_STORAGE_CODE, paramCamera);
        }else {
            //启动相机
            //Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activity.startActivityForResult(intent, OPEN_CAMERA_CODE);
        }
    }
}
