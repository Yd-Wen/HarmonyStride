package com.srdp.harmonystride.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.srdp.harmonystride.R;
import com.srdp.harmonystride.dialog.CheckBoxDialog;
import com.srdp.harmonystride.dialog.EditTextDialog;
import com.srdp.harmonystride.entity.User;

import org.litepal.LitePal;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileEditActivity extends BaseActivity {
    private Toolbar toolbar;

    private CircleImageView avatarCiv;
    private TextView nicknameTv;
    private TextView genderTv;
    private TextView locationTv;
    private TextView introductionTv;

    private User curUser;
    private boolean update = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        baseContext = this;

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
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        avatarCiv = findViewById(R.id.civ_avatar);
        nicknameTv = findViewById(R.id.tv_nickname);
        genderTv = findViewById(R.id.tv_gender);
        locationTv = findViewById(R.id.tv_location);
        introductionTv = findViewById(R.id.tv_introduction);
    }

    private void initEvents(){
        //TODO:修改头像
        avatarCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        nicknameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EditTextDialog(baseContext, "nickname", new EditTextDialog.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        //重新初始化和加载数据
                        initDatas();
                        loadUserInfo();
                        update = true;
                    }
                }).show();
            }
        });

        genderTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CheckBoxDialog(baseContext, new CheckBoxDialog.OnDismissListener(){
                    @Override
                    public void onDismiss() {
                        //重新初始化和加载数据
                        initDatas();
                        loadUserInfo();
                        update = true;
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
                new EditTextDialog(baseContext, "introduction", new EditTextDialog.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        //重新初始化和加载数据
                        initDatas();
                        loadUserInfo();
                        update = true;
                    }
                }).show();
            }
        });
    }

    private void loadUserInfo(){
        //TODO:从阿里云OSS读取并设置当前用户的头像
        setUserAvatar(avatarCiv);

        nicknameTv.setText(curUser.getNickname());
        genderTv.setText(curUser.getGender());
        locationTv.setText(curUser.getLocation());
        introductionTv.setText(curUser.getIntroduction());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_close, menu);
        return true;
    }

    //为工具栏菜单绑定单击事件监听器
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.close:
                Intent resultIntent = new Intent();
                resultIntent.putExtra("update", update);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
                break;
            default:
                break;
        }
        return true;
    }
}