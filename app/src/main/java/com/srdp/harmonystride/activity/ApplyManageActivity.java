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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.adapter.ApplyBriefAdapter;
import com.srdp.harmonystride.entity.Application;
import com.srdp.harmonystride.entity.Post;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.HTTPUtil;
import com.srdp.harmonystride.util.LogUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;
import com.srdp.harmonystride.util.TimeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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
        requestDatas(false);

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
        applyBriefAdapter = new ApplyBriefAdapter(userList, applicationList, String.valueOf(getIntent().getIntExtra("pid", 0)));
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
            requestDatas(false);
        });

        applySrl.setOnLoadMoreListener(refreshLayout -> {
            //加载更多数据
            requestDatas(true);
        });
    }

    private void requestDatas(Boolean isLoadMore){
        //发送请求，获取帖子简略页列表，包括Post列表和User列表
        String uid = null;
        if(isLoadMore) uid = String.valueOf(userList.get(userList.size()-1).getUid());
        else uid = "0";
        Map<String, Object> params = new HashMap<>();
        params.put("pid", String.valueOf(curPid));
        params.put("uid", uid);
        HTTPUtil.POST(params, "/application/findOther", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("find other application", "error" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d("find other application", "success");
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String applicationS = jsonObject.getJSONObject("data").getString("applicationList");
                    String userS = jsonObject.getJSONObject("data").getString("userList");
                    Gson gson = new Gson();
                    List<Application> applicationL = gson.fromJson(applicationS, new TypeToken<List<Application>>(){}.getType());
                    List<User> userL = gson.fromJson(userS, new TypeToken<List<User>>(){}.getType());
                    //获取新数据
                    if(!isLoadMore){
                        applicationList.clear();
                        userList.clear();
                    }
                    for(int i = 0; i < applicationL.size(); ++i){
                        applicationList.add(applicationL.get(i));
                        userList.add(userL.get(i));
                    }
                    //传递消息
                    Message message = new Message();
                    if(isLoadMore){
                        message.what = LOADMORE_SUCCESS;
                    }else {
                        message.what = REFRESH_SUCCESS;
                    }
                    handler.sendMessage(message);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}