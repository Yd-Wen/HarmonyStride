package com.srdp.harmonystride.dialog;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.nsd.NsdManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.srdp.harmonystride.R;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.ScreenSizeUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;

import org.litepal.LitePal;

import java.util.List;

public class EditTextDialog extends BaseDialog {
    private TextView titleTv;
    private EditText contentEt;
    private String editKey;
    private String content;

    private User curUser;

    /**
     * 自定义Dialog监听器
     */
    public interface OnDismissListener {
        /**
         * 回调函数，用于在Dialog的监听事件触发后刷新Activity的UI显示
         */
        public void onDismiss();
    }

    private OnDismissListener onDismissListener;

    public EditTextDialog(Context context, String editKey, OnDismissListener onDismissListener) {
        super(context);
        this.editKey = editKey;
        this.onDismissListener = onDismissListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(View.inflate(baseContext, R.layout.dialog_edit_text, null));
        //初始化数据
        initDatas();
        //初始化视图
        initViews();
        //初始化事件
        initEvents();
        //加载数据
        loadDatas();
    }

    private void initDatas(){
        //读取当前用户
        List<User> users = LitePal.where("account = ?", SharedPreferenceUtil.getParam(baseContext, "current_account", "").toString()).find(User.class);
        curUser = users.get(0);
    }

    private void initViews(){
        titleTv = findViewById(R.id.tv_dialog_title);
        contentEt = (findViewById(R.id.et_dialog_content));
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
            }
        });

        getConfirmBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newContent = contentEt.getText().toString().trim();
                if(!newContent.equals(content)){
                    //更新本地数据库
                    User user = new User();
                    if(editKey.equals("nickname")){
                        //修改昵称
                        user.setNickname(newContent);
                    }else {
                        //修改签名
                        user.setIntroduction(newContent);
                    }
                    user.updateAll("account = ?", curUser.getAccount());
                    //回调方法
                    onDismissListener.onDismiss();
                }
                dismiss();
            }
        });
    }

    private void loadDatas(){
        if(editKey.equals("nickname")){
            titleTv.setText(R.string.nickname_edit);
            contentEt.setHint(R.string.nickname_edit_hint);
            content = curUser.getNickname();
            contentEt.setText(content);
        }else if(editKey.equals("introduction")) {
            titleTv.setText(R.string.introduction_edit);
            contentEt.setHint(R.string.introduction_edit_hint);
            content = curUser.getIntroduction();
            contentEt.setText(content);
        }
    }

}
