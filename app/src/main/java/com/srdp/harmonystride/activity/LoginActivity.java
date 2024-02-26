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
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.entity.Result;
import com.srdp.harmonystride.util.HTTPUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.LogUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;
import com.srdp.harmonystride.util.StringUtil;

import org.litepal.LitePal;

public class LoginActivity extends BaseActivity {
    public static final int NOT_EXIST = 0; //用户不存在
    public static final int EXIST = 1; //用户存在

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
                case NOT_EXIST:
                    showToast("账号或密码错误");
                    break;
                case EXIST:
                    login();
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
                    curUser.setId(data.get("id").getAsInt());
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

    //登录，更新本地数据，记录配置信息
    private void login(){
        //写入SQLite本地数据库
        if(!LitePal.isExist(User.class, "account = ?", curUser.getAccount())){
            curUser.save();
        }
        //写入配置信息
        SharedPreferenceUtil.setParam("current_account", curUser.getAccount());
        SharedPreferenceUtil.setParam("current_password", curUser.getPassword());
        SharedPreferenceUtil.setParam("password_remember", rememberPwdCb.isChecked());
        //下次开屏页结束后进入主页
        SharedPreferenceUtil.setParam("is_login", true);

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

