package com.srdp.harmonystride.activity;

import static com.mob.MobSDK.submitPolicyGrantResult;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.dialog.PrivacyDialog;
import com.srdp.harmonystride.util.SharedPreferenceUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        /**
         * com.mob.MobSDK.class
         * 回传用户隐私授权结果
         * @param isGranted     用户是否同意隐私协议
         */
        submitPolicyGrantResult(true);
        //初始化环信IM SDK
        EMOptions options = new EMOptions();
        options.setAppKey("1161240210157052#harmonystride");
        EMClient.getInstance().init(MyApplication.getContext(), options);

        //加载会话
        EMClient.getInstance().chatManager().loadAllConversations();

        //展示隐私内容
        checkPrivacy();
    }

    // 从assets下的txt文件中读取数据
    public String initPrivacy(String fileName) {
        //获取 assetManager
        AssetManager assetManager = getAssets();
        InputStream inputStream = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            inputStream = assetManager.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (inputStream != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    //检查隐私是否同意
    private void checkPrivacy(){
        Boolean status = (Boolean) SharedPreferenceUtil.getParam("privacy_agree", false);
        if (status){
            Log.d("check", "1");
            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    //是否已经登录
                    Boolean isLogin = (Boolean) SharedPreferenceUtil.getParam("is_login", false);
                    if(isLogin){
                        navigateTo(MainActivity.class);
                    }else{
                        navigateTo(LoginActivity.class);
                    }
                    finish();
                }
            };
            timer.schedule(timerTask,1000);
        }else{
            Log.d("check", "0");
            showPrivacy();//放在assets目录下的隐私政策文本文件
        }
    }

    //展示隐私内容
    private void showPrivacy(){
       String privacyStr = initPrivacy("privacy.txt");
        PrivacyDialog privacyDialog = new PrivacyDialog(this);
        privacyDialog.getContentTv().setText(privacyStr);
    }

}