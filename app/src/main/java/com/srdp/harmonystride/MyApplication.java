package com.srdp.harmonystride;

import static com.mob.MobSDK.submitPolicyGrantResult;

import android.app.Application;
import android.content.Context;
import android.content.Intent;


import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseIM;
import com.srdp.harmonystride.fragment.MessageFragment;
import com.srdp.harmonystride.fragment.StatusFragment;
import com.srdp.harmonystride.util.IMUtil;

import org.litepal.LitePal;

import java.util.List;

public class MyApplication extends Application {
    private static Context context;
    public static EMMessageListener messamgeListener;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(context);

        /**
         * com.mob.MobSDK.class
         * 回传用户隐私授权结果
         * @param isGranted     用户是否同意隐私协议
         */
        //短信验证授权
        submitPolicyGrantResult(true);
        //EaseIM 初始化
        EMOptions options = new EMOptions();
        options.setAppKey(IMUtil.AppKey);
        options.setAutoLogin(false); //取消自动登录
        if(EaseIM.getInstance().init(context, options)){
            //在做打包混淆时，关闭 debug 模式，避免消耗不必要的资源
            EMClient.getInstance().setDebugMode(true);
            //EaseIM 初始化成功之后再调用注册消息监听的代码 ...
            messamgeListener = new EMMessageListener() {

                @Override
                public void onMessageReceived(List<EMMessage> messages) {
                    //收到消息
                    Intent intent = new Intent();
                    //BROADCAST_ACTION_DISC,用于标识接收
                    intent.setAction(MessageFragment.BROADCAST_ACTION_DISC);
                    //发送广播
                    sendBroadcast(intent);
                }

                @Override
                public void onCmdMessageReceived(List<EMMessage> messages) {
                    //收到透传消息
                }

                @Override
                public void onMessageRead(List<EMMessage> messages) {
                    //收到已读回执
                }

                @Override
                public void onMessageDelivered(List<EMMessage> message) {
                    //收到已送达回执
                }
                @Override
                public void onMessageRecalled(List<EMMessage> messages) {
                    //消息被撤回
                }

                @Override
                public void onMessageChanged(EMMessage message, Object change) {
                    //消息状态变动
                }
            };
        }

    }
    public static Context getContext() {
        return context;
    }
}
