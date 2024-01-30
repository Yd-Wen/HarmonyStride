package com.srdp.harmonystride.activity;

import static com.mob.MobSDK.submitPolicyGrantResult;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.srdp.harmonystride.R;
import com.srdp.harmonystride.util.StringUtil;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.wrapper.TokenVerifyResult;

public class RegisterActivity extends BaseActivity {

    private Toolbar toolbar;
    private EditText accountEt;
    private EditText passwordEt;
    private EditText verificationCodeEt;
    private Button getVerificationCodeBtn;
    private Button registerBtn;
    private CheckBox privacyAgreeCb;

    private EventHandler eventHandler;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int tag = msg.what;
            switch (tag){
                case 1:
                    Log.d("Codr","注册成功");
                    showToast("注册成功");

                    Intent resutltIntent = new Intent();

                    resutltIntent.putExtra("account", accountEt.getText().toString().trim())
                            .putExtra("password", passwordEt.getText().toString().trim());
                    setResult(Activity.RESULT_OK, resutltIntent);
                    finish();
                    break;
                case 2:
                    Log.d("Codr","获取短信验证码成功");
                    showToast("获取短信验证码成功");
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
    private TimeCount timeCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /**
         * com.mob.MobSDK.class
         * 回传用户隐私授权结果
         * @param isGranted     用户是否同意隐私协议
         */
        submitPolicyGrantResult(true);

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
        passwordEt = findViewById(R.id.et_password);
        verificationCodeEt = findViewById(R.id.et_verification_code);
        getVerificationCodeBtn = findViewById(R.id.btn_verification_code_get);
        registerBtn = findViewById(R.id.btn_register);
        privacyAgreeCb = findViewById(R.id.cb_privacy_agree);
    }

    public void initEvents(){
        timeCount = new TimeCount(60000, 1000);

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
                if(StringUtil.isEmpty(account)){
                    showToast("请输入手机号");
                }else if(!StringUtil.isPhone(account)){
                    showToast("请输入正确的手机号");
                } else if(!privacyAgreeCb.isChecked()){
                    showToast("请同意隐私协议");
                }else{
                    SMSSDK.getVerificationCode("+86", account); //获取验证码
                    timeCount.start(); //开始倒计时
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account = accountEt.getText().toString().trim();
                String verificationCode = verificationCodeEt.getText().toString().trim();

                if(StringUtil.isEmpty(account)){
                    showToast("请输入手机号");
                }else if(!StringUtil.isPhone(account)){
                    showToast("请输入正确的手机号");
                }else if(!privacyAgreeCb.isChecked()){
                    showToast("请同意隐私协议");
                }else if(StringUtil.isEmpty(verificationCode)){
                    showToast("请输入验证码");
                }else{
                    SMSSDK.submitVerificationCode("+86", account, verificationCode);
                }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 使用完EventHandler需注销，否则可能出现内存泄漏
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    /**
     * 计时器
     */
    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            getVerificationCodeBtn.setClickable(false);
            getVerificationCodeBtn.setText(l/1000 + "秒后重试");
            getVerificationCodeBtn.setBackground(getDrawable(R.drawable.selector_shape_button_grey));
        }

        @Override
        public void onFinish() {
            getVerificationCodeBtn.setClickable(true);
            getVerificationCodeBtn.setText(R.string.verification_code_get);
            getVerificationCodeBtn.setBackground(getDrawable(R.drawable.selector_shape_button_orange));
        }
    }
}
