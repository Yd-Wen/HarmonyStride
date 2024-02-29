package com.srdp.harmonystride.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.modules.conversation.EaseConversationListFragment;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.srdp.harmonystride.MyApplication;
import com.srdp.harmonystride.activity.ChatActivity;

public class MessageFragment extends EaseConversationListFragment {
    private IMmsgBroadcast iMmsgBroadcast;
    public static final String BROADCAST_ACTION_DISC = "com.srdp.harmonystride.fragment.IMmsgBroadcast";
    //    private RecyclerView recyclerView;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view;
//        view = inflater.inflate(R.layout.fragment_status, container, false);
//
//        //获取布局
//        recyclerView = view.findViewById(R.id.recycler_view);
//
//        // Inflate the layout for this fragment
//        return view;
//    }
//
    @Override
    public void onResume() {
        super.onResume();
        //recyclerView.scrollToPosition(0);
        //动态注册广播
        // 1. 实例化BroadcastReceiver子类 &  IntentFilter
        iMmsgBroadcast = new IMmsgBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        // 2. 设置接收广播的类型
        intentFilter.addAction(BROADCAST_ACTION_DISC);// 只有持有相同的action的接受者才能接收此广播
        // 3. 动态注册：调用Context的registerReceiver（）方法
        MyApplication.getContext().registerReceiver(iMmsgBroadcast, intentFilter);
    }


    @Override
    public void onPause() {
        super.onPause();
        //销毁在onResume()方法中的广播
        MyApplication.getContext().unregisterReceiver(iMmsgBroadcast);
    }

    //广播接收者
    public class IMmsgBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //收到广播后的操作
            initData();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        super.onItemClick(view, position);
        Object item = conversationListLayout.getItem(position).getInfo();
        if(item instanceof EMConversation){
            Intent intent = new Intent(MyApplication.getContext(), ChatActivity.class);
            intent.putExtra(EaseConstant.EXTRA_CONVERSATION_ID, ((EMConversation)item).conversationId());
            intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseCommonUtils.getChatType((EMConversation) item));
            startActivity(intent);
        }
    }


}