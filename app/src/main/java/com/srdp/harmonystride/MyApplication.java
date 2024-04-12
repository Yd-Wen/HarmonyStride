package com.srdp.harmonystride;

import static com.mob.MobSDK.submitPolicyGrantResult;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.srdp.harmonystride.activity.MainActivity;
import com.srdp.harmonystride.entity.Result;
import com.srdp.harmonystride.util.HTTPUtil;
import com.srdp.harmonystride.util.ImageUtil;
import com.srdp.harmonystride.util.LogUtil;

import org.litepal.LitePal;

import java.io.IOException;

import io.rong.imkit.GlideKitImageEngine;
import io.rong.imkit.IMCenter;
import io.rong.imkit.config.RongConfigCenter;
import io.rong.imkit.userinfo.RongUserInfoManager;
import io.rong.imkit.userinfo.UserDataProvider;
import io.rong.imkit.utils.RouteUtils;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.InitOption;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MyApplication extends Application {
    private static Context context;
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

        //融云IM 初始化
        String appKey = "82hegw5u86gpx";
        InitOption initOption = new InitOption.Builder().build();
        IMCenter.init(this, appKey, initOption);

        RouteUtils.registerActivity(RouteUtils.RongActivityType.ConversationListActivity, MainActivity.class);

        RongUserInfoManager.getInstance().setUserInfoProvider(new UserDataProvider.UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String userId) {
                // 在需要展示用户信息时（例如会话列表页面、会话页面），IMKit 首先会根据用户 ID 逐个调用 getUserInfo。
                // 此处由 App 自行完成异步请求用户信息的逻辑。后续通过 refreshUserInfoCache 提供给 SDK。
                HTTPUtil.getUserByAccount(userId, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogUtil.e("getUserByAccount", "onFailure: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        LogUtil.d("getUserByAccount", "onSuccess");
                        Gson gson = new Gson();
                        String responseBody = response.body().string();
                        Result result = gson.fromJson(responseBody, Result.class);

                        android.os.Message message = new android.os.Message();
                        if(result.getCode() == 1){
                            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                            JsonObject data = jsonObject.getAsJsonObject("data").getAsJsonObject("user");
                            String name = data.get("nickname").getAsString();
                            String avatarUrl = ImageUtil.getImagePath(data.get("avatar").getAsString());
                            UserInfo userInfo = new UserInfo(userId, name, Uri.parse(avatarUrl));
                            RongUserInfoManager.getInstance().refreshUserInfoCache(userInfo);
                        }else {
                            LogUtil.d(userId, "unregistered");
                        }
                    }
                });

                return null;
            }

        }, true);

        // 会话列表中每个条目的头像显示默认为矩形，可修改为圆角显示。
        RongConfigCenter.featureConfig().setKitImageEngine(new GlideKitImageEngine() {
            @Override
            public void loadConversationListPortrait(@NonNull Context context, @NonNull String url, @NonNull ImageView imageView, Conversation conversation) {
                Glide.with(context).load(url)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(imageView);

                LogUtil.e("uuuu", url);
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

        // 设置是否显示用户昵称，仅支持单聊会话
        RongConfigCenter.conversationConfig().setShowReceiverUserTitle(true);

    }
    public static Context getContext() {
        return context;
    }
}
