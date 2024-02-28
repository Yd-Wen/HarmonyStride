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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.adapter.ConversationAdapter;
import com.srdp.harmonystride.entity.Conversation;
import com.srdp.harmonystride.entity.Result;
import com.srdp.harmonystride.util.HTTPUtil;
import com.srdp.harmonystride.util.LogUtil;
import com.srdp.harmonystride.util.TimeUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MessageFragment extends Fragment {
    private static final int GET_USER_BY_MSG = 1; //通过消息查找用户
    //消息接收监听器
    private EMMessageListener msgListener;
    //Glide请求图片选项配置
    private RequestOptions requestOptions = RequestOptions
            .circleCropTransform()//圆形剪裁
            .diskCacheStrategy(DiskCacheStrategy.NONE);//不做磁盘缓存
    //.skipMemoryCache(true);//不做内存缓存

    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private List<Conversation> conversationList = new ArrayList<>();
    private List<EMMessage> messageList;
    private String avatar;
    private String nickname;
    private EMMessage emMessage; //单聊消息
    private ConversationAdapter conversationAdapter;

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET_USER_BY_MSG:
                    Conversation conversation = new Conversation(avatar, nickname, emMessage.toString(), TimeUtil.timestampToLocalDateTime(emMessage.getMsgTime()));
                    conversationList.add(conversation);
                    conversationAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, container, false);

        //加载会话
        EMClient.getInstance().chatManager().loadAllConversations();

        //初始化实体列表
        initDatas();
        //初始化视图
        initViews();
        //初始化事件
        initActions();
        // Inflate the layout for this fragment
        return view;
    }

    private void initDatas(){

    }

    private void initViews(){
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = view.findViewById(R.id.recycler_view);
        //设置进度条的颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        //设置进度条的背景颜色
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.progressBackground);
    }

    private void initActions(){
        //添加下拉刷新事件监听器
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshConversations();
            }
        });

        //添加滑动事件监听器
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
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
        conversationAdapter = new ConversationAdapter(conversationList);
        recyclerView.setAdapter(conversationAdapter);

        //消息监听
        msgListener = new EMMessageListener() {
            // 收到消息，遍历消息队列，解析和显示。
            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                messageList = messages;
                for(EMMessage msg:messages){
                    LogUtil.e("msg", msg.toString());
                    //当前消息
                    emMessage = msg;
                    //获取发送方账号ID
                    String account = msg.getFrom();
                    HTTPUtil.getUserByAccount(account, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            LogUtil.e("get user", "error");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            LogUtil.d("get user", "success");
                            Gson gson = new Gson();
                            String responseBody = response.body().string();
                            Result result = gson.fromJson(responseBody, Result.class);
                            if(result.getCode() == 1){
                                JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                                JsonObject data = jsonObject.getAsJsonObject("data");

                                avatar = data.get("avatar").getAsString();
                                nickname = data.get("nickname").getAsString();

                                Message message = new Message();
                                message.what = GET_USER_BY_MSG;
                                handler.sendMessage(message);
                            }
                        }
                    });
                }
            }
        };
        // 注册消息监听
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    private void refreshConversations() {
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
                        initMessages();
                        conversationAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void initMessages(){
        conversationList.clear();
        for(EMMessage msg:messageList){
            LogUtil.e("msg", msg.toString());
            //当前消息
            emMessage = msg;
            //获取发送方账号ID
            String account = msg.getFrom();
            HTTPUtil.getUserByAccount(account, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.e("get user", "error");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    LogUtil.d("get user", "success");
                    Gson gson = new Gson();
                    String responseBody = response.body().string();
                    Result result = gson.fromJson(responseBody, Result.class);
                    if(result.getCode() == 1){
                        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                        JsonObject data = jsonObject.getAsJsonObject("data");

                        avatar = data.get("avatar").getAsString();
                        nickname = data.get("nickname").getAsString();

                        Conversation conversation = new Conversation(avatar, nickname, emMessage.toString(), TimeUtil.timestampToLocalDateTime(emMessage.getMsgTime()));
                        conversationList.add(conversation);
                    }
                }
            });
        }
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
        // 解注册消息监听
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

}