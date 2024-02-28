package com.srdp.harmonystride.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.activity.PostActivity;
import com.srdp.harmonystride.entity.Chat;
import com.srdp.harmonystride.entity.Conversation;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.ImageUtil;
import com.srdp.harmonystride.util.SharedPreferenceUtil;
import com.srdp.harmonystride.util.TimeUtil;

import org.litepal.LitePal;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    private Context mContext;
    private List<Chat> chatList;
    private User user;

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView timeTv;
        CircleImageView avatarCiv;
        TextView messageTv;
        public ViewHolder(View view) {
            super(view);
            linearLayout = (LinearLayout) view;
            timeTv = (TextView) view.findViewById(R.id.tv_message_time);
            avatarCiv = (CircleImageView) view.findViewById(R.id.civ_avatar);
            messageTv = (TextView) view.findViewById(R.id.tv_message);
        }
    }

    public ChatAdapter(List<Chat> chatList){
        this.chatList = chatList;
        List<User> userList = LitePal.select("avatar").where("account = ?",SharedPreferenceUtil.getParam("current_account", "").toString()).find(User.class);
        user = userList.get(0);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_message_right, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        //点击事件监听器
        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Chat chat = chatList.get(position);
                //Intent intent = new Intent(mContext, PostActivity.class);
                //intent.putExtra(PostActivity.POST_NAME, chat.getAvatar());
                //intent.putExtra(PostActivity.POST_IMAGE, chat.getNickname());
                //mContext.startActivity(intent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //绑定数据
        Chat chat = chatList.get(position);
        holder.timeTv.setText(TimeUtil.formatLocalDateTime(chat.getTime(), "yyyy-MM-dd hh:mm"));
        holder.messageTv.setText(chat.getContent());
        Glide.with(mContext)
                .load(ImageUtil.getImagePath(user.getAvatar()))
                .error(R.drawable.load_error)
                .into(holder.avatarCiv);
    }

    @Override
    public void onViewRecycled(@NonNull ChatAdapter.ViewHolder holder) {
        super.onViewRecycled(holder);
        // 在 onViewRecycled 中取消加载请求
        Glide.with(mContext).clear(holder.avatarCiv);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


}
