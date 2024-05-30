package com.srdp.harmonystride.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.util.ScreenSizeUtil;

public abstract class BaseDialog extends Dialog {
    //自定义Dialog监听器
    public interface MyDialogListener {
        //回调函数，用于在Dialog的监听事件触发后刷新Activity的UI显示
        void onClick(Boolean isConfirm, String data);
    }

    private View view;

    protected double sizeRatio; //Dialog占屏幕比例
    protected int gravity; //Dialog布局位置

    private Context context;
    private int icon;
    private String title;

    private String cancelText;
    private String confirmText;

    private MyDialogListener listener = null;

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public double getSizeRatio() {
        return sizeRatio;
    }

    public void setSizeRatio(double sizeRatio) {
        this.sizeRatio = sizeRatio;
    }

    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCancelText() {
        return cancelText;
    }

    public void setCancelText(String cancelText) {
        this.cancelText = cancelText;
    }

    public String getConfirmText() {
        return confirmText;
    }

    public void setConfirmText(String confirmText) {
        this.confirmText = confirmText;
    }


    public MyDialogListener getListener() {
        return listener;
    }

    public void setListener(MyDialogListener listener) {
        this.listener = listener;
    }

    public BaseDialog(Context context){
        super(context, R.style.DialogStyle);
        this.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        view = View.inflate(MyApplication.getContext(), R.layout.dialog_base, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
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
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
    }
}

