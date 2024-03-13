package com.srdp.harmonystride.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.adapter.PostBriefAdapter;
import com.srdp.harmonystride.entity.Post;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.HTTPUtil;
import com.srdp.harmonystride.util.LogUtil;
import com.srdp.harmonystride.util.ScrollUtil;
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

public class HomeFragment extends Fragment{
    private static final int LOADMORE_SUCCESS = 1; //请求刷新数据
    private static final int REFRESH_SUCCESS = 2; //刷新成功



    private View view;
    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;

    private List<Post> postList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();

    private PostBriefAdapter postBriefAdapter;
    private ScrollUtil scrollUtil; //滑动工具
    private static final int SCROLL_THRESHOLD = 1000; // 设置滑动阈值

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case REFRESH_SUCCESS:
                    postBriefAdapter.notifyDataSetChanged();
                    smartRefreshLayout.finishRefresh();
                    break;
                case LOADMORE_SUCCESS:
                    postBriefAdapter.notifyDataSetChanged();
                    smartRefreshLayout.finishLoadMore();
                default:
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

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
        String label = "";
        Map<String, Object> params = new HashMap<>();
        params.put("time", time);
        params.put("label", label);
        HTTPUtil.POST(params, "/post/browse", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("browse post", "error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d("browse post", "success");
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String postS = jsonObject.getJSONObject("data").getString("postList");
                    String userS = jsonObject.getJSONObject("data").getString("userList");
                    Gson gson = new Gson();
                    List<Post> postL = gson.fromJson(postS, new TypeToken<List<Post>>(){}.getType());
                    List<User> userL = gson.fromJson(userS, new TypeToken<List<User>>(){}.getType());
                    //获取新数据
                    if(!isLoadMore){
                        postList.clear();
                        userList.clear();
                    }
                    for(int i = 0; i < postL.size(); ++i){
                        postList.add(postL.get(i));
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
        //获取布局
        smartRefreshLayout = view.findViewById(R.id.smart_refresh_layout);
        recyclerView = view.findViewById(R.id.recycler_view);
        floatingActionButton = getActivity().findViewById(R.id.floating_action_btn);
        //初始化滑动工具
        scrollUtil = new ScrollUtil(getActivity().findViewById(R.id.bottom_navigation_view), recyclerView);
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
                String time = postList.get(postList.size()-1).getDatetime();
                requestDatas(time, true);
            }
        });

        //添加滑动事件监听器
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 获取当前滚动的垂直距离
                int scrollY = recyclerView.computeVerticalScrollOffset();
                // 根据下滑距离更换图片
                if (scrollY > SCROLL_THRESHOLD) {
                    floatingActionButton.hide();
                    scrollUtil.setItem(R.drawable.top, R.string.top);
                }else {
                    floatingActionButton.show();
                    scrollUtil.setItem(R.drawable.home, R.string.home);
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 当滑动停止时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 恢复图片加载
                    Glide.with(MyApplication.getContext()).resumeRequests();
                } else {
                    // 暂停图片加载
                    Glide.with(MyApplication.getContext()).pauseRequests();
                }
            }
        });

        //设置布局管理器
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        //设置适配器
        postBriefAdapter = new PostBriefAdapter(postList, userList);
        recyclerView.setAdapter(postBriefAdapter);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            recyclerView.smoothScrollToPosition(0);
        }
    }

    public void refresh(){
        requestDatas(TimeUtil.formatLocalDateTime(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"), false);
    }


}