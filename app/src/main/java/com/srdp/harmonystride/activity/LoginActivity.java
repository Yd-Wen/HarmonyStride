package com.srdp.harmonystride.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.srdp.harmonystride.R;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.SharedPreferenceUtil;

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

        setActivityResultLauncher(
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // 处理返回的数据
                        Intent data = result.getData();
                        // 进行你的操作
                        accountEt.setText(data.getStringExtra("account"));
                        passwordEt.setText(data.getStringExtra("password"));
                        rememberPwdCb.setChecked((Boolean)SharedPreferenceUtil.getParam(this, "password_remember", false));
                    }
                })
        );

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
                navigateForResult(RegisterActivity.class);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account = accountEt.getText().toString().trim();
                String password = passwordEt.getText().toString().trim();
                Boolean pwdRemember = rememberPwdCb.isChecked();

                User user = new User(account, password);

                //TODO:服务端检查用户是否存在
                if(verifyUser(user)){
                    //写入SQLite本地数据库
                    user.save();
                    //写入配置信息
                    SharedPreferenceUtil.setParam(getBaseContext(), "current_account", account);
                    SharedPreferenceUtil.setParam(getBaseContext(), "current_password", password);
                    SharedPreferenceUtil.setParam(getBaseContext(), "password_remember", pwdRemember);

                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.putExtra("account", account);
                    startActivity(intent);
                    finish();
                }
            }
        });

        forgetPwdTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateForResult(PasswordResetActivity.class);
            }
        });
    }

    //TODO:服务端检查用户是否存在
    private boolean verifyUser(User user){

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //加载配置信息（二次登录）
        accountEt.setText((String)SharedPreferenceUtil.getParam(this, "current_account", ""));
        rememberPwdCb.setChecked((Boolean)SharedPreferenceUtil.getParam(this, "password_remember", false));
        //是否记住密码
        if(rememberPwdCb.isChecked()){
            passwordEt.setText((String)SharedPreferenceUtil.getParam(this, "current_password", ""));
        }
    }
}

