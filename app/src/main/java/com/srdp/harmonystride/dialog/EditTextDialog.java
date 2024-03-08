package com.srdp.harmonystride.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.ScreenSizeUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;

import org.litepal.LitePal;

import java.util.List;

public class EditTextDialog extends BaseDialog {
    public static final int EDIT_TYPE_NICKNAME = 1;
    public static final int EDIT_TYPE_INTRODUCTION = 2;
    public static final int EDIT_TYPE_CERTIFY_NUMBER = 3;

    private TextView titleTv;
    private EditText contentEt;

    private int type;
    private String curContent;

    /**
     * 自定义Dialog监听器
     */
    public interface OnDismissListener {
        /**
         * 回调函数，用于在Dialog的监听事件触发后刷新Activity的UI显示
         */
        public void onDismiss(Boolean isUpdate, String data);
    }

    private OnDismissListener onDismissListener;

    public EditTextDialog(Context context, int type, String curContent, OnDismissListener onDismissListener) {
        super(context);
        this.type = type;
        this.curContent = curContent;
        this.onDismissListener = onDismissListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(View.inflate(MyApplication.getContext(), R.layout.dialog_edit_text, null));
        //初始化视图
        initViews();
        //初始化事件
        initEvents();
        //加载数据
        loadDatas();
    }

    private void initViews(){
        titleTv = findViewById(R.id.tv_dialog_title);
        contentEt = (findViewById(R.id.et_dialog_content));
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
                dismiss();
            }
        });

        getConfirmBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newContent = contentEt.getText().toString().trim();
                if(!newContent.equals(curContent)){
                    //回调方法
                    onDismissListener.onDismiss(true, newContent);
                }else {
                    //回调方法
                    onDismissListener.onDismiss(false, null);
                }
                dismiss();
            }
        });
    }

    private void loadDatas(){
        switch (type){
            case EDIT_TYPE_NICKNAME:
                titleTv.setText(R.string.nickname_edit);
                contentEt.setHint(R.string.nickname_edit_hint);
                contentEt.setText(curContent);
                break;
            case EDIT_TYPE_INTRODUCTION:
                titleTv.setText(R.string.introduction_edit);
                contentEt.setHint(R.string.introduction_edit_hint);
                contentEt.setText(curContent);
                break;
            case EDIT_TYPE_CERTIFY_NUMBER:
                titleTv.setText(R.string.certify_number_edit);
                contentEt.setHint(R.string.certify_number_edit_hint);
                contentEt.setText(curContent);
                break;
            default:
                break;
        }
    }

}
