package com.srdp.harmonystride.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
//import com.hyphenate.chat.EMClient;
import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.dialog.TipDialog;
import com.srdp.harmonystride.entity.Result;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.HTTPUtil;
import com.srdp.harmonystride.util.LogUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SettingActivity extends BaseActivity {
    private static final int DELETE = 1; //注销成功

    private Toolbar toolbar;

    private TextView passwordEditTv;
    private TextView accountDeleteTv;

    private Switch notificationSystemSw;
    private Switch notificationUnfocusSw;

    private Switch privacyAllSw;
    private Switch privacyPostSw;
    private Switch privacyApplySw;
    private Switch privacyCommentSw;

    private Switch postAllowOpenSw;
    private Switch postImageWaterMarkSw;

    private TextView logoutTv;

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case DELETE:
                    //下次开屏页结束后进入登录页
                    SharedPreferenceUtil.setParam("is_login", false);
                    //登出IM
                    //EMClient.getInstance().logout(true);
                    navigateTo(LoginActivity.class);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //初始化视图
        initViews();
        //初始化事件
        initEvents();
    }

    private void initViews() {
        toolbar = findViewById(R.id.tool_bar);

        passwordEditTv = findViewById(R.id.tv_setting_password_edit);
        accountDeleteTv = findViewById(R.id.tv_setting_account_delete);

        notificationSystemSw = findViewById(R.id.sw_setting_notification_system);
        notificationSystemSw.setChecked((Boolean)SharedPreferenceUtil.getParam("notification_system", false));
        notificationUnfocusSw = findViewById(R.id.sw_setting_notification_unfocus);
        notificationUnfocusSw.setChecked((Boolean)SharedPreferenceUtil.getParam("notification_unfocus", false));

        privacyAllSw = findViewById(R.id.sw_setting_privacy_all);
        privacyAllSw.setChecked((Boolean)SharedPreferenceUtil.getParam("privacy_all", false));
        privacyPostSw = findViewById(R.id.sw_setting_privacy_post);
        privacyPostSw.setChecked((Boolean)SharedPreferenceUtil.getParam("privacy_post", false));
        privacyApplySw = findViewById(R.id.sw_setting_privacy_apply);
        privacyApplySw.setChecked((Boolean)SharedPreferenceUtil.getParam("privacy_apply", false));
        privacyCommentSw = findViewById(R.id.sw_setting_privacy_comment);
        privacyCommentSw.setChecked((Boolean)SharedPreferenceUtil.getParam("privacy_comment", false));
        if(!privacyAllSw.isChecked()){
            privacyPostSw.setVisibility(View.GONE);
            privacyApplySw.setVisibility(View.GONE);
            privacyCommentSw.setVisibility(View.GONE);
        }

        postAllowOpenSw = findViewById(R.id.sw_setting_post_allow_open);
        postAllowOpenSw.setChecked((Boolean)SharedPreferenceUtil.getParam("post_allow_open", false));
        postImageWaterMarkSw = findViewById(R.id.sw_setting_post_image_watermark);
        postImageWaterMarkSw.setChecked((Boolean)SharedPreferenceUtil.getParam("post_image_watermark", false));

        logoutTv = findViewById(R.id.tv_setting_logout);
    }

    private void initEvents() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //修改密码
        passwordEditTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyApplication.getContext(), PasswordResetActivity.class);
                intent.putExtra("account", SharedPreferenceUtil.getParam("current_account", "").toString());
                navigateTo(intent);
            }
        });
        //删除账号
        accountDeleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TipDialog(SettingActivity.this, "是否注销账号？请谨慎操作！", new TipDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(Boolean isConfirm) {
                        if(isConfirm){
                            //删除账号
                            HTTPUtil.deleteUserById((int) SharedPreferenceUtil.getParam("current_uid", 0), new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    LogUtil.e("delete request", "error" + e);
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    LogUtil.d("delete request", "success");
                                    Gson gson = new Gson();
                                    String responseBody = response.body().string();
                                    Result result = gson.fromJson(responseBody, Result.class);

                                    Message message = new Message();
                                    if(result.getCode() == 1){
                                        message.what = DELETE;
                                        handler.sendMessage(message);
                                        LogUtil.d("delete user", "success");
                                    }
                                }
                            });
                        }
                    }
                }).show();
            }
        });

        //其他设置事件
        notificationSystemSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferenceUtil.setParam("notification_system", isChecked);
            }
        });

        notificationUnfocusSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferenceUtil.setParam("notification_unfocus", isChecked);
            }
        });

        privacyAllSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    privacyPostSw.setVisibility(View.VISIBLE);
                    privacyApplySw.setVisibility(View.VISIBLE);
                    privacyCommentSw.setVisibility(View.VISIBLE);
                }else{
                    privacyPostSw.setVisibility(View.GONE);
                    privacyApplySw.setVisibility(View.GONE);
                    privacyCommentSw.setVisibility(View.GONE);
                }
                SharedPreferenceUtil.setParam("pricacy_all", isChecked);
            }
        });

        privacyPostSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferenceUtil.setParam("privacy_post", isChecked);
            }
        });

        privacyApplySw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferenceUtil.setParam("privacy_apply", isChecked);
            }
        });

        privacyCommentSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferenceUtil.setParam("privacy_comment", isChecked);
            }
        });

        postAllowOpenSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferenceUtil.setParam("post_allow_open", isChecked);
            }
        });

        postImageWaterMarkSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferenceUtil.setParam("post_image_watermark", isChecked);
            }
        });

        //退出登录
        logoutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TipDialog(SettingActivity.this, "是否退出登录？", new TipDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(Boolean isConfirm) {
                        if(isConfirm){
                            Intent intent = new Intent(MyApplication.getContext(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            navigateTo(intent);
                            finish();
                            //下次开屏页结束后进入登录页
                            SharedPreferenceUtil.setParam("is_login", false);
                            //登出IM
                            //EMClient.getInstance().logout(true);
                        }
                    }
                }).show();
            }
        });
    }
}