package com.srdp.harmonystride.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.srdp.harmonystride.R;
import com.srdp.harmonystride.activity.LoginActivity;
import com.srdp.harmonystride.activity.WelcomeActivity;
import com.srdp.harmonystride.util.SharedPreferenceUtil;

public class PrivacyDialog extends BaseDialog{

    private Context context;
    private TextView contentTv;

    public PrivacyDialog(Context context) {
        super(context);
        this.context = context;
        setContentView(R.layout.dialog_privacy);
        //初始化视图
        initViews();
        //初始化事件
        initEvents();

        show();
    }

    public TextView getContentTv() {
        return contentTv;
    }

    public void setContentTv(TextView contentTv) {
        this.contentTv = contentTv;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置透明背景
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //屏蔽返回键
        setCancelable(false);
    }

    private void initViews(){
        setContentTv(findViewById(R.id.tv_dialog_content));
        setCancelBtn(findViewById(R.id.btn_dialog_cancel));
        setConfirmBtn(findViewById(R.id.btn_dialog_ok));
    }

    private void initEvents(){
        getCancelBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                System.exit(0);
            }
        });

        getConfirmBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                SharedPreferenceUtil.setParam(context, "privacy_agree", true);
                ((WelcomeActivity) context).navigateTo(LoginActivity.class);
                ((WelcomeActivity) context).finish();
            }
        });
    }
}
