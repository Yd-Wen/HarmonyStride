package com.srdp.harmonystride.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.SharedPreferenceUtil;
import com.srdp.harmonystride.util.StringUtil;

import org.litepal.LitePal;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends BaseActivity {
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView backgroundIv;
    private Toolbar toolbar;
    private CircleImageView avatarToolbarCiv;
    private TextView nicknameToolbarCiv;

    private CircleImageView avatarCiv;
    private TextView nicknameTv;
    private TextView introductionTv;
    private TextView certifyTv;
    private TextView genderTv;
    private TextView locationTv;

    private TextView contentTv;
    private FloatingActionButton floatingActionButton;

    private User curUser;
    private boolean update = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setActivityResultLauncher(
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // 处理返回的数据
                        Intent data = result.getData();
                        // 进行你的操作
                        if(data.getBooleanExtra("update", false)){
                            //如果用户更新数据，重新初始化
                            update = true;
                            initDatas();
                            loadUserInfo();
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
        //读取当前用户
        Intent intent = getIntent();
        List<User> users = LitePal.where("account = ?", intent.getStringExtra("account")).find(User.class);
        curUser = users.get(0);
    }

    private void initViews(){
        appBarLayout = findViewById(R.id.appbar_layout);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);

        backgroundIv = findViewById(R.id.iv_background);
        toolbar = findViewById(R.id.tool_bar);
        //不要忘记设置工具栏
        setSupportActionBar(toolbar);

        avatarToolbarCiv = findViewById(R.id.civ_toolbar_avatar);
        nicknameToolbarCiv = findViewById(R.id.tv_toolbar_nickname);

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
                    nicknameToolbarCiv.setVisibility(View.VISIBLE);
                }else {
                    //展开状态
                    avatarToolbarCiv.setVisibility(View.GONE);
                    nicknameToolbarCiv.setVisibility(View.GONE);
                }
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resutltIntent = new Intent();
                resutltIntent.putExtra("update", update);
                setResult(Activity.RESULT_OK, resutltIntent);
                finish();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(baseContext, ProfileEditActivity.class);
                intent.putExtra("account", curUser.getAccount());
                navigateForResult(intent);
            }
        });

    }

    private void loadUserInfo(){
        //显示当前用户信息
        if(!StringUtil.isEmpty(curUser.getImageUrl())){ //头像资源路径不为空
            //TODO:从阿里云OSS读取并设置当前用户的头像
            setUserAvatar(avatarCiv);
            setUserAvatar(avatarToolbarCiv);
        }
        //显示昵称
        nicknameTv.setText(curUser.getNickname());
        nicknameToolbarCiv.setText(curUser.getNickname());
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

    //TODO:从阿里云OSS读取并设置当前用户的头像
    private void setUserAvatar(CircleImageView circleImageView){

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