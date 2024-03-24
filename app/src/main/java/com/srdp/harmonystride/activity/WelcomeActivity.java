package com.srdp.harmonystride.activity;

import static com.mob.MobSDK.submitPolicyGrantResult;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
//import com.hyphenate.EMCallBack;
//import com.hyphenate.chat.EMClient;
//import com.hyphenate.chat.EMOptions;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.dialog.PrivacyDialog;
import com.srdp.harmonystride.util.IMUtil;
import com.srdp.harmonystride.util.LogUtil;
import com.srdp.harmonystride.util.RongIMUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import io.rong.imlib.RongIMClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WelcomeActivity extends BaseActivity {
    private static final int GET_IM_USER_TOKEN_SUCCESS = 0; //获取IM用户Token成功
    private static final int LOGIN_IM_SUCCESS = 1; //登录IM成功

    //RongIM连接状态监听器
    private RongIMClient.ConnectionStatusListener connectionStatusListener = new RongIMClient.ConnectionStatusListener() {
        @Override
        public void onChanged(ConnectionStatus status) {
            switch (status){
                case TOKEN_INCORRECT:
                    //token过期
                    getUserToken();
                    break;
                default:
                    break;
            }
        }
    };

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET_IM_USER_TOKEN_SUCCESS:
                    //loginIM(msg.obj.toString());
                    break;
                case LOGIN_IM_SUCCESS:
                    navigateTo(MainActivity.class);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

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
                        finish();
                        //getIMUserToken();
                    }else{
                        navigateTo(LoginActivity.class);
                        finish();
                    }
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

    //获取IM用户的TOKEN
    private void getUserToken(){
        String account = SharedPreferenceUtil.getParam("current_account","").toString();
        String nickname = SharedPreferenceUtil.getParam("current_nickname","").toString();
        //RongIMUtil.register();
    }

    //登录IM用户
//    private void loginIM(String token){
//        //登录IM
//        EMClient.getInstance().loginWithToken(SharedPreferenceUtil.getParam("current_account","").toString(), token, new EMCallBack() {
//            // 登录成功回调
//            @Override
//            public void onSuccess() {
//                LogUtil.d("login IM", "success");
//                Message message = new Message();
//                message.what = LOGIN_IM_SUCCESS;
//                handler.sendMessage(message);
//            }
//
//            // 登录失败回调，包含错误信息
//            @Override
//            public void onError(int code, String error) {
//                LogUtil.e("login IM", code + "-" + error);
//            }
//
//            @Override
//            public void onProgress(int progress, String status) {
//                LogUtil.d("login IM", "login...");
//            }
//        });
//
//    }

}