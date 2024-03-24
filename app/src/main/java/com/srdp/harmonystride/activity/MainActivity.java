package com.srdp.harmonystride.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
//import com.hyphenate.chat.EMClient;
import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.dialog.TipDialog;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.fragment.HomeFragment;
import com.srdp.harmonystride.fragment.MessageFragment;
import com.srdp.harmonystride.fragment.StatusFragment;
import com.srdp.harmonystride.util.ImageUtil;
import com.srdp.harmonystride.util.ScrollUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;
import com.srdp.harmonystride.util.StringUtil;

import org.litepal.LitePal;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity {
    private HomeFragment homeFragment;
    private StatusFragment statusFragment;
    private MessageFragment messageFragment;
    private Fragment curFragment;

    private BottomNavigationView bottomNavigationView;

    private ScrollUtil scrollUtil; // 滑动工具
    private static final int SCROLL_THRESHOLD = 1000; // 设置滑动阈值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //注册消息监听
        //EMClient.getInstance().chatManager().addMessageListener(MyApplication.messamgeListener);

        //初始化视图
        initViews();
        //初始化事件
        initEvents();
    }


    public void initViews(){

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

        //获取底部导航栏
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

    }

    private void initEvents(){
        //底部导航栏单击事件监听器
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //显示toolbar
                //statusFragment.appBarLayout.setExpanded(true, true);
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

    @Override
    protected void onResume() {
        super.onResume();
        //初始化滑动工具
        scrollUtil = new ScrollUtil(bottomNavigationView, homeFragment.getView().findViewById(R.id.recycler_view));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //记得在不需要的时候移除listener，如在activity的onDestroy()时
        //EMClient.getInstance().chatManager().removeMessageListener(MyApplication.messamgeListener);
    }

    //获取工具栏菜单
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu){
//        getMenuInflater().inflate(R.menu.menu_toolbar_main, menu);
//        return true;
//    }

    //为工具栏菜单绑定单击事件监听器
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item){
//        switch (item.getItemId()){
//            case R.id.more:
//                //TODO：null
//                break;
//            default:
//                break;
//        }
//        return true;
//    }


    private void onHandleItemClick(Fragment tgtFragment, int title){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //相同fragment
        if(curFragment != null && curFragment == tgtFragment){
            if(curFragment == homeFragment){
                //回到顶部
                if(scrollUtil.getScrollOffset() >= SCROLL_THRESHOLD) scrollUtil.scrollToTop();
                //刷新
                //else homeFragment.refresh();
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
            bottomNavigationView.getMenu().findItem(R.id.home).setIcon(R.drawable.home);
            bottomNavigationView.getMenu().findItem(R.id.home).setTitle(R.string.home);

            curFragment = tgtFragment;
        }
        fragmentTransaction.commit();
    }
}