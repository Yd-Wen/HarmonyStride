package com.srdp.harmonystride.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
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
import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.adapter.CommentAdapter;
import com.srdp.harmonystride.dialog.EditTextDialog;
import com.srdp.harmonystride.dialog.TipDialog;
import com.srdp.harmonystride.entity.Application;
import com.srdp.harmonystride.entity.Comment;
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
import java.util.ArrayList;
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
    private static final int APPLY_SUCCESS = 3; //申请提交
    private static final int APPLY_EXIST = 4; //已存在申请
    private static final int COMMENT_SUCCESS = 5; //评论发布成功
    private static final int REQUEST_COMMENT_SUCCESS = 6; //评论获取成功

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

    private EditText commentEt;
    private Button commentBtn;

    private Boolean isSelf; //是否查看自己的帖子
    private Boolean isAllowApply; //是否允许申请
    private int curUid; //当前用户UID
    private int postUid; //发帖人UID
    private String avatar;
    private String nickname;
    private int postId;
    private Post post;

    private RecyclerView commentRv;
    private CommentAdapter commentAdapter;
    private List<User> userList = new ArrayList<>();
    private List<Comment> commentList = new ArrayList<>();

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
                case APPLY_SUCCESS:
                    showToast(getResources().getString(R.string.apply_submit_yes));
                    break;
                case APPLY_EXIST:
                    showToast(getResources().getString(R.string.apply_submit_no));
                    break;
                case COMMENT_SUCCESS:
                    showToast("发布成功");
                    commentEt.setText("");
                    //暂停1秒
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 这里放置你要在一秒后执行的代码，例如发起网络请求
                            requestComment(postId);
                        }
                    }, 1000); // 延迟1000毫秒，即1秒
                    break;
                case REQUEST_COMMENT_SUCCESS:
                    commentAdapter.notifyDataSetChanged();
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

        commentEt = findViewById(R.id.et_comment);
        commentBtn = findViewById(R.id.btn_comment);

        applyFab = findViewById(R.id.fab_apply);

        //无法申请自己的帖子
        if(isSelf) applyFab.setVisibility(View.GONE);

        commentRv = findViewById(R.id.rv_comment);

        //设置布局管理器
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        commentRv.setLayoutManager(gridLayoutManager);

        //设置适配器
        commentAdapter = new CommentAdapter(userList, commentList);
        commentRv.setAdapter(commentAdapter);
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
                if(SharedPreferenceUtil.getParam("current_certify", "").equals("未认证")){
                    new TipDialog(PostActivity.this, "未认证，是否刷新认证信息", new TipDialog.OnDismissListener() {
                        @Override
                        public void onDismiss(Boolean isConfirm) {
                            if(isConfirm){
                                navigateTo(CertificationActivity.class);
                            }
                        }
                    }).show();
                }else if(isAllowApply){
                    new EditTextDialog(PostActivity.this, EditTextDialog.EDIT_TYPE_APPLY_REASON, "", new EditTextDialog.OnDismissListener() {
                        @Override
                        public void onDismiss(Boolean isUpdate, String data) {
                            if(isUpdate){
                                submitApply(data);
                            }
                        }
                    }).show();
                }else {
                    showToast("申请已关闭，请联系发帖人开放申请");
                }
            }
        });

        commentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!StringUtil.isEmpty(commentEt.getText().toString())){
                    //文本不为空
                    commentBtn.setEnabled(true);
                    commentBtn.setTextColor(getResources().getColor(R.color.colorWhite, null));
                }else {
                    //文本为空
                    commentBtn.setEnabled(false);
                    commentBtn.setTextColor(getResources().getColor(R.color.colorText, null));
                }
            }
        });

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SharedPreferenceUtil.getParam("current_certify", "").equals("未认证")){
                    new TipDialog(PostActivity.this, "未认证，是否刷新认证信息", new TipDialog.OnDismissListener() {
                        @Override
                        public void onDismiss(Boolean isConfirm) {
                            if(isConfirm){
                                navigateTo(CertificationActivity.class);
                            }
                        }
                    }).show();
                }else {
                    //TODO 发布评论
                    Comment comment = new Comment();
                    comment.setOwner((Integer) SharedPreferenceUtil.getParam("current_uid", 0));
                    comment.setPid(postId);
                    comment.setDatetime(TimeUtil.formatLocalDateTime(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"));
                    comment.setContent(commentEt.getText().toString());
                    submitComment(comment);
                }
            }
        });

        //添加滑动事件监听器
        commentRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    }

    @Override
    protected void onStart() {
        super.onStart();
        //请求数据
        requestPost(postId);
        requestComment(postId);
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

    private void requestComment(int pid){
        //TODO 请求评论
        Map<String, Object> params = new HashMap<>();
        params.put("pid", String.valueOf(pid));
        params.put("time", TimeUtil.formatLocalDateTime(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"));
        HTTPUtil.POST(params, "/comment/browse", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("comment browse request", "error" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d("comment browse request", "success");
                try {
                    Message message = new Message();
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String commentS = jsonObject.getJSONObject("data").getString("commentList");
                    String userS = jsonObject.getJSONObject("data").getString("userList");
                    Gson gson = new Gson();
                    List<Comment> commentL = gson.fromJson(commentS, new TypeToken<List<Comment>>(){}.getType());
                    List<User> userL = gson.fromJson(userS, new TypeToken<List<User>>(){}.getType());
                    //获取新数据
                    commentList.clear();
                    userList.clear();
                    for(int i = 0; i < commentL.size(); ++i){
                        commentList.add(commentL.get(i));
                        userList.add(userL.get(i));
                    }
                    message.what = REQUEST_COMMENT_SUCCESS;
                    handler.sendMessage(message);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void submitComment(Comment comment){
        //TODO 提交评论
        String json = new Gson().toJson(comment);
        Map<String, Object> map = new HashMap<>();
        map.put("comment", json);
        HTTPUtil.POST(map, "/comment/add", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("comment add request", "error" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d("comment add request", "success");
                Gson gson = new Gson();
                Result result = gson.fromJson(response.body().string(), Result.class);
                //传递消息
                Message message = new Message();
                if(result.getCode()==1){
                    message.what = COMMENT_SUCCESS;
                }
                handler.sendMessage(message);
            }
        });
    }

    //提交申请
    private void submitApply(String reason){
        Application application = new Application(curUid, post.getPid(), post.getDatetime(), reason, "0");
        String json = new Gson().toJson(application);
        Map<String, Object> map = new HashMap<>();
        map.put("application", json);
        HTTPUtil.POST(map, "/application/add", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("application add request", "error" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d("application add request", "success");
                Gson gson = new Gson();
                Result result = gson.fromJson(response.body().string(), Result.class);
                //传递消息
                Message message = new Message();
                if(result.getCode()==1){
                    message.what = APPLY_SUCCESS;
                }else {
                    message.what = APPLY_EXIST;
                }
                handler.sendMessage(message);
            }
        });


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