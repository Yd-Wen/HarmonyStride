package com.srdp.harmonystride.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.sdk.android.oss.OSS;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.dialog.TipDialog;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.fragment.HomeFragment;
import com.srdp.harmonystride.fragment.MessageFragment;
import com.srdp.harmonystride.fragment.StatusFragment;
import com.srdp.harmonystride.util.HTTPUtil;
import com.srdp.harmonystride.util.ImageUtil;
import com.srdp.harmonystride.util.LogUtil;
import com.srdp.harmonystride.util.OSSClientUtil;
import com.srdp.harmonystride.util.ScrollUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;
import com.srdp.harmonystride.util.StringUtil;

import org.litepal.LitePal;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity {
    //Glide请求图片选项配置
    private RequestOptions requestOptions = RequestOptions
            .circleCropTransform()//圆形剪裁
            .diskCacheStrategy(DiskCacheStrategy.NONE);//不做磁盘缓存
    //.skipMemoryCache(true);//不做内存缓存

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private TextView titleTv;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private CircleImageView avatarCiv;
    private TextView nicknameTv;

    private User curUser; //当前用户

    private HomeFragment homeFragment;
    private StatusFragment statusFragment;
    private MessageFragment messageFragment;
    private Fragment curFragment;

    private FloatingActionButton floatingActionButton;
    private BottomNavigationView bottomNavigationView;

    private ScrollUtil scrollUtil; // 滑动工具
    private static final int SCROLL_THRESHOLD = 1000; // 设置滑动阈值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        // 创建一条文本消息，`content` 为消息文字内容。
//// `conversationId` 为消息接收方，单聊时为对端用户 ID、群聊时为群组 ID，聊天室时为聊天室 ID。
//        EMMessage message = EMMessage.createTextSendMessage("hello", "18379856278");
//// 会话类型：单聊为 EMMessage.ChatType.Chat，群聊为 EMMessage.ChatType.GroupChat, 聊天室为EMMessage.ChatType.ChatRoom，默认为单聊。
//        message.setChatType(EMMessage.ChatType.Chat);
//// 发送消息。
//        EMClient.getInstance().chatManager().sendMessage(message);

        //初始化视图
        initViews();
        //初始化事件
        initEvents();
    }

    public void initViews(){
        //获取drawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        //获取滑动导航栏
        navigationView = findViewById(R.id.navigation_view);
        //获取header内部控件
        avatarCiv = navigationView.getHeaderView(0).findViewById(R.id.civ_avatar);
        nicknameTv = navigationView.getHeaderView(0).findViewById(R.id.tv_nickname);

        //获取工具栏
        appBarLayout = findViewById(R.id.appbar_layout);
        toolbar = findViewById(R.id.tool_bar);
        titleTv = findViewById(R.id.tv_toolbar_title);
        setSupportActionBar(toolbar);
        //删除标题
        getSupportActionBar().setTitle(null);
        //设置新标题
        titleTv.setText(R.string.home);
        //为工具栏设置菜单溢出图标
        Drawable overflowIcon = ContextCompat.getDrawable(this, R.drawable.sort);
        toolbar.setOverflowIcon(overflowIcon);

        //获取碎片
        homeFragment = new HomeFragment();
        statusFragment = new StatusFragment();
        messageFragment = new MessageFragment();
        //当前默认碎片
        curFragment = homeFragment;
        //获取碎片管理器
        FragmentManager fragmentManager = getSupportFragmentManager();
        //开启事务
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //添加碎片
        fragmentTransaction.add(R.id.fragment_container, homeFragment);
        fragmentTransaction.commit();

        //获取悬浮按钮
        floatingActionButton = findViewById(R.id.floating_action_btn);

        //获取底部导航栏
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

    }

    private void initEvents(){
        //滑动菜单单击事件监听器
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.profile:
                        navigateTo(ProfileActivity.class);
                        break;
                    case R.id.certify:
                        showToast(getString(R.string.certify));
                        break;
                    case R.id.setting:
                        showToast(getString(R.string.setting));
                        break;
                    case R.id.logout:
                        drawerLayout.closeDrawers();
                        new TipDialog(MainActivity.this, "是否退出登录？", new TipDialog.OnDismissListener() {
                            @Override
                            public void onDismiss(Boolean isConfirm) {
                                if(isConfirm){
                                    navigateTo(LoginActivity.class);
                                    finish();
                                    //下次开屏页结束后进入登录页
                                    SharedPreferenceUtil.setParam("is_login", false);
                                    //登出IM
                                    EMClient.getInstance().logout(true);
                                }
                            }
                        }).show();
                        break;
                    default: break;
                }
                return true;
            }
        });

        //工具栏导航图标事件监听器
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //悬浮按钮单击事件监听器
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(getString(R.string.post));
            }
        });

        //底部导航栏单击事件监听器
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //显示toolbar
                appBarLayout.setExpanded(true, true);
                //替换碎片
                switch (item.getItemId()){
                    case R.id.home:
                        onHandleItemClick(homeFragment, R.string.home);
                        break;
                    case R.id.status:
                        onHandleItemClick(statusFragment, R.string.status);
                        break;
                    case R.id.message:
                        onHandleItemClick(messageFragment, R.string.message);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void loadUserInfo(){
        //通过账户名从本地数据库读取当前用户信息
        List<User> Users = LitePal.where("account = ?", SharedPreferenceUtil.getParam("current_account", "").toString()).find(User.class);
        curUser = Users.get(0);
        //显示当前用户信息
        if(!StringUtil.isEmpty(curUser.getAvatar())){ //头像资源路径不为空
            Glide.with(this).load(ImageUtil.getImagePath(curUser.getAvatar())).apply(requestOptions).into(avatarCiv);
        }
        nicknameTv.setText(curUser.getNickname());
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadUserInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //初始化滑动工具
        scrollUtil = new ScrollUtil(bottomNavigationView, homeFragment.getView().findViewById(R.id.recycler_view));
    }

    //获取工具栏菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_toolbar_main, menu);
        return true;
    }

    //为工具栏菜单绑定单击事件监听器
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.edit:
                //TODO：测试消息发送
                Intent intent = new Intent(MyApplication.getContext(), ChatActivity.class);
                intent.putExtra("account", "18911637274");
                intent.putExtra("avatar", "oss-cn-hangzhou.aliyuncs.com/img/user/18911637274/1708925719872.png");
                intent.putExtra("nickname", "yyz");
                navigateTo(intent);
                break;
            default:
                break;
        }
        return true;
    }


    private void onHandleItemClick(Fragment tgtFragment, int title){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //相同fragment
        if(curFragment != null && curFragment == tgtFragment){
            if(curFragment == homeFragment){
                //回到顶部
                if(scrollUtil.getScrollOffset() >= SCROLL_THRESHOLD) scrollUtil.scrollToTop();
                //刷新
                else homeFragment.refresh();
            }
        }else {
            //隐藏当前fragment
            if(curFragment != null){
                fragmentTransaction.hide(curFragment);
            }
            //显示目标fragment
            if(!tgtFragment.isAdded()){
                fragmentTransaction.add(R.id.fragment_container, tgtFragment);
            }else {
                fragmentTransaction.show(tgtFragment);
            }
            //设置工具栏和底部导航栏
            titleTv.setText(title);
            bottomNavigationView.getMenu().findItem(R.id.home).setIcon(R.drawable.home);
            bottomNavigationView.getMenu().findItem(R.id.home).setTitle(R.string.home);

            curFragment = tgtFragment;
        }
        fragmentTransaction.commit();
    }
}