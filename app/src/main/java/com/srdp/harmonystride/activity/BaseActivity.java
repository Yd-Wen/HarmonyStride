package com.srdp.harmonystride.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.dialog.BaseDialog;
import com.srdp.harmonystride.dialog.factory.DialogFactory;

public class BaseActivity extends AppCompatActivity {
    private ActivityResultLauncher activityResultLauncher;
    protected DialogFactory factory;
    protected BaseDialog dialog;

    @Override
    protected  void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {});
    }

    //设置ActivityResultLauncher
    public void setActivityResultLauncher(ActivityResultLauncher activityResultLauncher) {
        this.activityResultLauncher = activityResultLauncher;
    }

    //获取ActivityResultLauncher
    public ActivityResultLauncher getActivityResultLauncher() {
        return activityResultLauncher;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //消息提示
    public void showToast(String msg){
        Toast.makeText(MyApplication.getContext(),msg,Toast.LENGTH_SHORT).show();
    }

    //活动跳转
    public void navigateTo(Class cls){
        Intent intent = new Intent(MyApplication.getContext(),cls);
        startActivity(intent);
    }

    //活动跳转
    public void navigateTo(Intent intent){
        startActivity(intent);
    }

    //返回结果的活动跳转
    public void navigateForResult(Class cls){
        Intent intent = new Intent(MyApplication.getContext(), cls);
        activityResultLauncher.launch(intent);
    }

    //返回结果的活动跳转
    public void navigateForResult(Intent intent){
        activityResultLauncher.launch(intent);
    }
}
