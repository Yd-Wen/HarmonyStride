package com.srdp.harmonystride.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.entity.Post;
import com.srdp.harmonystride.util.ImageUtil;
import com.srdp.harmonystride.util.LogUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends BaseActivity {
    private Boolean isSelf; //是否查看自己的帖子
    private Boolean isAllowApply; //是否允许申请
    private int curUid; //当前用户UID
    private int postUid; //发帖人UID
    private String avatar;
    private String nickname;
    private Post post;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private ImageView imageView;
    private CircleImageView toolbarAvatarCiv;
    private TextView toolbarNicknameTv;

    private CircleImageView avatarCiv;
    private TextView nicknameTv;

    private TextView postTitleTv;
    private WebView postContentWv;
    private FloatingActionButton applyFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        //初始化数据
        initDatas();
        //初始化视图
        initViews();
        //初始化事件
        initEvents();
    }

    private void initDatas(){
        Intent intent = getIntent();
        postUid = intent.getIntExtra("user_id", 0);
        avatar = intent.getStringExtra("user_avatar");
        nickname = intent.getStringExtra("user_nickname");
        post = (Post) intent.getSerializableExtra("post");

        curUid = (Integer) SharedPreferenceUtil.getParam("current_uid", 0);
        if(curUid == postUid){
            isSelf = true;
        }else {
            isSelf = false;
        }

        if("1".equals(post.getAllow())){
            isAllowApply = true;
        }else {
            isAllowApply = false;
        }
    }

    private void initViews(){
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        appBarLayout = findViewById(R.id.appbar_layout);
        toolbar = findViewById(R.id.tool_bar);
        //不要忘记设置工具栏
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        imageView = findViewById(R.id.image_view);
        toolbarAvatarCiv = findViewById(R.id.civ_toolbar_avatar);
        toolbarNicknameTv = findViewById(R.id.tv_toolbar_nickname);

        avatarCiv = findViewById(R.id.civ_avatar);
        nicknameTv = findViewById(R.id.tv_nickname);

        postTitleTv = findViewById(R.id.tv_post_title);
        postContentWv = findViewById(R.id.wv_post_content);

        applyFab = findViewById(R.id.fab_apply);
    }

    private void initEvents(){
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()){
                    //折叠状态
                    toolbarAvatarCiv.setVisibility(View.VISIBLE);
                    toolbarNicknameTv.setVisibility(View.VISIBLE);
                }else {
                    //展开状态
                    toolbarAvatarCiv.setVisibility(View.GONE);
                    toolbarNicknameTv.setVisibility(View.GONE);
                }
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        toolbarAvatarCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostActivity.this, ProfileActivity.class);
                intent.putExtra("is_self", isSelf);
                intent.putExtra("user_id", postUid);
                navigateTo(intent);
            }
        });

        avatarCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostActivity.this, ProfileActivity.class);
                intent.putExtra("is_self", isSelf);
                intent.putExtra("user_id", postUid);
                navigateTo(intent);
            }
        });

        applyFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSelf){
                    if(isAllowApply){
                        //TODO:查看该帖子的申请列表
                    }else {
                        //TODO:修改帖子-是否允许申请
                    }
                }else {
                    if(isAllowApply){
                        //TODO:跳转申请页面，提交申请
                    }
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadDatas();
    }

    private void loadDatas(){
        //加载标题和背景图
        collapsingToolbarLayout.setTitle(post.getTitle());
        Glide.with(this).load(ImageUtil.getImagePath(post.getImages())).into(imageView);
        //加载工具栏
        Glide.with(this).load(ImageUtil.getImagePath(avatar)).into(toolbarAvatarCiv);
        toolbarNicknameTv.setText(nickname);
        //加载用户信息
        Glide.with(this).load(ImageUtil.getImagePath(avatar)).into(avatarCiv);
        nicknameTv.setText(nickname);
        //TODO:加载帖子
        postTitleTv.setText(post.getTitle());
        //postContentWv.loadUrl("www.baidu.com");
        //postContentWv.loadUrl(post.getContent());
        postContentWv.loadDataWithBaseURL(null, post.getContent(), "text/html", "UTF-8", null);

    }
}