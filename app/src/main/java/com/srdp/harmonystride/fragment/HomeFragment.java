package com.srdp.harmonystride.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.adapter.MyEntityAdapter;
import com.srdp.harmonystride.entity.MyEntity;
import com.srdp.harmonystride.util.ScrollUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment{
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;

    private MyEntity[] myEntities = {
            new MyEntity("111", R.drawable.welcome),
            new MyEntity("222", R.drawable.welcome)
    };

    private List<MyEntity> myEntityList = new ArrayList<>();

    private MyEntityAdapter myEntityAdapter;
    private Handler handler;
    private ScrollUtil scrollUtil; //滑动工具
    private static final int SCROLL_THRESHOLD = 1000; // 设置滑动阈值

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        //初始化实体列表
        initEntities();
        //初始化视图
        initViews();
        //初始化事件
        initActions();
        // Inflate the layout for this fragment
        return view;
    }

    private void initEntities(){
        myEntityList.clear();
        for(int i=0; i<30; i++){
            Random random = new Random();
            int index = random.nextInt(myEntities.length);
            myEntityList.add(myEntities[index]);
        }
    }

    private void initViews(){
        //获取布局
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = view.findViewById(R.id.recycler_view);
        floatingActionButton = getActivity().findViewById(R.id.floating_action_btn);

        //设置进度条的颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        //设置进度条的背景颜色
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.progressBackground);
        //初始化滑动工具
        scrollUtil = new ScrollUtil(getActivity().findViewById(R.id.bottom_navigation_view), recyclerView);
    }

    private void initActions(){
        //初始化handler
        handler = new Handler(Looper.getMainLooper());

        //添加下拉刷新事件监听器
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshEntities();
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
                    Glide.with(getActivity().getApplicationContext()).resumeRequests();
                } else {
                    // 暂停图片加载
                    Glide.with(getActivity().getApplicationContext()).pauseRequests();
                }
            }
        });

        //设置布局管理器
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        //设置适配器
        myEntityAdapter = new MyEntityAdapter(myEntityList);
        recyclerView.setAdapter(myEntityAdapter);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            recyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler = null;
    }

    private void refreshEntities(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        initEntities();
                        myEntityAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    public void refresh(){
        swipeRefreshLayout.setRefreshing(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        initEntities();
                        myEntityAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }


}