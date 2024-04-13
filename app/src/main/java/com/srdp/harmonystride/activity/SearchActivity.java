package com.srdp.harmonystride.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.adapter.PostBriefAdapter;
import com.srdp.harmonystride.entity.Post;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.HTTPUtil;
import com.srdp.harmonystride.util.LogUtil;
import com.srdp.harmonystride.util.StringUtil;
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

public class SearchActivity extends BaseActivity {
    private static final int LOADMORE_SUCCESS = 1; //请求刷新数据
    private static final int REFRESH_SUCCESS = 2; //刷新成功

    private SearchView searchView;
    private Button cancelBtn;
    private Spinner spinner;

    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;

    private List<Post> postList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();

    private String curKeyword; //当前搜索关键字
    private String curTag; //当前标签
    private PostBriefAdapter postBriefAdapter;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //初始化视图
        initViews();
        //初始化事件
        initEvents();
    }

    private void initViews(){
        searchView = findViewById(R.id.search_view);
        cancelBtn = findViewById(R.id.btn_cancel);
        spinner = findViewById(R.id.spinner);
        smartRefreshLayout = findViewById(R.id.smart_refresh_layout);
        recyclerView = findViewById(R.id.recycler_view);

        //设置布局管理器
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        //设置适配器
        postBriefAdapter = new PostBriefAdapter(postList, userList);
        recyclerView.setAdapter(postBriefAdapter);
    }

    private void initEvents(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //关闭键盘
                searchView.clearFocus();
                curKeyword = s;
                if(!StringUtil.isEmpty(curKeyword)) requestDatas(curKeyword, TimeUtil.formatLocalDateTime(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"), curTag, false);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean isFirstSelection = true;
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        curTag = getString(R.string.post_label_all);
                        break;
                    case 1:
                        isFirstSelection = false;
                        curTag = getString(R.string.post_label_volunteer);
                        break;
                    case 2:
                        isFirstSelection = false;
                        curTag = getString(R.string.post_label_job);
                        break;
                    case 3:
                        isFirstSelection = false;
                        curTag = getString(R.string.post_label_help);
                        break;
                    case 4:
                        isFirstSelection = false;
                        curTag = getString(R.string.post_label_recruitment);
                        break;
                    default:
                        break;
                }
                if(!isFirstSelection) requestDatas(curKeyword, TimeUtil.formatLocalDateTime(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"), curTag, false);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //添加下拉刷新事件监听器
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                requestDatas(curKeyword, TimeUtil.formatLocalDateTime(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"), curTag, false);
            }
        });
        //上滑加载更多
        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                String time = postList.get(postList.size()-1).getDatetime();
                requestDatas(curKeyword, time, curTag, true);

            }
        });
    }

    private void requestDatas(String keyword, String time, String label, boolean isLoadMore){
        //发送请求，获取帖子简略页列表，包括Post列表和User列表
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("time", time);
        params.put("label", label);
        HTTPUtil.POST(params, "/post/search", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("browse post", "error" + e);
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




}