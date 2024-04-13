package com.srdp.harmonystride.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.util.ImageUtil;
import com.srdp.harmonystride.util.ScreenSizeUtil;

public class RadioButtonDialog extends BaseDialog{
    public static final int EDIT_TYPE_GENDER = 1; //修改性别
    public static final int EDIT_TYPE_CERTIFY = 2; //修改认证
    private TextView titleTv;
    private RadioGroup radioGroup;
    private RadioButton contentRb1;
    private RadioButton contentRb2;
    private RadioButton contentRb3;
    private RadioButton contentRb4;
    private ImageView checkCancelIv;
    private ImageView checkConfirmIv;

    private int type;
    private String curContent;

    /**
     * 自定义Dialog监听器
     */
    public interface OnDismissListener {
        /**
         * 回调函数，用于在Dialog的监听事件触发后刷新Activity的UI显示
         */
        public void onDismiss(Boolean update, String data);
    }

    private RadioButtonDialog.OnDismissListener onDismissListener;

    public RadioButtonDialog(Context context, int type, String curContent, RadioButtonDialog.OnDismissListener onDismissListener) {
        super(context);
        this.type = type;
        this.curContent = curContent;
        this.onDismissListener = onDismissListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(View.inflate(MyApplication.getContext(), R.layout.dialog_radio_button, null));
        //初始化视图
        initViews();
        //初始化事件
        initEvents();
        //加载数据
        loadDatas();
    }

    private void initViews(){
        titleTv = findViewById(R.id.tv_title);
        radioGroup = findViewById(R.id.rg_edit);
        contentRb1 = findViewById(R.id.rb_content_1);
        contentRb2 = findViewById(R.id.rb_content_2);
        contentRb3 = findViewById(R.id.rb_content_3);
        contentRb4 = findViewById(R.id.rb_content_4);
        checkCancelIv = findViewById(R.id.iv_check_cancel);
        checkConfirmIv = findViewById(R.id.iv_check_confirm);
        //显示关闭按钮，隐藏确认按钮
        checkCancelIv.setVisibility(View.VISIBLE);
        checkConfirmIv.setVisibility(View.GONE);

        //显示内容
        switch (type){
            case EDIT_TYPE_GENDER:
                titleTv.setText(R.string.gender_edit);
                contentRb4.setVisibility(View.GONE);
                contentRb1.setText(R.string.gender_unknown);
                contentRb1.setCompoundDrawablesRelativeWithIntrinsicBounds(ImageUtil.getDrawableFromResourceId(R.drawable.gender_u), null, null, null);
                contentRb2.setText(R.string.gender_male);
                contentRb2.setCompoundDrawablesRelativeWithIntrinsicBounds(ImageUtil.getDrawableFromResourceId(R.drawable.gender_m), null, null, null);
                contentRb3.setText(R.string.gender_female);
                contentRb3.setCompoundDrawablesRelativeWithIntrinsicBounds(ImageUtil.getDrawableFromResourceId(R.drawable.gender_f), null, null, null);
                break;
            case EDIT_TYPE_CERTIFY:
                titleTv.setText(R.string.certify_edit);
                contentRb1.setText(R.string.certify_disabled);
                contentRb1.setCompoundDrawablesRelativeWithIntrinsicBounds(ImageUtil.getDrawableFromResourceId(R.drawable.certify_d), null, null, null);
                contentRb2.setText(R.string.certify_volunteer);
                contentRb2.setCompoundDrawablesRelativeWithIntrinsicBounds(ImageUtil.getDrawableFromResourceId(R.drawable.certify_v), null, null, null);
                contentRb3.setText(R.string.certify_employer);
                contentRb3.setCompoundDrawablesRelativeWithIntrinsicBounds(ImageUtil.getDrawableFromResourceId(R.drawable.certify_e), null, null, null);
                contentRb4.setText(R.string.certify_admin);
                contentRb4.setCompoundDrawablesRelativeWithIntrinsicBounds(ImageUtil.getDrawableFromResourceId(R.drawable.certify_a), null, null, null);
                break;
            default:
                break;
        }


        //设置布局
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtil.getInstance(MyApplication.getContext()).getScreenWidth());
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(lp);
    }

    private void initEvents(){


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                RadioButton radioButton = findViewById(id);
                if(radioButton != null){
                    if(!curContent.equals(radioButton.getText().toString())){
                        checkCancelIv.setVisibility(View.GONE);
                        checkConfirmIv.setVisibility(View.VISIBLE);
                    }else {
                        checkCancelIv.setVisibility(View.VISIBLE);
                        checkConfirmIv.setVisibility(View.GONE);
                    }
                }
            }
        });

        checkCancelIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        checkConfirmIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.rb_content_1:
                        curContent = contentRb1.getText().toString();
                        break;
                    case R.id.rb_content_2:
                        curContent = contentRb2.getText().toString();
                        break;
                    case R.id.rb_content_3:
                        curContent = contentRb3.getText().toString();
                        break;
                    case R.id.rb_content_4:
                        curContent = contentRb4.getText().toString();
                        break;
                    default:
                        break;
                }
                //回调方法
                onDismissListener.onDismiss(true, curContent);
                dismiss();
            }
        });
    }

    private void loadDatas(){
        switch (type){
            case EDIT_TYPE_GENDER:
                switch (curContent){
                    case "保密":
                        contentRb1.setChecked(true);
                        break;
                    case "男":
                        contentRb2.setChecked(true);
                        break;
                    case "女":
                        contentRb3.setChecked(true);
                        break;
                    default:
                        break;
                }
                break;
            case EDIT_TYPE_CERTIFY:
                switch (curContent){
                    case "残疾人":
                        contentRb1.setChecked(true);
                        break;
                    case "志愿者":
                        contentRb2.setChecked(true);
                        break;
                    case "用人单位":
                        contentRb3.setChecked(true);
                        break;
                    case "管理员":
                        contentRb4.setChecked(true);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

}
