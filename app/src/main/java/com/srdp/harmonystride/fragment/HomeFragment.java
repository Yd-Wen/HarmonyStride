package com.srdp.harmonystride.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
//import com.hyphenate.chat.EMClient;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.activity.CertificationActivity;
import com.srdp.harmonystride.activity.LoginActivity;
import com.srdp.harmonystride.activity.PostingActivity;
import com.srdp.harmonystride.activity.ProfileActivity;
import com.srdp.harmonystride.activity.SearchActivity;
import com.srdp.harmonystride.activity.SettingActivity;
import com.srdp.harmonystride.adapter.PostBriefAdapter;
import com.srdp.harmonystride.dialog.TipDialog;
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

public class HomeFragment extends Fragment{
    private static final int LOADMORE_SUCCESS = 1; //请求刷新数据
    private static final int REFRESH_SUCCESS = 2; //刷新成功

    private View view;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private TextView titleTv;
    private CircleImageView toolbarAvatarCiv;
    private TextView toolbarNicknameTv;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private CircleImageView avatarCiv;
    private TextView nicknameTv;
    private ImageView certifyIv;
    private ImageView postIv;

    private FloatingActionButton floatingActionButton;

    private ImageView searchIv;

    private RadioGroup tagRg;
    private RadioButton tagAllRb;
    private RadioButton tagVolunteerRb;
    private RadioButton tagJobRb;
    private RadioButton tagHelpRb;
    private RadioButton tagRecruitmentRb;

    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;

    private ScrollUtil scrollUtil; // 滑动工具
    private static final int SCROLL_THRESHOLD = 1000; // 设置滑动阈值

