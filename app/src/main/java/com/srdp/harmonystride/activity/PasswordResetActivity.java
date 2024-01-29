package com.srdp.harmonystride.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import com.srdp.harmonystride.R;

public class PasswordResetActivity extends BaseActivity {

    private Toolbar toolbar;
    private EditText accountEt;
    private EditText newPwdEt;
    private EditText verificationCodeEt;
    private Button getVerificationCodeBtn;
    private Button resetPwdBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        //初始化视图
        initViews();
        //初始化事件
        initEvents();
    }

    public void initViews(){
        toolbar = findViewById(R.id.tool_bar);
        //不要忘记设置工具栏
        setSupportActionBar(toolbar);

        accountEt = findViewById(R.id.et_account);
        verificationCodeEt = findViewById(R.id.et_verification_code);
        newPwdEt = findViewById(R.id.et_password_new);
        getVerificationCodeBtn = findViewById(R.id.btn_verification_code_get);
        resetPwdBtn = findViewById(R.id.btn_password_reset);
    }

    public void initEvents(){

        getVerificationCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        resetPwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    //获取工具栏菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_simple_toolbar, menu);
        return true;
    }

    //为工具栏菜单绑定单击事件监听器
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.close:
                finish();
                break;
            default:
                break;
        }
        return true;
    }
}