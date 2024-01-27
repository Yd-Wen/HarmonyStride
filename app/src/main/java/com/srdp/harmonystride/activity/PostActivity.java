package com.srdp.harmonystride.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
        initActions();
    }

    private void initDatas(){
        Intent intent = getIntent();
        postTitle = intent.getStringExtra(POST_NAME);
        imageId = intent.getIntExtra(POST_IMAGE, 0);
    }

    private void initViews(){
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        toolbar = findViewById(R.id.tool_bar);
        imageView = findViewById(R.id.image_view);
        textView = findViewById(R.id.content_tv);
    }

    private void initActions(){
        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        if(actionBar != null){
//            actionBar.setTitle(R.string.post);
//        }

        collapsingToolbarLayout.setTitle(postTitle);
        Glide.with(this).load(imageId).into(imageView);
        textView.setText(getString(R.string.content));

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}