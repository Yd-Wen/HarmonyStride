package com.srdp.harmonystride.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.adapter.ApplyBriefAdapter;
import com.srdp.harmonystride.entity.Application;
import com.srdp.harmonystride.entity.Post;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.TimeUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ApplyManageActivity extends BaseActivity {
    private static final int LOADMORE_SUCCESS = 1; //请求刷新数据
    private static final int REFRESH_SUCCESS = 2; //刷新成功

    private Toolbar toolbar;
    private SmartRefreshLayout applySrl;
    private RecyclerView applyRv;

    private int curPid; //当前帖子ID
    private List<User> userList = new ArrayList<>();
    private List<Application> applicationList = new ArrayList<>();
    private ApplyBriefAdapter applyBriefAdapter;

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case REFRESH_SUCCESS:
                    applyBriefAdapter.notifyDataSetChanged();
                    applySrl.finishRefresh();
                    break;
                case LOADMORE_SUCCESS:
                    applyBriefAdapter.notifyDataSetChanged();
                    applySrl.finishLoadMore();
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_manage);
        //初始化数据
        initDatas();
        //初始化视图
        initViews();
        //初始化事件
        initEvents();
        //请求数据
        requestDatas(TimeUtil.formatLocalDateTime(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"), false);

    }

    private void initDatas(){
        //获取当前帖子ID
        Intent intent = getIntent();
        curPid = intent.getIntExtra("pid", 0);
    }

    private void initViews(){
        toolbar = findViewById(R.id.tool_bar);
        //不要忘记设置工具栏
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        applySrl = findViewById(R.id.srl_apply);
        applyRv = findViewById(R.id.rv_apply);

        //设置布局管理器
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        applyRv.setLayoutManager(gridLayoutManager);

        //设置适配器
        applyBriefAdapter = new ApplyBriefAdapter(userList, applicationList);
        applyRv.setAdapter(applyBriefAdapter);
    }

    private void initEvents(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        applySrl.setOnRefreshListener(refreshLayout -> {
            //刷新数据
            requestDatas(TimeUtil.formatLocalDateTime(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"), true);
        });

        applySrl.setOnLoadMoreListener(refreshLayout -> {
            //加载更多数据
            requestDatas(TimeUtil.formatLocalDateTime(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"), false);
        });
    }

    private void requestDatas(String datetime, boolean isLoadMore){
        //TODO:根据PID发送请求
        Message message = new Message();
        if(isLoadMore) message.obj = LOADMORE_SUCCESS;
        else  message.obj = REFRESH_SUCCESS;
        handler.sendMessage(message);
    }


}