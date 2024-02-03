package com.srdp.harmonystride.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.srdp.harmonystride.R;
import com.srdp.harmonystride.activity.LoginActivity;
import com.srdp.harmonystride.activity.WelcomeActivity;
import com.srdp.harmonystride.util.ScreenSizeUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;

public class PrivacyDialog extends BaseDialog{

    private TextView contentTv;

    public PrivacyDialog(Context context) {
        super(context);
        setContentView(View.inflate(baseContext, R.layout.dialog_privacy, null));
        //初始化视图
        initViews();
        //初始化事件
        initEvents();

        show();
    }

    public TextView getContentTv() {
        return contentTv;
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
        contentTv = findViewById(R.id.tv_dialog_content);
        setCancelBtn(findViewById(R.id.btn_dialog_cancel));
        setConfirmBtn(findViewById(R.id.btn_dialog_confirm));

        //设置布局
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtil.getInstance(baseContext).getScreenWidth() * 0.8f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
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
                SharedPreferenceUtil.setParam(baseContext, "privacy_agree", true);
                ((WelcomeActivity) baseContext).navigateTo(LoginActivity.class);
                ((WelcomeActivity) baseContext).finish();
            }
        });
    }
}
