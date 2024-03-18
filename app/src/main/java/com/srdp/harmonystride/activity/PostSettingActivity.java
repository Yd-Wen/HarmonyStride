package com.srdp.harmonystride.activity;


import static com.srdp.harmonystride.util.ImageUtil.getImagePath;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.dialog.TipDialog;
import com.srdp.harmonystride.entity.Post;
import com.srdp.harmonystride.entity.Result;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.HTTPUtil;
import com.srdp.harmonystride.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PostSettingActivity extends BaseActivity {
    private static final int POST_DELETE = 1; //删除帖子
    private static final int POST_ALLOW_TRUE = 2; //允许申请
    private static final int POST_ALLOW_FALSE = 3;  //禁止申请

    private Toolbar toolbar;

    private Switch applyAllowSw;
    private TextView applyFindTv;
    private TextView postDelete;

    private int curPid; //当前帖子的pid
    private String allow; //当前帖子的申请状态

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case POST_DELETE:
                    showToast("帖子删除成功");
                    Intent resutltIntent = new Intent();
                    resutltIntent.putExtra("update", true);
                    setResult(Activity.RESULT_OK, resutltIntent);
                    finish();
                    break;
                case POST_ALLOW_TRUE:
                    showToast("申请已开放");
                    allow = "1";
                    break;
                case POST_ALLOW_FALSE:
                    showToast("申请已关闭");
                    allow = "0";
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_setting);
        //初始化数据
        initDatas();
        //初始化视图
        initViews();
        //初始化事件
        initEvents();
    }

    private void initDatas() {
        curPid = getIntent().getIntExtra("pid", 0);
        allow = getIntent().getStringExtra("allow");
    }

    private void initViews() {
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        applyAllowSw = findViewById(R.id.sw_apply_allow);
        if(allow.equals("0")){
            applyAllowSw.setChecked(false);
        }else if(allow.equals("1")){
            applyAllowSw.setChecked(true);
        }

        applyFindTv = findViewById(R.id.tv_apply_find);
        postDelete = findViewById(R.id.tv_post_delete);
    }

    private void initEvents() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resutltIntent = new Intent();
                resutltIntent.putExtra("update", true);
                setResult(Activity.RESULT_OK, resutltIntent);
                finish();
            }
        });

        applyAllowSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) updateAllowTrue(curPid);
                else updateAllowFalse(curPid);
            }
        });

        applyFindTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:查找申请
                Intent intent = new Intent(MyApplication.getContext(), ApplyManageActivity.class);
                intent.putExtra("pid", curPid);
                navigateTo(intent);
            }
        });

        postDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TipDialog(PostSettingActivity.this, "确定删除该帖子？该操作不可逆", new TipDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(Boolean isConfirm) {
                        if(isConfirm){
                            deletePost(curPid);
                        }
                    }
                }).show();
            }
        });
    }

    private void updateAllowTrue(int pid) {
        //TODO:修改申请权限
        Map<String, Object> params = new HashMap<>();
        params.put("pid", String.valueOf(pid));
        HTTPUtil.POST(params, "/post/agree", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("post allow request", "error" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d("post allow request", "success");
                Gson gson = new Gson();
                Result result = gson.fromJson(response.body().string(), Result.class);
                //传递消息
                if(result.getCode()==1){
                    Message message = new Message();
                    message.what = POST_ALLOW_TRUE;
                    handler.sendMessage(message);
                }
            }
        });
    }

    private void updateAllowFalse(int pid) {
        Map<String, Object> params = new HashMap<>();
        params.put("pid", String.valueOf(pid));
        HTTPUtil.POST(params, "/post/deny", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("post allow request", "error" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d("post allow request", "success");
                Gson gson = new Gson();
                Result result = gson.fromJson(response.body().string(), Result.class);
                //传递消息
                if(result.getCode()==1){
                    Message message = new Message();
                    message.what = POST_ALLOW_FALSE;
                    handler.sendMessage(message);
                }
            }
        });
    }

    private void deletePost(int pid) {
        Map<String, Object> params = new HashMap<>();
        params.put("pid", String.valueOf(pid));
        HTTPUtil.POST(params, "/post/done", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("post delete request", "error" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d("post delete request", "success");
                Gson gson = new Gson();
                Result result = gson.fromJson(response.body().string(), Result.class);
                //传递消息
                if(result.getCode()==1){
                    Message message = new Message();
                    message.what = POST_DELETE;
                    handler.sendMessage(message);
                }
            }
        });
    }
}