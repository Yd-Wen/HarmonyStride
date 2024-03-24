package com.srdp.harmonystride;

import static com.mob.MobSDK.submitPolicyGrantResult;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;


//import com.hyphenate.EMMessageListener;
//import com.hyphenate.chat.EMClient;
//import com.hyphenate.chat.EMMessage;
//import com.hyphenate.chat.EMOptions;
//import com.hyphenate.easeui.EaseIM;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.srdp.harmonystride.activity.WelcomeActivity;
import com.srdp.harmonystride.fragment.MessageFragment;
import com.srdp.harmonystride.fragment.StatusFragment;
import com.srdp.harmonystride.util.IMUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;

import org.litepal.LitePal;

import java.util.List;

import io.rong.imkit.GlideKitImageEngine;
import io.rong.imkit.IMCenter;
import io.rong.imkit.config.RongConfigCenter;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.InitOption;
import io.rong.imlib.model.Message;

//import io.rong.imkit.IMCenter;
//import io.rong.imlib.model.InitOption;

public class MyApplication extends Application {
    private static Context context;
    //public static EMMessageListener messamgeListener;
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
//        EMOptions options = new EMOptions();
//        options.setAppKey(IMUtil.AppKey);
//        options.setAutoLogin(false); //取消自动登录
//        if(EaseIM.getInstance().init(context, options)){
//            //在做打包混淆时，关闭 debug 模式，避免消耗不必要的资源
//            EMClient.getInstance().setDebugMode(true);
//            //EaseIM 初始化成功之后再调用注册消息监听的代码 ...
//            messamgeListener = new EMMessageListener() {
//
//                @Override
//                public void onMessageReceived(List<EMMessage> messages) {
//                    //收到消息
//                    Intent intent = new Intent();
//                    //BROADCAST_ACTION_DISC,用于标识接收
//                    intent.setAction(MessageFragment.BROADCAST_ACTION_DISC);
//                    //发送广播
//                    sendBroadcast(intent);
//                }
//
//                @Override
//                public void onCmdMessageReceived(List<EMMessage> messages) {
//                    //收到透传消息
//                }
//
//                @Override
//                public void onMessageRead(List<EMMessage> messages) {
//                    //收到已读回执
//                }
//
//                @Override
//                public void onMessageDelivered(List<EMMessage> message) {
//                    //收到已送达回执
//                }
//                @Override
//                public void onMessageRecalled(List<EMMessage> messages) {
//                    //消息被撤回
//                }
//
//                @Override
//                public void onMessageChanged(EMMessage message, Object change) {
//                    //消息状态变动
//                }
//            };
//        }

//        if((Boolean) SharedPreferenceUtil.getParam("privacy_agree", false)){
//            //融云IM 初始化
//            String appKey = "82hegw5u86gpx";
//            InitOption initOption = new InitOption.Builder().build();
//            IMCenter.init(this, appKey, initOption);
//        }
//
//        else startActivity(new Intent(context, WelcomeActivity.class));

        //融云IM 初始化
        String appKey = "82hegw5u86gpx";
        InitOption initOption = new InitOption.Builder().build();
        IMCenter.init(this, appKey, initOption);

        // 会话列表中每个条目的头像显示默认为矩形，可修改为圆角显示。
        RongConfigCenter.featureConfig().setKitImageEngine(new GlideKitImageEngine() {
            @Override
            public void loadConversationListPortrait(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView, Conversation conversation) {
                Glide.with(context).load(url)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(imageView);
            }
        });

        // 会话页面中每条消息的头像显示默认为矩形，可修改为圆角显示。
        RongConfigCenter.featureConfig().setKitImageEngine(new GlideKitImageEngine() {
            @Override
            public void loadConversationPortrait(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView, Message message) {
                Glide.with(context).load(url)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(imageView);
            }
        });

    }
    public static Context getContext() {
        return context;
    }
}
