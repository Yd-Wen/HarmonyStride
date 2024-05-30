package com.srdp.harmonystride.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.dialog.BaseDialog;
import com.srdp.harmonystride.dialog.factory.DialogFactory;
import com.srdp.harmonystride.dialog.factory.ProgressDialogFactory;
import com.srdp.harmonystride.dialog.factory.TipDialogFactory;
import com.srdp.harmonystride.entity.AppInfo;
import com.srdp.harmonystride.fragment.HomeFragment;
import com.srdp.harmonystride.fragment.MessageFragment;
import com.srdp.harmonystride.fragment.StatusFragment;
import com.srdp.harmonystride.util.ScrollUtil;
import com.srdp.harmonystride.util.DownloadUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private AppInfo appInfo;
    private HomeFragment homeFragment;
    private StatusFragment statusFragment;
    private MessageFragment messageFragment;
    private Fragment curFragment;

    private BottomNavigationView bottomNavigationView;

    private ScrollUtil scrollUtil; // 滑动工具
    private static final int SCROLL_THRESHOLD = 1000; // 设置滑动阈值

    private long mBackPressedTimestamp = 0;
    private static final int DOUBLE_BACK_INTERVAL = 2000; // 单位：毫秒

    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case DownloadUtil.DOWNLOAD_PREPARE:
                    appInfo = (AppInfo) msg.obj;
                    factory = new TipDialogFactory();
                    List<String> data = new ArrayList<>();
                    data.add("发现新版本，是否更新？\n\n" +
                            "当前版本：" + appInfo.getOldVersionName() + "\n" +
                            "最新版本：" + appInfo.getVersionName() + "\n" +
                            "软件大小：" + appInfo.getApkSize() + "\n\n" +
                            "更新日志：" + "\n" + appInfo.getUpdateLog() );
                    String title = "";
                    if(appInfo.isNoIgnorable()){
                        title = DialogFactory.DIALOG_TITLE_UPDATE_NEED_MUST;
                    }else title = DialogFactory.DIALOG_TITLE_UPDATE_NEED_OPTIONAL;
                    dialog = factory.createDialog(MainActivity.this, title, data, null, new BaseDialog.MyDialogListener() {
                        @Override
                        public void onClick(Boolean isConfirm, String data) {
                            if(isConfirm) {
                                Message message = new Message();
                                message.what = DownloadUtil.DOWNLOAD_START;
                                handler.sendMessage(message);
                            }
                        }
                    });
                    dialog.show();
                    break;
                case DownloadUtil.DOWNLOAD_START:
                    factory = new ProgressDialogFactory();
                    List<String> downloadingData = new ArrayList<>();
                    downloadingData.add("新版本下载中，请稍等");
                    dialog = factory.createDialog(MainActivity.this, DialogFactory.DIALOG_TITLE_UPDATE_DOING, downloadingData, null, new BaseDialog.MyDialogListener() {
                        @Override
                        public void onClick(Boolean isConfirm, String data) {
                            if(!isConfirm) {
                                DownloadUtil.IS_STOP = true;
                            }
                        }
                    });
                    dialog.show();
                    DownloadUtil.getInstance(MainActivity.this, handler).download(appInfo.getApkUrl());
                    break;
                case DownloadUtil.DOWNLOAD_DOING:
                    int progress = (int)msg.obj;
                    ((ProgressBar)dialog.getView().findViewById(R.id.progress_bar)).setProgress(progress);
                    break;
                case DownloadUtil.DOWNLOAD_SUCCESS:
                    showToast("下载成功，准备安装");
                    DownloadUtil.getInstance(MainActivity.this, handler).installAPK(MainActivity.this, DownloadUtil.apkFile);
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        //检查更新
        try {
            DownloadUtil.getInstance(this, handler).checkUpdate();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //记得在不需要的时候移除listener，如在activity的onDestroy()时
        //EMClient.getInstance().chatManager().removeMessageListener(MyApplication.messamgeListener);
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

    @Override
    public void onBackPressed() {
        long currentTimestamp = System.currentTimeMillis();
        if (currentTimestamp - mBackPressedTimestamp > DOUBLE_BACK_INTERVAL) { // 检查时间差是否大于预设阈值
            mBackPressedTimestamp = currentTimestamp; // 更新首次按下返回键的时间戳
            showToast("再按一次退出应用");// 提示用户
        } else {
            //super.onBackPressed(); // 跳转到上一个Activity或退出应用（取决于当前栈状态）
            System.exit(0); //退出应用
        }
    }
}