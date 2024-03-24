package com.srdp.harmonystride.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
//import com.hyphenate.EMCallBack;
//import com.hyphenate.chat.EMClient;
import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.entity.Result;
import com.srdp.harmonystride.util.HTTPUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.IMUtil;
import com.srdp.harmonystride.util.LogUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;
import com.srdp.harmonystride.util.StringUtil;

import org.litepal.LitePal;

public class LoginActivity extends BaseActivity {
    private static final int GET_IM_USER_TOKEN_SUCCESS = 0; //获取IM用户Token成功
    private static final int LOGIN_IM_SUCCESS = 1; //登录IM成功
    private static final int NOT_EXIST = 2; //用户不存在
    private static final int EXIST = 3; //用户存在

    private User curUser;

    private EditText accountEt;
    private EditText passwordEt;
    private Button registerBtn;
    private Button loginBtn;
    private CheckBox rememberPwdCb;
    private TextView forgetPwdTv;

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET_IM_USER_TOKEN_SUCCESS:
                    loginIM(msg.obj.toString());
                    break;
                case LOGIN_IM_SUCCESS:
                    login();
                    break;
                case NOT_EXIST:
                    showToast("账号或密码错误");
                    break;
                case EXIST:
                    getIMUserToken();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //从注册页返回
        setActivityResultLauncher(
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // 处理返回的数据
                        Intent data = result.getData();
                        // 进行你的操作
                        accountEt.setText(data.getStringExtra("account"));
                        passwordEt.setText(data.getStringExtra("password"));
                        rememberPwdCb.setChecked((Boolean)SharedPreferenceUtil.getParam("password_remember", false));
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
                if (StringUtil.isEmpty(account)) {
                    showToast("请输入手机号");
                }else if(!StringUtil.isPhone(account)){
                    showToast("手机号错误");
                }else if(StringUtil.isEmpty(password)){
                    showToast("请输入密码");
                }else if(!StringUtil.isPassword(password)){
                    showToast("密码错误");
                }else {
                    //服务端验证账号密码
                    verify(account,password);
                }

            }
        });

        forgetPwdTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyApplication.getContext(), PasswordResetActivity.class);
                intent.putExtra("account", accountEt.getText().toString());
                navigateForResult(intent);
            }
        });
    }

    //服务端查重，是否已注册
    private void verify(String account, String password){
        HTTPUtil.verify(account, password, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e(account, " request error " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d(account, "request success");

                Gson gson = new Gson();
                String responseBody = response.body().string();
                Result result = gson.fromJson(responseBody, Result.class);

                Message message = new Message();
                if(result.getCode() == 1){
                    JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                    JsonObject data = jsonObject.getAsJsonObject("data");
                    //注意不要用toString，否则引号会保留到字符串
                    curUser = new User(accountEt.getText().toString(), passwordEt.getText().toString());
                    curUser.setUid(data.get("id").getAsInt());
                    curUser.setAvatar(data.get("avatar").getAsString());
                    curUser.setNickname(data.get("nickname").getAsString());
                    curUser.setCertify(data.get("certify").getAsString());
                    curUser.setGender(data.get("gender").getAsString());
                    curUser.setLocation(data.get("location").getAsString());
                    curUser.setIntroduction(data.get("introduction").getAsString());
                    curUser.setFocus(data.get("focus").getAsString());
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

    //获取IM用户的TOKEN
    private void getIMUserToken(){
        IMUtil.getUserToken(accountEt.getText().toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("get user " + accountEt.getText().toString() + " token", "error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d("get user " + accountEt.getText().toString() + " token", "success");
                if(response.code() == 200){
                    //获取请求体
                    String responseBody = response.body().string();
                    //转换为json对象
                    JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                    //传递消息
                    Message message = new Message();
                    message.what = GET_IM_USER_TOKEN_SUCCESS;
                    message.obj = jsonObject.get("access_token").getAsString();
                    handler.sendMessage(message);
                    LogUtil.d("get user token success", message.obj.toString());
                }else {
                    LogUtil.e("register error", String.valueOf(response.code()));
                }
            }
        });
    }

    //登录IM用户
    private void loginIM(String token){
        //登录IM
//        EMClient.getInstance().loginWithToken(accountEt.getText().toString(), token, new EMCallBack() {
//            // 登录成功回调
//            @Override
//            public void onSuccess() {
//                LogUtil.d("login IM", "success");
//                Message message = new Message();
//                message.what = LOGIN_IM_SUCCESS;
//                handler.sendMessage(message);
//            }
//
//            // 登录失败回调，包含错误信息
//            @Override
//            public void onError(int code, String error) {
//                LogUtil.e("login IM", code + "-" + error);
//            }
//
//            @Override
//            public void onProgress(int progress, String status) {
//                LogUtil.d("login IM", "login...");
//            }
//        });

        Message message = new Message();
                message.what = LOGIN_IM_SUCCESS;
                handler.sendMessage(message);

    }

    //登录，更新本地数据，记录配置信息
    private void login(){
        //写入配置信息
        SharedPreferenceUtil.setParam("current_uid", curUser.getUid());
        SharedPreferenceUtil.setParam("current_account", curUser.getAccount());
        SharedPreferenceUtil.setParam("current_password", curUser.getPassword());
        SharedPreferenceUtil.setParam("password_remember", rememberPwdCb.isChecked());
        //下次开屏页结束后进入主页
        SharedPreferenceUtil.setParam("is_login", true);
        //写入默认的设置信息
        SharedPreferenceUtil.setParam("notification_system", true);
        SharedPreferenceUtil.setParam("notification_unfocus", false);
        SharedPreferenceUtil.setParam("privacy_all", true);
        SharedPreferenceUtil.setParam("privacy_post", true);
        SharedPreferenceUtil.setParam("privacy_apply", true);
        SharedPreferenceUtil.setParam("privacy_comment", true);
        SharedPreferenceUtil.setParam("post_allow_open", true);
        SharedPreferenceUtil.setParam("post_image_watermark", true);

        //写入SQLite本地数据库
        if(!LitePal.isExist(User.class, "account = ?", curUser.getAccount())){
            curUser.save();
        }

        //跳转到主页
        navigateTo(MainActivity.class);
        finish();
        showToast("登录成功");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //加载配置信息（二次登录）
        accountEt.setText((String)SharedPreferenceUtil.getParam("current_account", ""));
        rememberPwdCb.setChecked((Boolean)SharedPreferenceUtil.getParam("password_remember", false));
        //是否记住密码
        if(rememberPwdCb.isChecked()){
            passwordEt.setText((String)SharedPreferenceUtil.getParam("current_password", ""));
        }
    }
}

