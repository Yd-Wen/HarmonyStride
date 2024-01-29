package com.srdp.harmonystride.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.srdp.harmonystride.R;

public class LoginActivity extends BaseActivity {

    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //初始化视图
        initViews();
        //初始化事件
        initEvents();

    }



    private void initViews() {
        loginButton = findViewById(R.id.btn_login);
    }

    private void initEvents(){
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateTo(MainActivity.class);
                finish();
            }
        });
    }

}

