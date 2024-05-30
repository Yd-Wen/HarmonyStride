package com.srdp.harmonystride.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.util.ScreenSizeUtil;

public class TipDialog extends BaseDialog {

    private String content;

    public TipDialog(Context context){
        super(context);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void show() {
        super.show();
        //设置布局
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.gravity = gravity;
        lp.width = (int) (ScreenSizeUtil.getInstance(MyApplication.getContext()).getScreenWidth() * sizeRatio);
        if(content.length() > 300){
            lp.height = (int) (ScreenSizeUtil.getInstance(MyApplication.getContext()).getScreenHeight() * sizeRatio);
        }else lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
    }
}
