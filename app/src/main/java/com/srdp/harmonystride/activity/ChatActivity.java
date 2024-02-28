package com.srdp.harmonystride.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.adapter.ChatAdapter;
import com.srdp.harmonystride.adapter.ConversationAdapter;
import com.srdp.harmonystride.entity.Chat;
import com.srdp.harmonystride.entity.Conversation;
import com.srdp.harmonystride.util.ImageUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;
import com.srdp.harmonystride.util.StringUtil;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends BaseActivity {
    private static final int MSG_SEND = 1; //发送消息
    private static final int MSG_RECEIVE = 2; //接收消息
    //Glide请求图片选项配置
    private RequestOptions requestOptions = RequestOptions
            .circleCropTransform()//圆形剪裁
            .diskCacheStrategy(DiskCacheStrategy.NONE);//不做磁盘缓存
    //.skipMemoryCache(true);//不做内存缓存

    private Toolbar toolbar; //工具栏
    private CircleImageView avatarToolbarCiv; //工具栏头像
    private TextView nicknameToolbarTv; //工具栏昵称

    private RecyclerView recyclerView;

    private CircleImageView leftCiv;
    private CircleImageView rightCiv;
    private TextView leftTv;
    private TextView rightTv;

    private EditText messageEt; //消息编辑
    private ImageButton moreTypeMsgIb; //更多消息类型
    private ImageButton sendMsgIb; //发送消息

    private LinearLayout moreTypeMsgLinearLayout; //更多消息类型的布局
    private ImageButton albumIb; //相册
    private ImageButton cameraIb; //相机
    private ImageButton positionIb; //定位

    private String chatAccount; //当前聊天对象的账号

    private List<Chat> chatList = new ArrayList<>();
    private List<EMMessage> messageList;
    private ChatAdapter chatAdapter;

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_SEND:
                    chatAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //初始化视图
        initViews();
        //初始化行为
        initActions();
    }

    private void initViews(){
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        //去除标题
        getSupportActionBar().setTitle(null);

        avatarToolbarCiv = findViewById(R.id.civ_toolbar_avatar);
        nicknameToolbarTv = findViewById(R.id.tv_toolbar_nickname);

        recyclerView = findViewById(R.id.recycler_view_content);

        messageEt = findViewById(R.id.et_message);
        moreTypeMsgIb = findViewById(R.id.ib_more_type_message);
        sendMsgIb = findViewById(R.id.ib_send_message);
        //初始化时隐藏发送按钮
        sendMsgIb.setVisibility(View.GONE);

        moreTypeMsgLinearLayout = findViewById(R.id.ll_more_type_message);
        //初始化时隐藏更多消息类型
        moreTypeMsgLinearLayout.setVisibility(View.GONE);
        albumIb = findViewById(R.id.ib_album);
        cameraIb = findViewById(R.id.ib_camera);
        positionIb = findViewById(R.id.ib_position);
    }

    private void initActions(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        avatarToolbarCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO：获取用户信息，查看用户主页
            }
        });

        messageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(StringUtil.isEmpty(charSequence.toString())){
                    //输入为空
                    sendMsgIb.setVisibility(View.GONE);
                    moreTypeMsgIb.setVisibility(View.VISIBLE);
                }else {
                    sendMsgIb.setVisibility(View.VISIBLE);
                    moreTypeMsgIb.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        moreTypeMsgIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //隐藏或显示更多消息类型
                if(moreTypeMsgLinearLayout.getVisibility() == View.VISIBLE){
                    moreTypeMsgLinearLayout.setVisibility(View.GONE);
                }else {
                    moreTypeMsgLinearLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        sendMsgIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:发送消息
                // 创建一条文本消息，`content` 为消息文字内容。
                // `conversationId` 为消息接收方，单聊时为对端用户 ID、群聊时为群组 ID，聊天室时为聊天室 ID。
                Chat chat = new Chat(String.valueOf(SharedPreferenceUtil.getParam("current_account", "")), chatAccount, messageEt.getText().toString(), LocalDateTime.now());

                chatList.add(chat);

                EMMessage emMessage = EMMessage.createTextSendMessage(messageEt.getText().toString(), chatAccount);
                // 会话类型：单聊为 EMMessage.ChatType.Chat，群聊为 EMMessage.ChatType.GroupChat, 聊天室为EMMessage.ChatType.ChatRoom，默认为单聊。
                emMessage.setChatType(EMMessage.ChatType.Chat);
                // 发送消息时可以设置 `EMCallBack` 的实例，获得消息发送的状态。可以在该回调中更新消息的显示状态。例如消息发送失败后的提示等等。
                emMessage.setMessageStatusCallback(new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        // 发送消息成功
                        Message message = new Message();
                        message.what = MSG_SEND;
                        handler.sendMessage(message);
                    }
                    @Override
                    public void onError(int code, String error) {
                        // 发送消息失败
                    }
                    @Override
                    public void onProgress(int progress, String status) {
                        //消息发送中
                    }

                });
                // 发送消息。
                EMClient.getInstance().chatManager().sendMessage(emMessage);

                //清空输入
                messageEt.setText("");
            }
        });

        albumIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:打开相册
            }
        });

        cameraIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO：打开相机
            }
        });

        positionIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO：获取定位
            }
        });

        //设置布局管理器
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MyApplication.getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        //设置适配器
        chatAdapter = new ChatAdapter(chatList);
        recyclerView.setAdapter(chatAdapter);

    }

    private void loadUserInfo(){
        Intent intent = getIntent();

        chatAccount = intent.getStringExtra("account");
        String avatar = intent.getStringExtra("avatar");
        String nickname = intent.getStringExtra("nickname");

        Glide.with(this).load(ImageUtil.getImagePath(avatar)).apply(requestOptions).into(avatarToolbarCiv);
        nicknameToolbarTv.setText(nickname);

        //TODO:加载历史消息
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
                //TODO:更多选项，查看主页，屏蔽消息，举报等
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadUserInfo();
    }

}