package com.srdp.harmonystride.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    public Context myContext;

    @Override
    protected  void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        myContext = this;
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

    public void showToast(String msg){
        Toast.makeText(myContext,msg,Toast.LENGTH_SHORT).show();
    }

    public void navigateTo(Class cls){
        Intent intent = new Intent(myContext,cls);
        startActivity(intent);
    }
}
