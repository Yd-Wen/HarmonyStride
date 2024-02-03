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

import com.srdp.harmonystride.R;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.ScreenSizeUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;

import org.litepal.LitePal;

import java.util.List;

public class CheckBoxDialog extends BaseDialog{
    private RadioGroup genderRg;
    private RadioButton genderUnknownRb;
    private RadioButton genderMaleRb;
    private RadioButton genderFemaleRb;
    private ImageView checkCancelIv;
    private ImageView checkConfirmIv;

    private User curUser;
    private String content;

    /**
     * 自定义Dialog监听器
     */
    public interface OnDismissListener {
        /**
         * 回调函数，用于在Dialog的监听事件触发后刷新Activity的UI显示
         */
        public void onDismiss();
    }

    private CheckBoxDialog.OnDismissListener onDismissListener;

    public CheckBoxDialog(Context context, CheckBoxDialog.OnDismissListener onDismissListener) {
        super(context);
        this.onDismissListener = onDismissListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(View.inflate(baseContext, R.layout.dialog_check_box, null));
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
        content = curUser.getGender();
    }

    private void initViews(){
        genderRg = findViewById(R.id.rg_gender_edit);
        genderUnknownRb = findViewById(R.id.rb_gender_unknown);
        genderMaleRb = findViewById(R.id.rb_gender_male);
        genderFemaleRb = findViewById(R.id.rb_gender_female);
        checkCancelIv = findViewById(R.id.iv_check_cancel);
        checkConfirmIv = findViewById(R.id.iv_check_confirm);
        //显示关闭按钮，隐藏确认按钮
        checkCancelIv.setVisibility(View.VISIBLE);
        checkConfirmIv.setVisibility(View.GONE);

        //设置布局
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtil.getInstance(baseContext).getScreenWidth());
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(lp);
    }

    private void initEvents(){
        genderUnknownRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!content.equals("保密")){
                    checkCancelIv.setVisibility(View.GONE);
                    checkConfirmIv.setVisibility(View.VISIBLE);
                }else{
                    checkCancelIv.setVisibility(View.VISIBLE);
                    checkConfirmIv.setVisibility(View.GONE);
                }
            }
        });

        genderMaleRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!content.equals("男")){
                    checkCancelIv.setVisibility(View.GONE);
                    checkConfirmIv.setVisibility(View.VISIBLE);
                }else{
                    checkCancelIv.setVisibility(View.VISIBLE);
                    checkConfirmIv.setVisibility(View.GONE);
                }
            }
        });

        genderFemaleRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!content.equals("女")){
                    checkCancelIv.setVisibility(View.GONE);
                    checkConfirmIv.setVisibility(View.VISIBLE);
                }else{
                    checkCancelIv.setVisibility(View.VISIBLE);
                    checkConfirmIv.setVisibility(View.GONE);
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
                switch (genderRg.getCheckedRadioButtonId()){
                    case R.id.rb_gender_unknown:
                        content = genderUnknownRb.getText().toString();
                        break;
                    case R.id.rb_gender_male:
                        content = genderMaleRb.getText().toString();
                        break;
                    case R.id.rb_gender_female:
                        content = genderFemaleRb.getText().toString();
                        break;
                    default:
                        break;
                }
                //更新本地数据库
                User user = new User();
                user.setGender(content);
                user.updateAll("account = ?", curUser.getAccount());
                //回调方法
                onDismissListener.onDismiss();
                dismiss();
            }
        });
    }

    private void loadDatas(){
        switch (content){
            case "保密":
                genderUnknownRb.setChecked(true);
                break;
            case "男":
                genderMaleRb.setChecked(true);
                break;
            case "女":
                genderFemaleRb.setChecked(true);
                break;
            default:
                break;
        }
    }

}
