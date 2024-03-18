package com.srdp.harmonystride.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.util.ScreenSizeUtil;

public class TipDialog extends BaseDialog{
    private TextView contentTv;
    private String content;

    /**
     * 自定义Dialog监听器
     */
    public interface OnDismissListener {
        /**
         * 回调函数，用于在Dialog的监听事件触发后刷新Activity的UI显示
         */
        public void onDismiss(Boolean isConfirm);
    }

    private TipDialog.OnDismissListener onDismissListener;

    public TipDialog(Context context, String content, OnDismissListener onDismissListener) {
        super(context);
        this.onDismissListener = onDismissListener;
        this.content = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(View.inflate(MyApplication.getContext(), R.layout.dialog_tip, null));
        //初始化视图
        initViews();
        //初始化事件
        initEvents();
    }

    private void initViews(){
        contentTv = findViewById(R.id.tv_content);
        contentTv.setText(content);
        setCancelBtn(findViewById(R.id.btn_dialog_cancel));
        setConfirmBtn(findViewById(R.id.btn_dialog_confirm));
        //设置布局
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtil.getInstance(MyApplication.getContext()).getScreenWidth() * 0.8f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
    }

    private void initEvents(){
        getCancelBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDismissListener.onDismiss(false);
                dismiss();
            }
        });

        getConfirmBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDismissListener.onDismiss(true);
                dismiss();
            }
        });
    }

}