    private List<Post> postList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    private User curUser; //当前用户
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        //初始化视图
        initViews();
        //初始化事件
        initEvents();
        // Inflate the layout for this fragment
        return view;
    }

    private void initDatas(){
        //通过账户名从本地数据库读取当前用户信息
        List<User> Users = LitePal.where("account = ?", SharedPreferenceUtil.getParam("current_account", "").toString()).find(User.class);
        curUser = Users.get(0);
        curTag = getString(R.string.post_label_all);
    }

    private void requestDatas(String time, String label, Boolean isLoadMore){
        //发送请求，获取帖子简略页列表，包括Post列表和User列表
        Map<String, Object> params = new HashMap<>();
        params.put("time", time);
        params.put("label", label);
        HTTPUtil.POST(params, "/post/browse", new Callback() {
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

//    private void requestDatas(String time, String curTag, Boolean isLoadMore){
//        //暂停一秒
//
//        if(!isLoadMore){
//            postList.clear();
//            userList.clear();
//        }
//        //发送请求，获取帖子简略页列表，包括Post列表和User列表
//        Post post = new Post(1, 1, "2024-01-01", "title", "content", "志愿", null, 1, "1");
//        for(int i = 0; i < 10; i++){
//            postList.add(post);
//        }
//        User user = new User("account", "password", null, "nickname", "0", "保密", "", "系统原装签名：与你同行", "0");
//        for(int i = 0; i < 10; i++){
//            userList.add(user);
//        }
//
//        //传递消息
//        Message message = new Message();
//        if(isLoadMore){
//            message.what = LOADMORE_SUCCESS;
//        }else {
//            message.what = REFRESH_SUCCESS;
//        }
//        handler.sendMessage(message);
//    }

    private void initViews(){
        //获取drawerLayout
        drawerLayout = view.findViewById(R.id.drawer_layout);

        //获取滑动导航栏
        navigationView = view.findViewById(R.id.navigation_view);
        //获取header内部控件
        avatarCiv = navigationView.getHeaderView(0).findViewById(R.id.civ_avatar);
        nicknameTv = navigationView.getHeaderView(0).findViewById(R.id.tv_nickname);

        //获取工具栏
        appBarLayout = view.findViewById(R.id.appbar_layout);
        toolbar = view.findViewById(R.id.tool_bar);
        titleTv = view.findViewById(R.id.tv_toolbar_title);
        toolbarAvatarCiv = view.findViewById(R.id.civ_toolbar_avatar);
        toolbarNicknameTv = view.findViewById(R.id.tv_toolbar_nickname);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        //删除标题
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(null);
        //设置新标题
        titleTv.setText(R.string.home);

        certifyIv = view.findViewById(R.id.iv_certify);
        postIv = view.findViewById(R.id.iv_post);

        floatingActionButton = view.findViewById(R.id.floating_action_btn);

        searchIv = view.findViewById(R.id.iv_search);
        tagRg = view.findViewById(R.id.rg_label);
        tagAllRb = view.findViewById(R.id.rb_label_a);
        tagVolunteerRb = view.findViewById(R.id.rb_label_v);
        tagJobRb = view.findViewById(R.id.rb_label_j);
        tagHelpRb = view.findViewById(R.id.rb_label_h);
        tagRecruitmentRb = view.findViewById(R.id.rb_label_r);

        //获取布局
        smartRefreshLayout = view.findViewById(R.id.smart_refresh_layout);
        recyclerView = view.findViewById(R.id.recycler_view);

        //初始化滑动工具
        scrollUtil = new ScrollUtil(getActivity().findViewById(R.id.bottom_navigation_view), recyclerView);

    }

    private void initEvents(){
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()){
                    //折叠状态
                    toolbarAvatarCiv.setVisibility(View.VISIBLE);
                    toolbarNicknameTv.setVisibility(View.VISIBLE);
                    titleTv.setVisibility(View.GONE);
                }else {
                    //展开状态
                    toolbarAvatarCiv.setVisibility(View.GONE);
                    toolbarNicknameTv.setVisibility(View.GONE);
                    titleTv.setVisibility(View.VISIBLE);
                }
            }
        });

        toolbarAvatarCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        titleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        certifyIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent certifyIntent = new Intent(getActivity(), CertificationActivity.class);
                startActivity(certifyIntent);
            }
        });

        postIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PostingActivity.class);
                startActivity(intent);
            }
        });

        //滑动菜单单击事件监听器
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.profile:
                        drawerLayout.closeDrawers();
                        Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
                        profileIntent.putExtra("is_self", true);
                        profileIntent.putExtra("user_id", curUser.getUid());
                        startActivity(profileIntent);
                        break;
                    case R.id.certify:
                        drawerLayout.closeDrawers();
                        Intent certifyIntent = new Intent(getActivity(), CertificationActivity.class);
                        startActivity(certifyIntent);
                        break;
                    case R.id.setting:
                        drawerLayout.closeDrawers();
                        Intent settingIntent = new Intent(getActivity(), SettingActivity.class);
                        startActivity(settingIntent);
                        break;
                    default: break;
                }
                return true;
            }
        });

        //悬浮按钮单击事件监听器
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PostingActivity.class);
                startActivity(intent);
            }
        });

        //搜索框
        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        //筛选
        tagRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {

                tagAllRb.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                tagVolunteerRb.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                tagJobRb.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                tagHelpRb.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                tagRecruitmentRb.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                switch (id){
                    case R.id.rb_label_a:
                        //tagAllRb.getPaint().setFakeBoldText(true);
                        tagAllRb.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        curTag = getString(R.string.post_label_all);
                        break;
                    case R.id.rb_label_v:
                        tagVolunteerRb.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        curTag = getString(R.string.post_label_volunteer);
                        break;
                    case R.id.rb_label_j:
                        tagJobRb.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        curTag = getString(R.string.post_label_job);
                        break;
                    case R.id.rb_label_h:
                        tagHelpRb.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        curTag = getString(R.string.post_label_help);
                        break;
                    case R.id.rb_label_r:
                        tagRecruitmentRb.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        curTag = getString(R.string.post_label_recruitment);
                        break;
                    default:
                        break;
                }
                requestDatas(TimeUtil.formatLocalDateTime(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"), curTag,false);
                //RadioButton radioButton = view.findViewById(id);
            }
        });

        //添加下拉刷新事件监听器
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                LogUtil.e("tag", curTag == null?"null":curTag);
                requestDatas(TimeUtil.formatLocalDateTime(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"), curTag,false);
            }
        });
        //上滑加载更多
        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                String time = postList.get(postList.size()-1).getDatetime();
                requestDatas(time, curTag, true);
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
                    scrollUtil.setItem(R.drawable.home, R.string.status);
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

    private void loadUserInfo(){
        //显示当前用户信息
        if(!StringUtil.isEmpty(curUser.getAvatar())){ //头像资源路径不为空
            Glide.with(this).load(ImageUtil.getImagePath(curUser.getAvatar())).apply(ImageUtil.requestOptionsWithCircle).into(avatarCiv);
            Glide.with(this).load(ImageUtil.getImagePath(curUser.getAvatar())).apply(ImageUtil.requestOptionsWithCircle).into(toolbarAvatarCiv);
        }
        nicknameTv.setText(curUser.getNickname());
        toolbarNicknameTv.setText(curUser.getNickname());
        if(curUser.getCertify().equals("0")) {
            navigationView.getMenu().findItem(R.id.certify).setTitle(R.string.no_certify);
            navigationView.getMenu().findItem(R.id.certify).setIcon(R.drawable.certify_null);
        } else {
            navigationView.getMenu().findItem(R.id.certify).setTitle(R.string.certify);
            navigationView.getMenu().findItem(R.id.certify).setIcon(R.drawable.certifiy);
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
    public void onStart() {
        super.onStart();
        //初始化数据
        initDatas();
        //加载用户数据
        loadUserInfo();
        //请求数据
        requestDatas(TimeUtil.formatLocalDateTime(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"), curTag,false);
    }
}