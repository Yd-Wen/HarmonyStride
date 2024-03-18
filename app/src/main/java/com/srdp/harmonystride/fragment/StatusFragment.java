package com.srdp.harmonystride.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.scwang.smart.refresh.header.BezierRadarHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.activity.CertificationActivity;
import com.srdp.harmonystride.activity.LoginActivity;
import com.srdp.harmonystride.activity.MainActivity;
import com.srdp.harmonystride.activity.PostActivity;
import com.srdp.harmonystride.activity.PostingActivity;
import com.srdp.harmonystride.activity.ProfileActivity;
import com.srdp.harmonystride.adapter.ApplyMyAdapter;
import com.srdp.harmonystride.adapter.PostBriefAdapter;
import com.srdp.harmonystride.dialog.TipDialog;
import com.srdp.harmonystride.entity.Application;
import com.srdp.harmonystride.entity.Post;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.HTTPUtil;
import com.srdp.harmonystride.util.ImageUtil;
import com.srdp.harmonystride.util.LogUtil;
import com.srdp.harmonystride.util.ScrollUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;
import com.srdp.harmonystride.util.StringUtil;
import com.srdp.harmonystride.util.TimeUtil;
import com.srdp.harmonystride.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class StatusFragment extends Fragment{
    private static final int LOADMORE_SUCCESS = 1; //请求刷新数据
    private static final int REFRESH_SUCCESS = 2; //刷新成功

    private View view;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private TextView titleTv;

    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;

    private List<Post> postList = new ArrayList<>();
    private List<Application> applicationList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();

    private ApplyMyAdapter applyMyAdapter;

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case REFRESH_SUCCESS:
                    applyMyAdapter.notifyDataSetChanged();
                    smartRefreshLayout.finishRefresh();
                    break;
                case LOADMORE_SUCCESS:
                    applyMyAdapter.notifyDataSetChanged();
                    smartRefreshLayout.finishLoadMore();
                default:
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_status, container, false);
        //初始化视图
        initViews();
        //初始化事件
        initActions();
        //请求数据
        requestDatas(TimeUtil.formatLocalDateTime(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"), false);
        // Inflate the layout for this fragment
        return view;
    }


    private void requestDatas(String time, Boolean isLoadMore){
        //发送请求，获取帖子简略页列表，包括Post列表和User列表
        Map<String, Object> params = new HashMap<>();
        params.put("time", time);
        params.put("uid", SharedPreferenceUtil.getParam("current_uid", 0).toString());
        HTTPUtil.POST(params, "/application/findFocus", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("find my application", "error" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d("find my application", "success");
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String applicationS = jsonObject.getJSONObject("data").getString("applicationList");
                    String postS = jsonObject.getJSONObject("data").getString("postList");
                    String userS = jsonObject.getJSONObject("data").getString("userList");
                    Gson gson = new Gson();
                    List<Application> applicationL = gson.fromJson(applicationS, new TypeToken<List<Application>>(){}.getType());
                    List<Post> postL = gson.fromJson(postS, new TypeToken<List<Post>>(){}.getType());
                    List<User> userL = gson.fromJson(userS, new TypeToken<List<User>>(){}.getType());
                    //获取新数据
                    if(!isLoadMore){
                        postList.clear();
                        applicationList.clear();
                        userList.clear();
                    }
                    for(int i = 0; i < applicationL.size(); ++i){
                        postList.add(postL.get(i));
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

    private void initViews(){
        //获取工具栏
        appBarLayout = view.findViewById(R.id.appbar_layout);
        toolbar = view.findViewById(R.id.tool_bar);
        titleTv = view.findViewById(R.id.tv_toolbar_title);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        //删除标题
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(null);
        //设置新标题
        titleTv.setText(R.string.status);

        //获取布局
        smartRefreshLayout = view.findViewById(R.id.smart_refresh_layout);
        recyclerView = view.findViewById(R.id.recycler_view);

        //设置布局管理器
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        //设置适配器
        applyMyAdapter = new ApplyMyAdapter(applicationList, postList, userList);
        recyclerView.setAdapter(applyMyAdapter);

    }

    private void initActions(){

        //添加下拉刷新事件监听器
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                requestDatas(TimeUtil.formatLocalDateTime(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"), false);
            }
        });

        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                String time = applicationList.get(postList.size()-1).getDatetime();
                requestDatas(time, true);
            }
        });

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            recyclerView.smoothScrollToPosition(0);
        }
    }



}