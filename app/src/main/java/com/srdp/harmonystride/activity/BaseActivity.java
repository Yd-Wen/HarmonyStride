package com.srdp.harmonystride.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    public Context baseContext;
    private ActivityResultLauncher activityResultLauncher;

    @Override
    protected  void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        baseContext = this;
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {});
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
        Toast.makeText(baseContext,msg,Toast.LENGTH_SHORT).show();
    }

    //活动跳转
    public void navigateTo(Class cls){
        Intent intent = new Intent(baseContext,cls);
        startActivity(intent);
    }

    //设置ActivityResultLauncher
    public void setActivityResultLauncher(ActivityResultLauncher activityResultLauncher) {
        this.activityResultLauncher = activityResultLauncher;
    }

    //返回结果的活动跳转
    public void navigateForResult(Class cls){
        Intent intent = new Intent(baseContext, cls);
        activityResultLauncher.launch(intent);
    }
}
