package com.srdp.harmonystride.activity;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import com.srdp.harmonystride.R;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.HTTPUtil;
import com.srdp.harmonystride.util.StringUtil;
import com.srdp.harmonystride.util.TimerUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.wrapper.TokenVerifyResult;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PasswordResetActivity extends BaseActivity {

    private Toolbar toolbar;
    private EditText accountEt;
    private EditText newPwdEt;
    private EditText verificationCodeEt;
    private Button getVerificationCodeBtn;
    private Button resetPwdBtn;

    private EventHandler eventHandler;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int tag = msg.what;
            switch (tag){
                case 1:
                    Log.d("Codr","重置密码成功");
                    showToast("重置密码成功，请登录");

                    String account = accountEt.getText().toString().trim();
                    String password = newPwdEt.getText().toString().trim();

                    //TODO：更新服务端数据库,等待测试
                    showToast("正在验证...");
                    Map map=new HashMap();
                    map.put("account",account);
                    map.put("new_password",password);
                    String url= "/user/reset_password";
                    HTTPUtil.POST(url, "", map, new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(TAG, e.toString());
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result = response.body().string();
                            PasswordResetActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject jsonObject = new JSONObject(result);
                                        if(jsonObject.getString("status").equals("success")){
                                            //更新SQLite本地数据库
                                            User user = new User();
                                            user.setPassword(password);
                                            user.updateAll("account = ?", account);
                                            //返回登录页
                                            Intent resutltIntent = new Intent();
                                            resutltIntent.putExtra("account", account)
                                                    .putExtra("password", password);
                                            setResult(Activity.RESULT_OK, resutltIntent);
                                            finish();
                                        }else{
                                            showToast(jsonObject.getString("info"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                    break;
                case 2:
                    Log.d("Codr","短信已发送");
                    showToast("短信已发送");
                    break;
                case 3:
                    Log.d("Codr","获取短信验证码失败");
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

        baseContext = this;

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
    }

    private void initEvents(){

        eventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //成功回调
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交短信、语音验证码成功
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取短信验证码成功
                        Message message = new Message();
                        message.what = 2;
                        handler.sendMessage(message);
                    } else if (event == SMSSDK.EVENT_GET_VOICE_VERIFICATION_CODE) {
                        //获取语音验证码成功
                        Message message = new Message();
                        message.what = 2;
                        handler.sendMessage(message);
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                    }else if (event == SMSSDK.EVENT_GET_VERIFY_TOKEN_CODE) {
                        //本机验证获取token成功
                        TokenVerifyResult tokenVerifyResult = (TokenVerifyResult) data;
                        //SMSSDK.login(phoneNum,tokenVerifyResult);
                    }else if (event == SMSSDK.EVENT_VERIFY_LOGIN) {
                        //本机验证登陆成功
                    }
                } else if (result == SMSSDK.RESULT_ERROR) {
                    //失败回调
                    Message message = new Message();
                    message.what = 3;
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
                }
//                else if(!userRegistered(account)){//TODO：服务端查重，是否已经注册过,等待测试
//                    showToast("该账号未注册，请先注册");
//                }
                else if(StringUtil.isEmpty(password)){
                    showToast("请输入新密码");
                }else if(!StringUtil.isPassword(password)){
                    showToast("请输入正确的新密码");
                }else{
                    Map map=new HashMap();
                    map.put("account",account);
                    String url= "/user/is_registered";
                    HTTPUtil.POST(url, "", map, new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(TAG, e.toString());
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result = response.body().string();
                            PasswordResetActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject jsonObject = new JSONObject(result);
                                        if(jsonObject.getString("status").equals("success")){
                                            SMSSDK.getVerificationCode("+86", account); //获取验证码
                                            new TimerUtil(baseContext, (Button) view, 60000, 1000).start(); //开始倒计时
                                        }else{
                                            showToast(jsonObject.getString("该账号未注册，请先注册"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
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

    //DONE：服务端查查，是否已经注册
    private boolean userRegistered(String account){

        return true;
    }

    //DONE：更新服务端数据库
    private boolean updateUser(User user){

        return true;
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