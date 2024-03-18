package com.srdp.harmonystride.activity;

import static com.srdp.harmonystride.util.ImageUtil.OPEN_ALBUM_CODE;
import static com.srdp.harmonystride.util.ImageUtil.getImagePath;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.entity.Post;
import com.srdp.harmonystride.entity.Result;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.HTTPUtil;
import com.srdp.harmonystride.util.ImageUtil;
import com.srdp.harmonystride.util.LogUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;
import com.srdp.harmonystride.util.StringUtil;
import com.srdp.harmonystride.util.TimeUtil;


import org.litepal.LitePal;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jp.wasabeef.richeditor.RichEditor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PostingActivity extends BaseActivity {
    private static final int IMAGE_UPLOAD_SUCCESS = 1; //图片上传成功
    private static final int POST_ADD_SUCCESS = 2; //帖子上传成功

    private Toolbar toolbar;
    private Button postBtn;
    private EditText titleEt;

    private RichEditor contentRe;

    private ImageView undoIv;
    private ImageView redoIv;
    private ImageView boldIv;
    private ImageView italicIv;
    private ImageView insertImgIv;

    private RadioGroup labelRg;
    private RadioButton labelVolunteerRb;
    private RadioButton labelJobRb;
    private RadioButton labelHelpRb;
    private RadioButton labelRecruitmentRb;

    private Post post;

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case IMAGE_UPLOAD_SUCCESS:
                    String url = msg.obj.toString();
                    if((Boolean) SharedPreferenceUtil.getParam("post_image_watermark", false)){
                        //添加水印
                        url = ImageUtil.addWaterMark(msg.obj.toString());
                    }
                    post.setImages(url);
                    contentRe.insertImage(ImageUtil.getImagePath(url), "");
                    break;
                case POST_ADD_SUCCESS:
                    showToast("帖子发布成功");
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
        setContentView(R.layout.activity_posting);
        //初始化视图
        initViews();
        //初始化事件
        initEvents();
        //初始化数据
        initDatas();
    }

    private void initViews(){
        toolbar = findViewById(R.id.tool_bar);
        //不要忘记设置工具栏
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        postBtn = findViewById(R.id.btn_post);
        titleEt = findViewById(R.id.et_title);

        contentRe = findViewById(R.id.re_content);
        contentRe.setEditorBackgroundColor(Color.WHITE); //白色背景
        contentRe.setEditorFontColor(R.color.colorText); //设置字体颜色
        contentRe.setFontSize(R.dimen.textSize); //设置字体大小
        contentRe.setPlaceholder(getResources().getString(R.string.post_content)); //设置文本

        undoIv = findViewById(R.id.iv_action_undo);
        redoIv = findViewById(R.id.iv_action_redo);
        boldIv = findViewById(R.id.iv_action_bold);
        italicIv = findViewById(R.id.iv_action_italic);
        insertImgIv = findViewById(R.id.iv_action_insert_image);

        labelRg = findViewById(R.id.rg_label);
        labelVolunteerRb = findViewById(R.id.rb_label_v);
        labelJobRb = findViewById(R.id.rb_label_j);
        labelHelpRb = findViewById(R.id.rb_label_h);
        labelRecruitmentRb = findViewById(R.id.rb_label_r);
    }

    private void initEvents(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:发布帖子
                post.setTitle(titleEt.getText().toString());
                post.setContent(contentRe.getHtml());
                post.setDatetime(TimeUtil.formatLocalDateTime(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"));
                LogUtil.d("post", post.toString());

                String json = new Gson().toJson(post);
                Map<String, Object> map = new HashMap<>();
                map.put("post", json);

                HTTPUtil.POST(map, "/post/add", new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogUtil.e("post add", "error" + e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        LogUtil.d("post add", "success");

                        Gson gson = new Gson();
                        Result result = gson.fromJson(response.body().string(), Result.class);
                        if(result.getCode() == 1){
                            Message message = new Message();
                            message.what = POST_ADD_SUCCESS;
                            handler.sendMessage(message);
                        }

                    }
                });

            }
        });

        titleEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!StringUtil.isEmpty(contentRe.getHtml()) && !StringUtil.isEmpty(titleEt.getText().toString())){
                    //文本不为空
                    postBtn.setEnabled(true);
                    postBtn.setTextColor(getResources().getColor(R.color.colorWhite, null));
                }else {
                    //文本为空
                    postBtn.setEnabled(false);
                    postBtn.setTextColor(getResources().getColor(R.color.colorText, null));
                }
            }
        });

        contentRe.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                if(!StringUtil.isEmpty(text) && !StringUtil.isEmpty(titleEt.getText().toString())){
                    //文本不为空
                    postBtn.setEnabled(true);
                    postBtn.setTextColor(getResources().getColor(R.color.colorWhite, null));
                }else {
                    //文本为空
                    postBtn.setEnabled(false);
                    postBtn.setTextColor(getResources().getColor(R.color.colorText, null));
                }
            }
        });

        undoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contentRe.undo();
            }
        });

        redoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contentRe.redo();
            }
        });

        boldIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contentRe.setBold();
            }
        });

        italicIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contentRe.setItalic();
            }
        });

        insertImgIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, OPEN_ALBUM_CODE);
            }
        });

        labelRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                RadioButton radioButton = findViewById(id);
                if(radioButton != null){
                    post.setLabel(radioButton.getText().toString());
                }
            }
        });

    }

    private void initDatas(){
        //初始化帖子
        post = new Post();
        post.setOwner((int) SharedPreferenceUtil.getParam("current_uid", 0));
        post.setLabel("志愿");
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
                    ImageUtil.pictureCropping(this, imageUri, 3, 2);
                    break;
                case ImageUtil.PICTURE_CROPPING_CODE:
                    //图片剪裁返回
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        //在这里获得了剪裁后的Bitmap对象，可以用于上传
                        Bitmap image = bundle.getParcelable("data");
                        //上传头像
                        String imageUrl = ImageUtil.POST_PATH + System.currentTimeMillis() + ".png";
                        ImageUtil.upload("", image, imageUrl, new ImageUtil.OnUploadImage() {
                            @Override
                            public void onUpload(Boolean isUpload) {
                                if(isUpload){
                                    Message message = new Message();
                                    message.what = IMAGE_UPLOAD_SUCCESS;
                                    message.obj = imageUrl;
                                    handler.sendMessage(message);
                                }
                            }
                        });
                    }
                    break;
                default:
                    break;
            }
        }
    }

}