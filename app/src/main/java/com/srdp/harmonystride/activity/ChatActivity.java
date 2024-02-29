package com.srdp.harmonystride.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.modules.chat.EaseChatFragment;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.ImageUtil;

import org.litepal.LitePal;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends BaseActivity {
    //Glide请求图片选项配置
    private RequestOptions requestOptions = RequestOptions
            .circleCropTransform()//圆形剪裁
            .diskCacheStrategy(DiskCacheStrategy.NONE);//不做磁盘缓存
    //.skipMemoryCache(true);//不做内存缓存

    private Toolbar toolbar;
    private CircleImageView avatarCiv;
    private TextView nicknameTv;

    private String chatAccount; //当前聊天对象的账号

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
        List<User> userList = LitePal.where("account=?", chatAccount).find(User.class);
        User curUser = userList.get(0);
        //获取头像和昵称
        String avatar = curUser.getAvatar();
        String nickname = curUser.getNickname();
        Glide.with(this).load(ImageUtil.getImagePath(avatar)).apply(requestOptions).into(avatarCiv);
        nicknameTv.setText(nickname);

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