package com.srdp.harmonystride.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.srdp.harmonystride.R;

public class LoginActivity extends BaseActivity {

    private EditText accountEt;
    private EditText passwordEt;
    private Button registerBtn;
    private Button loginBtn;
    private CheckBox rememberPwdCb;
    private TextView forgetPwdTv;

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
        accountEt = findViewById(R.id.et_account);
        passwordEt = findViewById(R.id.et_password);
        registerBtn = findViewById(R.id.btn_register);
        loginBtn = findViewById(R.id.btn_login);
        rememberPwdCb = findViewById(R.id.cb_password_remember);
        forgetPwdTv = findViewById(R.id.tv_password_forget);
    }

    private void initEvents(){
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateTo(RegisterActivity.class);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateTo(MainActivity.class);
                finish();
            }
        });

        forgetPwdTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateTo(PasswordResetActivity.class);
            }
        });
    }

}

