package com.srdp.harmonystride.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import com.google.gson.Gson;
import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.entity.Result;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.HTTPUtil;
import com.srdp.harmonystride.util.ImageUtil;
import com.srdp.harmonystride.util.LogUtil;
import com.srdp.harmonystride.util.RongIMUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;
import com.srdp.harmonystride.util.StringUtil;
import com.srdp.harmonystride.util.TimerUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends BaseActivity {
    private static final int REGISTER_SUCCESS = 1; //注册成功
    private static final int GET_TOKEN_SUCCESS = 2; //注册IM成功
    private static final int EXIST = 4; //账号已存在
    public static final int NOT_EXIST = 5; //账号不存在
    private static final int MESSAGE_GET_SUCCESS = 6; //短信发送成功
    private static final int MESSAGE_GET_ERROR = 7; //短信发送失败

    private ImageView closeIv;
    private EditText accountEt;
    private EditText passwordEt;
    private EditText verificationCodeEt;
    private Button getVerificationCodeBtn;
    private Button registerBtn;
    private CheckBox privacyAgreeCb;

    private User user;

    private EventHandler eventHandler;
    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case REGISTER_SUCCESS:
                    getUserToken(user);
                    break;
                case GET_TOKEN_SUCCESS:
                    //返回登录页
                    Intent resutltIntent = new Intent();
                    resutltIntent.putExtra("account", accountEt.getText().toString())
                            .putExtra("password", passwordEt.getText().toString());
                    setResult(Activity.RESULT_OK, resutltIntent);
                    finish();
                    showToast("注册成功，请登录");
                    break;
                case EXIST:
                    showToast("账号已注册");
                    break;
                case NOT_EXIST:
                    SMSSDK.getVerificationCode("+86", accountEt.getText().toString()); //获取验证码
                    new TimerUtil(MyApplication.getContext(), getVerificationCodeBtn, 60000, 1000).start(); //开始倒计时
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
        setContentView(R.layout.activity_register);

        //初始化视图
        initViews();
        //初始化事件
        initEvents();
    }

    private void initViews(){
        closeIv = findViewById(R.id.iv_close);
        accountEt = findViewById(R.id.et_account);
        passwordEt = findViewById(R.id.et_password);
        verificationCodeEt = findViewById(R.id.et_verification_code);
        getVerificationCodeBtn = findViewById(R.id.btn_verification_code_get);
        registerBtn = findViewById(R.id.btn_register);
        privacyAgreeCb = findViewById(R.id.cb_privacy_agree);
    }

    private void initEvents(){
        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        eventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //成功回调
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交短信、语音验证码成功
                        //上传头像
                        String imageUrl = "user/" + accountEt.getText().toString() + "/" + System.currentTimeMillis() + ".png";
                        Bitmap image = ImageUtil.getBitmapFromResourceId(R.drawable.default_avatar);
                        ImageUtil.upload("", image, imageUrl, null);
                        //服务端注册
                        user = new User(accountEt.getText().toString(), passwordEt.getText().toString(), imageUrl);
                        register(user);
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
                String password = passwordEt.getText().toString().trim();

                if(StringUtil.isEmpty(account)){
                    showToast("请输入手机号");
                }else if(!StringUtil.isPhone(account)){
                    showToast("请输入正确的手机号");
                }else if(StringUtil.isEmpty(password)){
                    showToast("请输入密码");
                }else if(!StringUtil.isPassword(password)){
                    showToast("请输入正确的密码");
                } else if(!privacyAgreeCb.isChecked()){
                    showToast("请同意隐私协议");
                }else{
                    //服务端查重
                    isRegistered(account);
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account = accountEt.getText().toString().trim();
                String password = passwordEt.getText().toString().trim();
                String verificationCode = verificationCodeEt.getText().toString().trim();

                if(StringUtil.isEmpty(account)){
                    showToast("请输入手机号");
                }else if(!StringUtil.isPhone(account)){
                    showToast("请输入正确的手机号");
                }else if(StringUtil.isEmpty(password)){
                    showToast("请输入密码");
                }else if(!StringUtil.isPassword(password)){
                    showToast("请输入正确的密码");
                } else if(!privacyAgreeCb.isChecked()){
                    showToast("请同意隐私协议");
                }else if(StringUtil.isEmpty(verificationCode)){
                    showToast("请输入验证码");
                }else{
                    //进行短信验证
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

    //将注册用户写入数据库
    private void register(User user){
        //向服务端发起请求
        HTTPUtil.insert(user, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e(user.getAccount(), "request error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d(user.getAccount(), "request success");

                Gson gson = new Gson();
                Result result = gson.fromJson(response.body().string(), Result.class);
                if(result.getCode() == 1){
                    Message message = new Message();
                    message.what = REGISTER_SUCCESS;
                    handler.sendMessage(message);
                    LogUtil.d(user.getAccount(), "register success");
                }
            }
        });
    }

    //向融云IM服务器注册
    private void getUserToken(User user){
        String TAG = "registerIM";
        RongIMUtil.register(user, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e(TAG, "error:" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d(TAG, "success");
                if(response.code() == 200){
                    String responseStr = response.body().string();
                    Result result = RongIMUtil.gson.fromJson(responseStr, Result.class);
                    if(result.getCode() == 1){
                        try {
                            JSONObject jsonObject = new JSONObject(responseStr);
                            String token = jsonObject.getJSONObject("data").getString("token");
                            SharedPreferenceUtil.setParam("user_token", token);
                            Message message = new Message();
                            message.what = GET_TOKEN_SUCCESS;
                            handler.sendMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    LogUtil.e(TAG, "error:" + response.code());
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 使用完EventHandler需注销，否则可能出现内存泄漏
        SMSSDK.unregisterEventHandler(eventHandler);
    }

}
