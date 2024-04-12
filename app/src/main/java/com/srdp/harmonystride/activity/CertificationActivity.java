package com.srdp.harmonystride.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.dialog.ButtonDialog;
import com.srdp.harmonystride.dialog.EditTextDialog;
import com.srdp.harmonystride.dialog.RadioButtonDialog;
import com.srdp.harmonystride.entity.Certification;
import com.srdp.harmonystride.entity.Result;
import com.srdp.harmonystride.util.HTTPUtil;
import com.srdp.harmonystride.util.ImageUtil;
import com.srdp.harmonystride.util.LogUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;
import com.srdp.harmonystride.util.StringUtil;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CertificationActivity extends BaseActivity {
    public static final int EXIST = 1; //当前用户的认证信息存在
    public static final int INSERT = 2; //认证信息插入
    public static final int UPDATE = 3; //认证信息更新
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
    private Boolean isExist = false;

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case EXIST:
                    loadInfo();
                    break;
                case INSERT:
                    showToast("提交成功，请耐心等待审核");
                    certifyStatusTv.setText(R.string.certify_status_reviewing);
                    certifyInfoTv.setText(R.string.certify_info_default);
                    break;
                case UPDATE:
                    showToast("更新成功，请耐心等待审核");
                    certifyStatusTv.setText(R.string.certify_status_reviewing);
                    certifyInfoTv.setText(R.string.certify_info_default);
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
        certification.setUid(uid);
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
                    isExist = true;
                    LogUtil.d("get certify", "success");
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
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        certifyTypeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RadioButtonDialog(CertificationActivity.this, RadioButtonDialog.EDIT_TYPE_CERTIFY, certifyTypeTv.getText().toString(), new RadioButtonDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(Boolean update, String data) {
                        if(update){
                            certifyTypeTv.setText(data);
                            certification.setType(data);
                            switch (data){
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
                        }
                    }
                }).show();
            }
        });

        certifyNumberTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EditTextDialog(CertificationActivity.this, EditTextDialog.EDIT_TYPE_CERTIFY_NUMBER, certification.getNumber(), new EditTextDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(Boolean update, String data) {
                        if(update){
                            certifyNumberTv.setText(data);
                            certification.setNumber(data);
                        }
                    }
                }).show();
            }
        });

        certifyImageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ButtonDialog(CertificationActivity.this).show();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(StringUtil.isEmpty(certifyTypeTv.getText().toString())){
                    showToast("请选择认证类型");
                }else if(StringUtil.isEmpty(certifyNumberTv.getText().toString())){
                    showToast("请输入证件号");
                }else if(certifyImageIv.getDrawable() == null){
                    showToast("请上传证件照片");
                }else {
                    //提交按钮不可见
                    submitBtn.setVisibility(View.GONE);
                    //查看状态不可点击
                    certifyTypeTv.setClickable(false);
                    certifyNumberTv.setClickable(false);
                    certifyImageIv.setClickable(false);
                    if(isExist){
                        //TODO:更新数据库
                        update();
                    }else {
                        submitBtn.setVisibility(View.GONE);
                        //TODO:插入数据库
                        insert(certification);
                    }
                }
            }
        });
    }

    private void insert(Certification certification){
        HTTPUtil.insert(certification, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("insert request", "error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d("insert request", "success");
                Gson gson = new Gson();
                Result result = gson.fromJson(response.body().string(), Result.class);
                if(result.getCode() == 1){
                    Message message = new Message();
                    message.what = INSERT;
                    handler.sendMessage(message);
                    LogUtil.d(String.valueOf(certification.getUid()), "insert success");
                }
            }
        });
    }

    private void update(){
        certification.setStatus("待审核");
        HTTPUtil.update(certification, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("update request", "error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d("update request", "success");

                Gson gson = new Gson();
                Result result = gson.fromJson(response.body().string(), Result.class);

                if(result.getCode() == 1){
                    Message message = new Message();
                    message.what = UPDATE;
                    handler.sendMessage(message);
                    LogUtil.d(String.valueOf(certification.getUid()), "update success");
                }
            }
        });
    }

    private void loadInfo(){
        //加载认证信息
        certifyTypeTv.setText(certification.getType());
        certifyNumberTv.setText(certification.getNumber());
        Glide.with(this).load(ImageUtil.getImagePath(certification.getImageUrl())).centerCrop().into(certifyImageIv);
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
                if(!certification.getStatus().equals("通过")){
                    //修改认证
                    //提交按钮可见
                    submitBtn.setVisibility(View.VISIBLE);
                    //编辑状态可点击
                    certifyTypeTv.setClickable(true);
                    certifyNumberTv.setClickable(true);
                    certifyImageIv.setClickable(true);
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case ImageUtil.OPEN_ALBUM_CODE:
                    //打开相册返回
                    Uri imageUri = Objects.requireNonNull(data).getData();
                    //图片剪裁
                    ImageUtil.pictureCropping(this, imageUri, 3, 2);
                    break;
                case ImageUtil.OPEN_CAMERA_CODE:
                    Bundle extras = data.getExtras();
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    //图片剪裁
                    ImageUtil.pictureCropping(this, ImageUtil.getImageUri(this, bitmap), 3, 2);//开始裁减图片
                    break;
                case ImageUtil.PICTURE_CROPPING_CODE:
                    //图片剪裁返回
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        //在这里获得了剪裁后的Bitmap对象，可以用于上传
                        Bitmap image = bundle.getParcelable("data");
                        //设置到ImageView上
                        Glide.with(this).load(image).centerCrop().into(certifyImageIv);
                        //上传头像
                        String imageUrl = ImageUtil.CERTIFICATION_PATH + System.currentTimeMillis() + ".png";
                        ImageUtil.upload(certification.getImageUrl(), image, imageUrl, null);
                        certification.setImageUrl(imageUrl);
                    }
                    break;
                default:
                    break;
            }
        }
    }

}