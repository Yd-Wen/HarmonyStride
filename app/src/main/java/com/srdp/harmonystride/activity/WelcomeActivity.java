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
import com.srdp.harmonystride.entity.Result;
import com.srdp.harmonystride.util.LogUtil;
import com.srdp.harmonystride.util.RongIMUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;
import com.srdp.harmonystride.util.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import io.rong.imkit.RongIM;
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
                    LogUtil.e("token过期", "WelcomeActivity");
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
                    //TODO 连接IM服务器
                    LogUtil.e("获取IM用户Token成功", "WelcomeActivity");
                    loginIM();
                    break;
                case LOGIN_IM_SUCCESS:
                    LogUtil.e("登录IM成功", "WelcomeActivity");
                    navigateTo(MainActivity.class);
                    finish();
                    //移除连接监听器
                    RongIM.setConnectionStatusListener(null);
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
        //设置连接监听器
        RongIM.setConnectionStatusListener(connectionStatusListener);
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
                        //TODO 连接IM服务器
                        if(StringUtil.isEmpty((String)SharedPreferenceUtil.getParam("user_token", ""))){
                            getUserToken();
                        }else {
                            loginIM();
                        }
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
        String TAG = "getUserToken";
        String account = SharedPreferenceUtil.getParam("current_account","").toString();
        RongIMUtil.getToken(account, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e(TAG, "error" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d(TAG, "success");
                Result result = RongIMUtil.gson.fromJson(response.body().string(), Result.class);
                LogUtil.e("token", result.getData().toString());
                SharedPreferenceUtil.setParam("user_token", result.getData().toString());
                Message message = new Message();
                message.what = GET_IM_USER_TOKEN_SUCCESS;
                handler.sendMessage(message);
            }
        });
    }



    //登录IM用户
    private void loginIM(){
        //登录IM
        String token = (String) SharedPreferenceUtil.getParam("user_token", "");
        LogUtil.e("token", token);
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onSuccess(String t) {
                Message message = new Message();
                message.what = LOGIN_IM_SUCCESS;
                handler.sendMessage(message);
            }

            @Override
            public void onError(RongIMClient.ConnectionErrorCode e) {
                switch (e){
                    case RC_CONN_TOKEN_INCORRECT:
                        //token无效
                        getUserToken();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus code) {
            }
        });

    }

}