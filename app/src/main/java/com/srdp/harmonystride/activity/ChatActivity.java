package com.srdp.harmonystride.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.modules.chat.EaseChatFragment;
import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.entity.Result;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.HTTPUtil;
import com.srdp.harmonystride.util.ImageUtil;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChatActivity extends BaseActivity {
    private static final int GET_USER = 1; //获取用户成功
    //Glide请求图片选项配置
    private RequestOptions requestOptions = RequestOptions
            .circleCropTransform()//圆形剪裁
            .diskCacheStrategy(DiskCacheStrategy.NONE);//不做磁盘缓存
    //.skipMemoryCache(true);//不做内存缓存

    private Toolbar toolbar;
    private CircleImageView avatarCiv;
    private TextView nicknameTv;

    private String chatAccount; //当前聊天对象的账号
    private User curUser;

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET_USER:
                    //获取头像和昵称
                    String avatar = curUser.getAvatar();
                    String nickname = curUser.getNickname();
                    Glide.with(MyApplication.getContext()).load(ImageUtil.getImagePath(avatar)).apply(requestOptions).into(avatarCiv);
                    nicknameTv.setText(nickname);
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //初始化视图
        initViews();
        //初始化事件
        initEvents();
    }

    private void initViews(){
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null); //去除标题

        avatarCiv = findViewById(R.id.civ_toolbar_avatar);
        nicknameTv = findViewById(R.id.tv_toolbar_nickname);

        //use EaseChatFratFragment
        EaseChatFragment chatFragment = new EaseChatFragment();
        //pass parameters to chat fragment
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();

    }

    private void initEvents(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        avatarCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO：获取用户信息，查看用户主页
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        loadUserInfo();
    }

    private void loadUserInfo(){
        Intent intent = getIntent();
        //获取当前聊天对象账号
        chatAccount = intent.getStringExtra(EaseConstant.EXTRA_CONVERSATION_ID);
        //通过账号查找用户

        HTTPUtil.getUserByAccount(chatAccount, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Gson gson = new Gson();
                String responseBody = response.body().string();
                Result result = gson.fromJson(responseBody, Result.class);
                if(result.getCode() == 1){
                    JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                    JsonObject data = jsonObject.getAsJsonObject("data");
                    //注意不要用toString，否则引号会保留到字符串
                    curUser = new User();
                    curUser.setId(data.get("id").getAsInt());
                    curUser.setAccount(data.get("id").getAsString());
                    curUser.setAvatar(data.get("avatar").getAsString());
                    curUser.setNickname(data.get("nickname").getAsString());
                    curUser.setCertify(data.get("certify").getAsString());
                    curUser.setGender(data.get("gender").getAsString());
                    curUser.setLocation(data.get("location").getAsString());
                    curUser.setIntroduction(data.get("introduction").getAsString());
                    curUser.setFocus(data.get("focus").getAsString());

                    Message message = new Message();
                    message.what = GET_USER;
                    handler.sendMessage(message);
                }
            }
        });
    }

    //获取工具栏菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_toolbar_more, menu);
        return true;
    }

    //为工具栏菜单绑定单击事件监听器
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.more:
                //TODO:更多选项，查看主页，屏蔽消息，举报等
                break;
            default:
                break;
        }
        return true;
    }
}