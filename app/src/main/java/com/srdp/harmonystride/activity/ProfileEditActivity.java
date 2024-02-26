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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.dialog.ButtonDialog;
import com.srdp.harmonystride.dialog.CheckBoxDialog;
import com.srdp.harmonystride.dialog.EditTextDialog;
import com.srdp.harmonystride.entity.Result;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.ImageUtil;
import com.srdp.harmonystride.util.HTTPUtil;
import com.srdp.harmonystride.util.LogUtil;
import com.srdp.harmonystride.util.OSSClientUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;
import com.srdp.harmonystride.util.StringUtil;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProfileEditActivity extends BaseActivity {
    //外部存储权限请求码
    private static final int REQUEST_EXTERNAL_STORAGE_CODE = 901;
    //相机权限请求码
    private static final int REQUEST_CAMERA_CODE = 902;
    //打开相册请求码
    private static final int OPEN_ALBUM_CODE = 100;
    //打开摄像头请求码
    private static final int OPEN_CAMERA_CODE = 200;
    //图片剪裁请求码
    public static final int PICTURE_CROPPING_CODE = 300;
    //Glide请求图片选项配置
    private RequestOptions requestOptions = RequestOptions
            .circleCropTransform()//圆形剪裁
            .diskCacheStrategy(DiskCacheStrategy.NONE);//不做磁盘缓存
            //.skipMemoryCache(true);//不做内存缓存

    private static final int UPDATE_SUCCESS = 1; //更新成功
    private static final String USER_DIR = "user/";
    private File file; //定义拍照后存放图片的文件位置和名称，使用完毕后可以方便删除

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

        avatarCiv = findViewById(R.id.civ_avatar);
        nicknameTv = findViewById(R.id.tv_nickname);
        genderTv = findViewById(R.id.tv_gender);
        locationTv = findViewById(R.id.tv_location);
        introductionTv = findViewById(R.id.tv_introduction);

        //获取头像
        if(!StringUtil.isEmpty(curUser.getAvatar())){ //头像资源路径不为空
            Glide.with(this).load("https://harmonystride-bucket." + curUser.getAvatar()).apply(requestOptions).into(avatarCiv);
        }
        nicknameTv.setText(curUser.getNickname());
        genderTv.setText(curUser.getGender());
        locationTv.setText(curUser.getLocation());
        introductionTv.setText(curUser.getIntroduction());
    }

    private void initEvents(){
        avatarCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ButtonDialog(ProfileEditActivity.this).show();
            }
        });

        nicknameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EditTextDialog(ProfileEditActivity.this, "nickname", new EditTextDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(Boolean update, String data) {
                        //更新数据
                        if(update){
                            nicknameTv.setText(data);
                            isUpdate = true;
                        }
                    }
                }).show();
            }
        });

        genderTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CheckBoxDialog(ProfileEditActivity.this, new CheckBoxDialog.OnDismissListener(){
                    @Override
                    public void onDismiss(Boolean update, String data) {
                        //更新数据
                        if(update){
                            genderTv.setText(data);
                            isUpdate = true;
                        }
                    }
                }).show();
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
                new EditTextDialog(ProfileEditActivity.this, "introduction", new EditTextDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(Boolean update, String data) {
                        //更新数据
                        if(update){
                            introductionTv.setText(data);
                            isUpdate = true;
                        }
                    }
                }).show();
            }
        });
    }

    //上传用户头像到OSS
    private void upload(Bitmap image){
        //删除线程
        Thread deleteImg = new Thread(new Runnable() {
            @Override
            public void run() {
                OSSClientUtil.getInstance().deleteImage(curUser.getAvatar());
            }
        });
        //上传线程
        Thread uploadImg = new Thread(new Runnable() {
            @Override
            public void run() {
                //上传
                String imageUrl = OSSClientUtil.getInstance().uploadImage(ImageUtil.bitmapToByteArray(image), USER_DIR + curUser.getAccount() + "/");

                if(imageUrl != null){
                    //更新本地数据库
                    User user = new User();
                    user.setAvatar(imageUrl);
                    user.updateAll("account = ?", curUser.getAccount());
                }
            }
        });

        if(!StringUtil.isEmpty(curUser.getAvatar())){
            deleteImg.start();
            try {
                // 等待第一个线程执行完毕
                deleteImg.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            uploadImg.start();
        }else {
            uploadImg.start();
        }

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

    //获取工具栏菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_close, menu);
        return true;
    }

    //为工具栏菜单绑定单击事件监听器
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.close:
                if(isUpdate){
                    update();
                }else {
                    finish();
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
                case OPEN_ALBUM_CODE:
                    //打开相册返回
                    Uri imageUri = Objects.requireNonNull(data).getData();
                    //图片剪裁
                    pictureCropping(imageUri);
                    break;
                case OPEN_CAMERA_CODE:
                    Bundle extras = data.getExtras();
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    pictureCropping(ImageUtil.getImageUri(this, bitmap));//开始裁减图片
                    break;
                case PICTURE_CROPPING_CODE:
                    //图片剪裁返回
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        //在这里获得了剪裁后的Bitmap对象，可以用于上传
                        Bitmap image = bundle.getParcelable("data");
                        //设置到ImageView上
                        Glide.with(this).load(image).apply(requestOptions).into(avatarCiv);
                        //上传头像
                        upload(image);
                        //修改更新标识
                        isUpdate = true;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 图片剪裁
     * @param uri 图片uri
     */
    private void pictureCropping(Uri uri) {
        // 调用系统中自带的图片剪裁
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        //支持缩放
        intent.putExtra("scale", true);
        // 返回裁剪后的数据
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PICTURE_CROPPING_CODE);
    }


}