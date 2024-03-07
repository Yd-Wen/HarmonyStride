package com.srdp.harmonystride.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.dialog.EditTextDialog;
import com.srdp.harmonystride.dialog.RadioButtonDialog;
import com.srdp.harmonystride.entity.Certification;
import com.srdp.harmonystride.entity.Result;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.HTTPUtil;
import com.srdp.harmonystride.util.ImageUtil;
import com.srdp.harmonystride.util.LogUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CertificationActivity extends BaseActivity {
    //Glide请求图片选项配置
    private RequestOptions requestOptions = RequestOptions
            .circleCropTransform()//圆形剪裁
            .diskCacheStrategy(DiskCacheStrategy.NONE);//不做磁盘缓存
    //.skipMemoryCache(true);//不做内存缓存
    public static final int NOT_EXIST = 0; //当前用户的认证信息不存在
    public static final int EXIST = 1; //当前用户的认证信息存在
    private Toolbar toolbar;

    private TextView certifyTypeTv;
    private TextView certifyNumberTypeTv;
    private TextView certifyNumberTv;
    private TextView certifyImageTv;
    private ImageView certifyImageIv;

    private TextView certifyStatusTv;
    private TextView certifyInfoTv;

    private Button submitBtn;

    private int uid; //当前用户uid
    private Certification certification;

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case NOT_EXIST:

                    break;
                case EXIST:
                    loadInfo();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certification);
        //初始化数据
        initDatas();
        //初始化视图
        initViews();
        //初始化事件
        initEvents();
    }

    private void initDatas(){
        uid = (Integer)SharedPreferenceUtil.getParam("current_uid", 0);
        certification = new Certification();
        //判断是否提交过认证
        isCertify();
    }

    private void isCertify(){
        HTTPUtil.getCertify(uid, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e(String.valueOf(uid), " request error " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d(String.valueOf(uid), " request success");
                Gson gson = new Gson();
                String responseBody = response.body().string();
                Result result = gson.fromJson(responseBody, Result.class);

                Message message = new Message();
                if(result.getCode() == 1){
                    JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                    JsonObject data = jsonObject.getAsJsonObject("data");
                    //注意不要用toString，否则引号会保留到字符串
                    certification.setUid(data.get("uid").getAsInt());
                    certification.setType(data.get("type").getAsString());
                    certification.setNumber(data.get("number").getAsString());
                    certification.setImageUrl(data.get("imageurl").getAsString());
                    certification.setStatus(data.get("status").getAsString());
                    certification.setInfo(data.get("info").getAsString());

                    message.what = EXIST;
                    handler.sendMessage(message);
                    LogUtil.d("get certify", "success");
                }else {
                    message.what = NOT_EXIST;
                    handler.sendMessage(message);
                    LogUtil.d("get certify", "error");
                }
            }
        });
    }

    private void initViews(){
        toolbar = findViewById(R.id.tool_bar);
        //不要忘记设置工具栏
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        certifyTypeTv = findViewById(R.id.tv_certify_type);
        certifyNumberTypeTv = findViewById(R.id.tv_certify_number_type);
        certifyNumberTv = findViewById(R.id.tv_certify_number);
        certifyImageTv = findViewById(R.id.tv_certify_image);
        certifyImageIv = findViewById(R.id.iv_certify_image);
        certifyStatusTv = findViewById(R.id.tv_certify_status);
        certifyInfoTv = findViewById(R.id.tv_certify_info);
        submitBtn = findViewById(R.id.btn_submit);

    }

    private void initEvents(){
        certifyTypeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RadioButtonDialog(CertificationActivity.this, RadioButtonDialog.EDIT_TYPE_CERTIFY, certifyTypeTv.getText().toString(), new RadioButtonDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(Boolean update, String data) {
                        if(update){
                            certifyTypeTv.setText(data);
                            certification.setType(data);
                        }
                    }
                }).show();
            }
        });

        certifyNumberTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        certifyImageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void loadInfo(){
        //加载认证信息
        certifyTypeTv.setText(certification.getType());
        certifyNumberTv.setText(certification.getNumber());
        Glide.with(this).load(ImageUtil.getImagePath(certification.getImageUrl())).apply(requestOptions).into(certifyImageIv);
        certifyStatusTv.setText(certification.getStatus());
        certifyInfoTv.setText(certification.getInfo());
        switch (certification.getType()){
            case "残疾人":
                certifyNumberTypeTv.setText(R.string.certify_disabled_number);
                certifyImageTv.setText(R.string.certify_disabled_image);
                break;
            case "志愿者":
                certifyNumberTypeTv.setText(R.string.certify_volunteer_number);
                certifyImageTv.setText(R.string.certify_volunteer_image);
                break;
            case "用人单位":
                certifyNumberTypeTv.setText(R.string.certify_employer_number);
                certifyImageTv.setText(R.string.certify_employer_image);
                break;
            case "管理员":
                certifyNumberTypeTv.setText(R.string.certify_admin_number);
                certifyImageTv.setText(R.string.certify_admin_image);
                break;
            default:
                break;
        }
        //提交按钮不可见
        submitBtn.setVisibility(View.GONE);
        //查看状态不可点击
        certifyTypeTv.setClickable(false);
        certifyNumberTv.setClickable(false);
        certifyImageIv.setClickable(false);
    }


    //获取工具栏菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_toolbar_edit, menu);
        return true;
    }

    //为工具栏菜单绑定单击事件监听器
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.edit:
                //TODO：修改认证
                //提交按钮可见
                submitBtn.setVisibility(View.VISIBLE);
                //编辑状态可点击
                certifyTypeTv.setClickable(true);
                certifyNumberTv.setClickable(true);
                certifyImageIv.setClickable(true);
                break;
            default:
                break;
        }
        return true;
    }

}