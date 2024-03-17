package com.srdp.harmonystride.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.dialog.EditTextDialog;
import com.srdp.harmonystride.dialog.TipDialog;
import com.srdp.harmonystride.entity.Application;
import com.srdp.harmonystride.entity.Post;
import com.srdp.harmonystride.entity.Result;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.HTTPUtil;
import com.srdp.harmonystride.util.ImageUtil;
import com.srdp.harmonystride.util.LogUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;
import com.srdp.harmonystride.util.StringUtil;
import com.srdp.harmonystride.util.TimeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PostActivity extends BaseActivity {
    private static final int REQUEST_POST_SUCCESS = 1; //获取帖子成功
    private static final int REQUEST_POST_NULL = 2; //帖子不存在

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private ImageView imageView;
    private CircleImageView toolbarAvatarCiv;
    private TextView toolbarNicknameTv;

    private CircleImageView avatarCiv;
    private TextView nicknameTv;

    private LinearLayout postLineartLayout;
    private TextView postTitleTv;
    private WebView postContentWv;
    private TextView postNullTv;
    private FloatingActionButton applyFab;

    private Boolean isSelf; //是否查看自己的帖子
    private Boolean isAllowApply; //是否允许申请
    private int curUid; //当前用户UID
    private int postUid; //发帖人UID
    private String avatar;
    private String nickname;
    private int postId;
    private Post post;

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case REQUEST_POST_SUCCESS:
                    loadDatas();
                    break;
                case REQUEST_POST_NULL:
                    postLineartLayout.setVisibility(View.GONE);
                    postNullTv.setVisibility(View.VISIBLE);
                    applyFab.setVisibility(View.GONE);
                    if(toolbar.getMenu().findItem(R.id.setting) != null){
                        toolbar.getMenu().findItem(R.id.setting).setVisible(false);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        //从设置页返回
        setActivityResultLauncher(
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // 处理返回的数据
                        Intent data = result.getData();
                        // 进行你的操作
                        if(data.getBooleanExtra("update", false)){
                            requestPost(postId);
                        }
                    }
                })
        );

        //初始化数据
        initDatas();
        //初始化视图
        initViews();
        //初始化事件
        initEvents();
