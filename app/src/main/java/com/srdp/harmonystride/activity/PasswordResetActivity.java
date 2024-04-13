package com.srdp.harmonystride.activity;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.entity.Result;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.HTTPUtil;
import com.srdp.harmonystride.util.LogUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;
import com.srdp.harmonystride.util.StringUtil;
import com.srdp.harmonystride.util.TimerUtil;
import java.io.IOException;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PasswordResetActivity extends BaseActivity {
    private static final int UPDATE_SUCCESS = 1; //重置密码成功
    private static final int EXIST = 3; //账号已存在
    public static final int NOT_EXIST = 4; //账号不存在
    private static final int MESSAGE_GET_SUCCESS = 5; //短信发送成功
    private static final int MESSAGE_GET_ERROR = 6; //短信发送失败

    private Toolbar toolbar;
    private EditText accountEt;
    private EditText newPwdEt;
    private EditText verificationCodeEt;
    private Button getVerificationCodeBtn;
    private Button resetPwdBtn;

    private EventHandler eventHandler;
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int tag = msg.what;
            switch (tag){
                case UPDATE_SUCCESS:
                    toLogin();
                    break;
                case EXIST:
                    SMSSDK.getVerificationCode("+86", accountEt.getText().toString()); //获取验证码
                    new TimerUtil(MyApplication.getContext(), getVerificationCodeBtn, 60000, 1000).start(); //开始倒计时
                    break;
                case NOT_EXIST:
                    showToast("账号不存在");
                    break;
                case MESSAGE_GET_SUCCESS:
                    showToast("获取短信验证码成功");
                    break;
                case MESSAGE_GET_ERROR:
                    showToast("获取短信验证码失败");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        //初始化视图
        initViews();
        //初始化事件
        initEvents();
    }

    private void initViews(){
        toolbar = findViewById(R.id.tool_bar);
        //不要忘记设置工具栏
        setSupportActionBar(toolbar);

        accountEt = findViewById(R.id.et_account);
        verificationCodeEt = findViewById(R.id.et_verification_code);
        newPwdEt = findViewById(R.id.et_password_new);
        getVerificationCodeBtn = findViewById(R.id.btn_verification_code_get);
        resetPwdBtn = findViewById(R.id.btn_password_reset);

        //初始化账号
        accountEt.setText(getIntent().getStringExtra("account"));
    }

    private void initEvents(){

        eventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //成功回调
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交短信、语音验证码成功
                        //服务端更新
                        update(accountEt.getText().toString(), newPwdEt.getText().toString());
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取短信验证码成功
                        Message message = new Message();
                        message.what = MESSAGE_GET_SUCCESS;
                        handler.sendMessage(message);
                    }
                } else if (result == SMSSDK.RESULT_ERROR) {
                    //失败回调：短信发送失败
                    Message message = new Message();
                    message.what = MESSAGE_GET_ERROR;
                    handler.sendMessage(message);
                } else {
                    //其他失败回调
                    ((Throwable) data).printStackTrace();
                }
            }
        };
        //注册短信回调
        SMSSDK.registerEventHandler(eventHandler);

        getVerificationCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account = accountEt.getText().toString().trim();
                String password = newPwdEt.getText().toString().trim();

                if(StringUtil.isEmpty(account)){
                    showToast("请输入手机号");
                }else if(!StringUtil.isPhone(account)){
                    showToast("请输入正确的手机号");
                }else if(StringUtil.isEmpty(password)){
                    showToast("请输入新密码");
                }else if(!StringUtil.isPassword(password)){
                    showToast("请输入正确的新密码");
                }else{
                    //服务端查重
                    isRegistered(account);
                }
            }
        });

        resetPwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account = accountEt.getText().toString().trim();
                String password = newPwdEt.getText().toString().trim();
                String verificationCode = verificationCodeEt.getText().toString().trim();
                if(StringUtil.isEmpty(account)){
                    showToast("请输入手机号");
                }else if(!StringUtil.isPhone(account)){
                    showToast("请输入正确的手机号");
                }else if(StringUtil.isEmpty(password)){
                    showToast("请输入新密码");
                }else if(!StringUtil.isPassword(password)){
                    showToast("请输入正确的新密码");
                } else if(StringUtil.isEmpty(verificationCode)){
                    showToast("请输入验证码");
                }else{
                    SMSSDK.submitVerificationCode("+86", account, verificationCode);
                }
            }
        });
    }

    //服务端查重，是否已注册
    private void isRegistered(String account){
        HTTPUtil.isRegistered(account, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e(account, " request error " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d(account, "request success");

                Gson gson = new Gson();
                Result result = gson.fromJson(response.body().string(), Result.class);

                Message message = new Message();
                if(result.getCode() == 1){
                    message.what = EXIST;
                    handler.sendMessage(message);
                    LogUtil.d(account, "already registered");
                }else {
                    message.what = NOT_EXIST;
                    handler.sendMessage(message);
                    LogUtil.d(account, "unregistered");
                }
            }
        });
    }

    //更新服务端数据库
    private void update(String account, String password){
        HTTPUtil.resetPwd(account, password, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e(account, " request error " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d(account, "request success");

                Gson gson = new Gson();
                Result result = gson.fromJson(response.body().string(), Result.class);

                Message message = new Message();
                if(result.getCode() == 1){
                    message.what = UPDATE_SUCCESS;
                    handler.sendMessage(message);
                    LogUtil.d(account, "update success");
                }
            }
        });
    }

    //返回登录，更新本地数据，记录配置信息
    private void toLogin(){
        //获取登录信息
        String account = accountEt.getText().toString().trim();
        String password = newPwdEt.getText().toString().trim();
        //更新SQLite本地数据库
        User user = new User();
        user.setPassword(password);
        user.updateAll("account = ?", account);
        //更新配置信息
        SharedPreferenceUtil.setParam("current_password", password);
        //返回登录页
        Intent resutltIntent = new Intent();
        resutltIntent.putExtra("account", accountEt.getText().toString())
                .putExtra("password", newPwdEt.getText().toString());
        setResult(Activity.RESULT_OK, resutltIntent);
        finish();
        showToast("重置密码成功");
    }

    //获取工具栏菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_close, menu);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 使用完EventHandler需注销，否则可能出现内存泄漏
        SMSSDK.unregisterEventHandler(eventHandler);
    }
}