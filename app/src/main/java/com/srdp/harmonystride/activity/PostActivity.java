package com.srdp.harmonystride.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.srdp.harmonystride.R;

public class PostActivity extends BaseActivity {

    public static final String POST_NAME = "post_name";
    public static final String POST_IMAGE = "post_image";

    private String postTitle;
    private int imageId;


    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private ImageView imageView;
    private TextView textView;


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
        postTitle = intent.getStringExtra(POST_NAME);
        imageId = intent.getIntExtra(POST_IMAGE, 0);
    }

    private void initViews(){
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        toolbar = findViewById(R.id.tool_bar);
        //不要忘记设置工具栏
        setSupportActionBar(toolbar);

        imageView = findViewById(R.id.image_view);
        textView = findViewById(R.id.content_tv);
    }

    private void initEvents(){

        collapsingToolbarLayout.setTitle(postTitle);
        Glide.with(this).load(imageId).into(imageView);
        textView.setText(getString(R.string.content));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}