//        //请求数据
//        requestPost(postId);
    }

    private void initDatas(){
        Intent intent = getIntent();
        postUid = intent.getIntExtra("user_id", 0);
        avatar = intent.getStringExtra("user_avatar");
        nickname = intent.getStringExtra("user_nickname");
        postId = intent.getIntExtra("post_id", 0);

        curUid = (Integer) SharedPreferenceUtil.getParam("current_uid", 0);
        if(curUid == postUid){
            isSelf = true;
        }else {
            isSelf = false;
        }

        post = new Post();
    }

    private void initViews(){
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        appBarLayout = findViewById(R.id.appbar_layout);
        toolbar = findViewById(R.id.tool_bar);
        //不要忘记设置工具栏
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        imageView = findViewById(R.id.image_view);
        toolbarAvatarCiv = findViewById(R.id.civ_toolbar_avatar);
        toolbarNicknameTv = findViewById(R.id.tv_toolbar_nickname);

        avatarCiv = findViewById(R.id.civ_avatar);
        nicknameTv = findViewById(R.id.tv_nickname);

        postLineartLayout = findViewById(R.id.ll_post_page);
        postTitleTv = findViewById(R.id.tv_post_title);
        postContentWv = findViewById(R.id.wv_post_content);
        postNullTv = findViewById(R.id.tv_post_null);

        applyFab = findViewById(R.id.fab_apply);
    }

    private void initEvents(){
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()){
                    //折叠状态
                    toolbarAvatarCiv.setVisibility(View.VISIBLE);
                    toolbarNicknameTv.setVisibility(View.VISIBLE);
                }else {
                    //展开状态
                    toolbarAvatarCiv.setVisibility(View.GONE);
                    toolbarNicknameTv.setVisibility(View.GONE);
                }
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        toolbarAvatarCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostActivity.this, ProfileActivity.class);
                intent.putExtra("is_self", isSelf);
                intent.putExtra("user_id", postUid);
                navigateTo(intent);
            }
        });

        avatarCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostActivity.this, ProfileActivity.class);
                intent.putExtra("is_self", isSelf);
                intent.putExtra("user_id", postUid);
                navigateTo(intent);
            }
        });

        applyFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSelf){
                    if(isAllowApply){
                        //TODO:查看该帖子的申请列表
                        Intent intent = new Intent(PostActivity.this, ApplyManageActivity.class);
                        intent.putExtra("pid", post.getPid());
                        startActivity(intent);
                    }else {
                        //TODO:修改帖子-是否允许申请
                        new TipDialog(PostActivity.this, "该帖子已关闭申请，是否开放", new TipDialog.OnDismissListener() {
                            @Override
                            public void onDismiss(Boolean isConfirm) {
                                if(isConfirm){
                                    //TODO:修改post.allow
                                    openAllowApply();
                                }
                            }
                        }).show();
                    }
                }else {
                    if(isAllowApply){
                        new EditTextDialog(PostActivity.this, EditTextDialog.EDIT_TYPE_INTRODUCTION, "申请理由", new EditTextDialog.OnDismissListener() {
                            @Override
                            public void onDismiss(Boolean isUpdate, String data) {
                                if(isUpdate){
                                    //TODO:提交申请
                                    submitApply(data);
                                }
                            }
                        }).show();
                    }else {
                        showToast("申请已关闭，请联系发帖人");
                    }
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //请求数据
        requestPost(postId);
    }

    private void loadDatas(){
        //加载工具栏
        Glide.with(this).load(ImageUtil.getImagePath(avatar)).into(toolbarAvatarCiv);
        toolbarNicknameTv.setText(nickname);
        //加载用户信息
        Glide.with(this).load(ImageUtil.getImagePath(avatar)).into(avatarCiv);
        nicknameTv.setText(nickname);
        //加载标题和背景图
        collapsingToolbarLayout.setTitle(post.getTitle());
        if(!StringUtil.isEmpty(post.getImages())){
            Glide.with(MyApplication.getContext()).load(ImageUtil.getImagePath(post.getImages())).into(imageView);
        }
        //加载帖子
        postLineartLayout.setVisibility(View.VISIBLE);
        postNullTv.setVisibility(View.GONE);
        postTitleTv.setText(post.getTitle());
        postContentWv.loadDataWithBaseURL(null, post.getContent(), "text/html", "UTF-8", null);
        if(post.getAllow().equals("0")){
            isAllowApply = false;
        }else if(post.getAllow().equals("1")){
            isAllowApply = true;
        }
    }

    private void requestPost(int pid){
        Map<String, Object> params = new HashMap<>();
        params.put("pid", String.valueOf(pid));
        HTTPUtil.POST(params, "/post/find", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("post allow request", "error" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d("post allow request", "success");
                try {
                    Message message = new Message();
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String postS = jsonObject.getJSONObject("data").getString("post");
                    Gson gson = new Gson();
                    Post newPost = gson.fromJson(postS, new TypeToken<Post>(){}.getType());
                    if(newPost != null){
                        //获取新数据
                        post.copyFromPost(newPost);
                        //传递消息
                        message.what = REQUEST_POST_SUCCESS;
                    }else {
                        //传递消息
                        message.what = REQUEST_POST_NULL;
                    }
                    handler.sendMessage(message);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //TODO 修改post.allow
    private void openAllowApply(){
        //TODO 发起update请求
    }

    //TODO:提交申请
    private void submitApply(String reason){
        Application application = new Application(curUid, post.getPid(), TimeUtil.formatLocalDateTime(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"), reason, "0");
        //TODO:发起insert请求
    }

    //获取工具栏菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_setting, menu);
        MenuItem menuItem = menu.findItem(R.id.setting);
        if(!isSelf){
            menuItem.setVisible(false);
        }
        return true;
    }

    //为工具栏菜单绑定单击事件监听器
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:
                Intent intent = new Intent(MyApplication.getContext(), PostSettingActivity.class);
                intent.putExtra("pid", post.getPid());
                intent.putExtra("allow", post.getAllow());
                navigateForResult(intent);
                break;
            default:
                break;
        }
        return true;
    }

}