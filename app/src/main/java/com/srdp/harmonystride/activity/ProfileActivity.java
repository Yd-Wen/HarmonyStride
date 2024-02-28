package com.srdp.harmonystride.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.ImageUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;
import com.srdp.harmonystride.util.StringUtil;

import org.litepal.LitePal;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends BaseActivity {
    //Glide请求图片选项配置
    private RequestOptions requestOptions = RequestOptions
            .circleCropTransform()//圆形剪裁
            .diskCacheStrategy(DiskCacheStrategy.NONE);//不做磁盘缓存
    //.skipMemoryCache(true);//不做内存缓存

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

    private TextView contentTv;
    private FloatingActionButton floatingActionButton;

    private User curUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //初始化视图
        initViews();
        //初始化事件
        initEvents();
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

        contentTv = findViewById(R.id.tv_content);
        contentTv.setText(getString(R.string.content));

        floatingActionButton = findViewById(R.id.floating_action_btn);

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

    }

    private void loadUserInfo(){
        //读取当前用户
        List<User> users = LitePal.where("account = ?", SharedPreferenceUtil.getParam("current_account", "").toString()).find(User.class);
        curUser = users.get(0);
        //下载头像
        if(!StringUtil.isEmpty(curUser.getAvatar())){
            Glide.with(this).load(ImageUtil.getImagePath(curUser.getAvatar())).apply(requestOptions).into(avatarCiv);
            Glide.with(this).load(ImageUtil.getImagePath(curUser.getAvatar())).apply(requestOptions).into(avatarToolbarCiv);
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

    @Override
    protected void onStart() {
        super.onStart();
        loadUserInfo();
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