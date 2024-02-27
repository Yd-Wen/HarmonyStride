package com.srdp.harmonystride.dialog;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.activity.ProfileEditActivity;
import com.srdp.harmonystride.util.LogUtil;
import com.srdp.harmonystride.util.ScreenSizeUtil;

import java.io.File;
import java.io.IOException;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ButtonDialog extends BaseDialog{
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

    private Button takePhotoBtn;
    private Button selectFormAlbumBtn;

    public ButtonDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(View.inflate(MyApplication.getContext(), R.layout.dialog_button, null));
        //初始化视图
        initViews();
        //初始化事件
        initEvents();
    }

    private void initViews(){
        takePhotoBtn = findViewById(R.id.btn_take_photo);
        selectFormAlbumBtn = findViewById(R.id.btn_select_from_album);
        setCancelBtn(findViewById(R.id.btn_dialog_cancel));
        //设置布局
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtil.getInstance(MyApplication.getContext()).getScreenWidth() * 0.9f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(lp);
    }

    private void initEvents(){
        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCameraPermission();
                dismiss();
            }
        });

        selectFormAlbumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestExternalPermission();
                dismiss();
            }
        });

        getCancelBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    //请求存储权限
    @AfterPermissionGranted(REQUEST_EXTERNAL_STORAGE_CODE)
    private void requestExternalPermission(){
        if(!EasyPermissions.hasPermissions(baseContext, paramExternal)){
            //无权限 则进行权限请求
            EasyPermissions.requestPermissions((ProfileEditActivity)baseContext, "请求存储权限", REQUEST_EXTERNAL_STORAGE_CODE, paramExternal);
        }else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setType("image/*");
            if (baseContext instanceof ProfileEditActivity) {
                ((ProfileEditActivity) baseContext).startActivityForResult(intent, OPEN_ALBUM_CODE);
            }
        }
    }

    //请求相机权限
    @AfterPermissionGranted(REQUEST_CAMERA_CODE)
    private void requestCameraPermission(){
        if(!EasyPermissions.hasPermissions(baseContext, paramCamera)){
            //无权限 则进行权限请求
            EasyPermissions.requestPermissions((ProfileEditActivity)baseContext, "请求相机权限", REQUEST_EXTERNAL_STORAGE_CODE, paramCamera);
        }else {
            //启动相机
            //Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (baseContext instanceof ProfileEditActivity) {
                ((ProfileEditActivity) baseContext).startActivityForResult(intent, OPEN_CAMERA_CODE);
            }
        }
    }


}
