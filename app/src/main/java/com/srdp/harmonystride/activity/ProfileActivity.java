package com.srdp.harmonystride.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
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
import com.srdp.harmonystride.adapter.PostVisitAdapter;
import com.srdp.harmonystride.entity.Post;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.HTTPUtil;
import com.srdp.harmonystride.util.ImageUtil;
import com.srdp.harmonystride.util.LogUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;
import com.srdp.harmonystride.util.StringUtil;
import com.srdp.harmonystride.util.TimeUtil;

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

public class ProfileActivity extends BaseActivity {
    private static final int REQUEST_USER_SUCCESS = 1; //请求用户数据成功
    private static final int LOADMORE_SUCCESS = 2; //请求刷新数据
    private static final int REFRESH_SUCCESS = 3; //刷新成功

    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView backgroundIv;
    private Toolbar toolbar;
    private CircleImageView avatarToolbarCiv;
    private TextView nicknameToolbarTv;

    private CircleImageView avatarCiv;
    private TextView nicknameTv;
    private TextView introductionTv;
    private TextView certifyTv;
    private TextView genderTv;
    private TextView locationTv;

    private FloatingActionButton floatingActionButton;

    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;

    private LinearLayout userOptionLl;
    private Button chatBtn;
    private Button focusBtn;

    private PostVisitAdapter postVisitAdapter;

    private User curUser = new User();
    private int uid;
    private Boolean isSelf; //是否查看自己的主页
    private List<Post> postList = new ArrayList<>();

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case REQUEST_USER_SUCCESS:
                    loadUserInfo();
                    break;
                case REFRESH_SUCCESS:
                    loadUserInfo();
                    postVisitAdapter.notifyDataSetChanged();
                    smartRefreshLayout.finishRefresh();
                    break;
                case LOADMORE_SUCCESS:
                    postVisitAdapter.notifyDataSetChanged();
                    smartRefreshLayout.finishLoadMore();
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //初始化视图
        initViews();
        //初始化事件
        initEvents();
        //初始化数据
        initDatas();
    }

    private void initDatas(){
        Intent intent = getIntent();
        isSelf = intent.getBooleanExtra("is_self", false);
        uid = intent.getIntExtra("user_id", 0);
        requestDatas(TimeUtil.formatLocalDateTime(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"), false);
        if(!isSelf){
            floatingActionButton.setVisibility(View.GONE);
        }
    }

    private void requestDatas(String time, Boolean isLoadMore){
        Map<String, Object> params = new HashMap<>();
        params.put("uid", String.valueOf(uid));
        params.put("time", time);
        HTTPUtil.POST(params, "/post/visit", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("visit post", "error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d("visit post", "success");
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String postS = jsonObject.getJSONObject("data").getString("postList");
                    String userS = jsonObject.getJSONObject("data").getString("user");
                    Gson gson = new Gson();
                    List<Post> postL = gson.fromJson(postS, new TypeToken<List<Post>>(){}.getType());
                    curUser.copyFromUser(gson.fromJson(userS, User.class));

                    //获取新数据
                    if(!isLoadMore){
                        postList.clear();
                    }
                    for(int i = 0; i < postL.size(); ++i){
                        postList.add(postL.get(i));
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

    private void loadUserInfo(){
        //下载头像
        if(!StringUtil.isEmpty(curUser.getAvatar())){
            Glide.with(this).load(ImageUtil.getImagePath(curUser.getAvatar())).apply(ImageUtil.requestOptionsWithCircle).into(avatarCiv);
            Glide.with(this).load(ImageUtil.getImagePath(curUser.getAvatar())).apply(ImageUtil.requestOptionsWithCircle).into(avatarToolbarCiv);
            Glide.with(this).load(ImageUtil.getImagePath(curUser.getAvatar())).apply(ImageUtil.requestOptionsWithCircle).into(backgroundIv);
        }
        //显示昵称
        nicknameTv.setText(curUser.getNickname());
        nicknameToolbarTv.setText(curUser.getNickname());
        //显示简介
        introductionTv.setText(curUser.getIntroduction());
        //显示认证信息
        switch (curUser.getCertify()){
            case "0":
                certifyTv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.certify_unknown), null, null, null);
                certifyTv.setText(R.string.certify_unknown);
                break;
            case "1":
                certifyTv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.certify_disabled), null, null, null);
                certifyTv.setText(R.string.certify_disabled);
                break;
            case "2":
                certifyTv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.certify_volunteer), null, null, null);
                certifyTv.setText(R.string.certify_volunteer);
                break;
            case "3":
                certifyTv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.certify_employer), null, null, null);
                certifyTv.setText(R.string.certify_employer);
                break;
            default:
                break;
        }
        //显示性别
        genderTv.setText(curUser.getGender());
        switch (curUser.getGender()){
            case "保密":
                genderTv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.gender_unknown), null, null, null);
                break;
            case "男":
                genderTv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.gender_male), null, null, null);
                break;
            case "女":
                genderTv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.gender_female), null, null, null);
            default:
                break;
        }
        //显示定位
        locationTv.setText(curUser.getLocation());
    }

    private void initViews(){
        appBarLayout = findViewById(R.id.appbar_layout);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);

        backgroundIv = findViewById(R.id.iv_background);
        toolbar = findViewById(R.id.tool_bar);
        //不要忘记设置工具栏
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        avatarToolbarCiv = findViewById(R.id.civ_toolbar_avatar);
        nicknameToolbarTv = findViewById(R.id.tv_toolbar_nickname);

        avatarCiv = findViewById(R.id.civ_avatar);
        nicknameTv = findViewById(R.id.tv_nickname);

        introductionTv = findViewById(R.id.tv_introduction);
        certifyTv = findViewById(R.id.tv_certify);
        genderTv = findViewById(R.id.tv_gender);
        locationTv = findViewById(R.id.tv_location);

        floatingActionButton = findViewById(R.id.floating_action_btn);

        smartRefreshLayout = findViewById(R.id.smart_refresh_layout);
        recyclerView = findViewById(R.id.recycler_view);

        userOptionLl = findViewById(R.id.ll_user_option);
        chatBtn = findViewById(R.id.btn_chat);
        focusBtn = findViewById(R.id.btn_focus);

    }

    private void initEvents(){
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()){
                    //折叠状态
                    avatarToolbarCiv.setVisibility(View.VISIBLE);
                    nicknameToolbarTv.setVisibility(View.VISIBLE);
                }else {
                    //展开状态
                    avatarToolbarCiv.setVisibility(View.GONE);
                    nicknameToolbarTv.setVisibility(View.GONE);
                }
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateTo(ProfileEditActivity.class);
            }
        });

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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        //设置适配器
        postVisitAdapter = new PostVisitAdapter(postList, curUser);
        recyclerView.setAdapter(postVisitAdapter);

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        focusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    //获取工具栏菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_toolbar_more, menu);
        return true;
    }

    //为工具栏菜单绑定单击事件监听器
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.more:
                // TODO:弹出更多选项的对话框
                showToast(String.valueOf(R.string.more));
                break;
            default:
                break;
        }
        return true;
    }
}