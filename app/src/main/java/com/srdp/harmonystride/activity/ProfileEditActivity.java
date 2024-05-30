package com.srdp.harmonystride.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.dialog.BaseDialog;
import com.srdp.harmonystride.dialog.factory.ButtonSheetDialogFactory;
import com.srdp.harmonystride.dialog.factory.DialogFactory;
import com.srdp.harmonystride.dialog.factory.EditDialogFactory;
import com.srdp.harmonystride.dialog.factory.SigleChoiceDialogFactory;
import com.srdp.harmonystride.entity.Result;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.ImageUtil;
import com.srdp.harmonystride.util.HTTPUtil;
import com.srdp.harmonystride.util.LogUtil;
import com.srdp.harmonystride.util.PermissionUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;
import com.srdp.harmonystride.util.StringUtil;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProfileEditActivity extends BaseActivity {
    private static final int UPDATE_SUCCESS = 1; //更新成功

    private Toolbar toolbar;
    private CircleImageView avatarCiv;
    private TextView nicknameTv;
    private TextView genderTv;
    private TextView locationTv;
    private TextView introductionTv;

    private User curUser;
    private Boolean isUpdate = false;

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_SUCCESS:
                    showToast("修改成功");
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        //初始化数据
        initDatas();
        //初始化视图
        initViews();
        //初始化事件
        initEvents();
    }

    private void initDatas(){
        List<User> users = LitePal.where("account = ?", SharedPreferenceUtil.getParam("current_account", "").toString()).find(User.class);
        curUser = users.get(0);
    }

    private void initViews(){
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        avatarCiv = findViewById(R.id.civ_avatar);
        nicknameTv = findViewById(R.id.tv_nickname);
        genderTv = findViewById(R.id.tv_gender);
        locationTv = findViewById(R.id.tv_location);
        introductionTv = findViewById(R.id.tv_introduction);

        //获取头像
        if(!StringUtil.isEmpty(curUser.getAvatar())){ //头像资源路径不为空
            Glide.with(this).load(ImageUtil.getImagePath(curUser.getAvatar())).apply(ImageUtil.requestOptionsWithCircle).into(avatarCiv);
        }
        nicknameTv.setText(curUser.getNickname());
        genderTv.setText(curUser.getGender());
        locationTv.setText(curUser.getLocation());
        introductionTv.setText(curUser.getIntroduction());
    }

    private void initEvents(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isUpdate){
                    update();
                }else {
                    finish();
                }
            }
        });

        avatarCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                factory = new ButtonSheetDialogFactory();
                List<String> data = new ArrayList<>();
                data.add(PermissionUtil.TYPE_PHOTO_ALBUM);
                data.add(PermissionUtil.TYPE_OPEN_CAMERA);
                dialog = factory.createDialog(ProfileEditActivity.this, null, data, null, new BaseDialog.MyDialogListener() {
                    @Override
                    public void onClick(Boolean isConfirm, String data) {
                        if(isConfirm){
                            if(data.equals(PermissionUtil.TYPE_PHOTO_ALBUM)){
                                //选择相册
                                PermissionUtil.getInstance(ProfileEditActivity.this).requestExternalPermission();
                            }else if(data.equals(PermissionUtil.TYPE_OPEN_CAMERA)){
                                //拍照
                                PermissionUtil.getInstance(ProfileEditActivity.this).requestCameraPermission();
                            }
                        }
                    }
                });
                dialog.show();
            }
        });

        nicknameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                factory = new EditDialogFactory();
                List<String> data = new ArrayList<>();
                data.add(curUser.getNickname());
                data.add(DialogFactory.DIALOG_HINT_ACCOUNT_NICKNAME);
                dialog = factory.createDialog(ProfileEditActivity.this, DialogFactory.DIALOG_TITLE_ACCOUNT_NICKNAME, data, null, new BaseDialog.MyDialogListener() {
                    @Override
                    public void onClick(Boolean isConfirm, String data) {
                        if(isConfirm){
                            //更新本地数据库
                            User user = new User();
                            //修改昵称
                            user.setNickname(data);
                            user.updateAll("account = ?", curUser.getAccount());
                            //显示新数据
                            nicknameTv.setText(data);
                            isUpdate = true;
                        }
                    }
                });
                dialog.show();
            }
        });

        genderTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                factory = new SigleChoiceDialogFactory();
                List<String> data = new ArrayList<>();
                List<Integer> resId = new ArrayList<>();
                data.add(genderTv.getText().toString());
                data.add(getResources().getString(R.string.gender_male));
                data.add(getResources().getString(R.string.gender_female));
                data.add(getResources().getString(R.string.gender_unknown));
                resId.add(R.drawable.gender_m);
                resId.add(R.drawable.gender_f);
                resId.add(R.drawable.gender_u);
                dialog = factory.createDialog(ProfileEditActivity.this, DialogFactory.DIALOG_TITLE_ACCOUNT_GENDER, data, resId, new BaseDialog.MyDialogListener() {
                    @Override
                    public void onClick(Boolean isConfirm, String data) {
                        if(isConfirm){
                            //更新本地数据库
                            User user = new User();
                            user.setGender(data);
                            user.updateAll("account = ?", curUser.getAccount());
                            genderTv.setText(data);
                            isUpdate = true;
                        }
                    }
                });
                dialog.show();
            }
        });

        locationTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        introductionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                factory = new EditDialogFactory();
                List<String> data = new ArrayList<>();
                data.add(curUser.getIntroduction());
                data.add(DialogFactory.DIALOG_HINT_ACCOUNT_INTRODUCTION);
                dialog = factory.createDialog(ProfileEditActivity.this, DialogFactory.DIALOG_TITLE_ACCOUNT_INTRODUCTION, data, null, new BaseDialog.MyDialogListener() {
                    @Override
                    public void onClick(Boolean isConfirm, String data) {
                        if(isConfirm){
                            //更新本地数据库
                            User user = new User();
                            //修改签名
                            user.setIntroduction(data);
                            user.updateAll("account = ?", curUser.getAccount());
                            //显示新数据
                            introductionTv.setText(data);
                            isUpdate = true;
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    //更新服务端
    public void update(){
        //重新读取当前用户（用户信息已修改）
        List<User> users = LitePal.where("account = ?", SharedPreferenceUtil.getParam("current_account", "").toString()).find(User.class);
        curUser = users.get(0);
        //更新服务端数据库
        Message message = new Message();
        HTTPUtil.update(curUser, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e(curUser.getAccount(), " request error " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d(curUser.getAccount(), "request success");

                Gson gson = new Gson();
                Result result = gson.fromJson(response.body().string(), Result.class);

                if(result.getCode() == 1){
                    message.what = UPDATE_SUCCESS;
                    handler.sendMessage(message);
                    LogUtil.d(curUser.getAccount(), "update success");
                }
            }
        });
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
                    ImageUtil.pictureCropping(this, imageUri, 1, 1);
                    break;
                case ImageUtil.OPEN_CAMERA_CODE:
                    Bundle extras = data.getExtras();
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    ImageUtil.pictureCropping(this, ImageUtil.getImageUri(this, bitmap), 1, 1);//开始裁减图片
                    break;
                case ImageUtil.PICTURE_CROPPING_CODE:
                    //图片剪裁返回
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        //在这里获得了剪裁后的Bitmap对象，可以用于上传
                        Bitmap image = bundle.getParcelable("data");
                        //设置到ImageView上
                        Glide.with(this).load(image).apply(ImageUtil.requestOptionsWithCircle).into(avatarCiv);
                        //上传头像
                        String imageUrl = ImageUtil.USER_PATH + System.currentTimeMillis() + ".png";
                        ImageUtil.upload(curUser.getAvatar(), image, imageUrl, null);
                        //更新本地数据库
                        User user = new User();
                        user.setAvatar(imageUrl);
                        user.updateAll("account = ?", SharedPreferenceUtil.getParam("current_account", "").toString());
                        //修改更新标识
                        isUpdate = true;
                    }
                    break;
                default:
                    break;
            }
        }
    }

}