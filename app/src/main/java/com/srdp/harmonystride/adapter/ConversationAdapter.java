package com.srdp.harmonystride.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.activity.PostActivity;
import com.srdp.harmonystride.entity.Conversation;
import com.srdp.harmonystride.entity.MyEntity;
import com.srdp.harmonystride.util.ImageUtil;
import com.srdp.harmonystride.util.TimeUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder>{
    private Context mContext;
    private List<Conversation> conversationList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        CircleImageView avatarCiv;
        TextView nicknameTv;
        TextView messageTv;
        TextView timeTv;
        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            avatarCiv = (CircleImageView) view.findViewById(R.id.civ_avatar);
            nicknameTv = (TextView) view.findViewById(R.id.tv_nickname);
            messageTv = (TextView) view.findViewById(R.id.tv_message);
            timeTv = (TextView) view.findViewById(R.id.tv_time);
        }
    }

    public ConversationAdapter(List<Conversation> conversationList){
        this.conversationList = conversationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_conversation, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        //点击事件监听器
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Conversation conversation = conversationList.get(position);
                Intent intent = new Intent(mContext, PostActivity.class);
                intent.putExtra(PostActivity.POST_NAME, conversation.getAvatar());
                intent.putExtra(PostActivity.POST_IMAGE, conversation.getNickname());
                mContext.startActivity(intent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //绑定数据
        Conversation conversation = conversationList.get(position);
        holder.nicknameTv.setText(conversation.getNickname());
        holder.messageTv.setText(conversation.getMessage());
        holder.timeTv.setText(TimeUtil.formatLocalDateTime(conversation.getTime(), "yyyy-MM-dd hh:mm"));
        Glide.with(mContext)
                .load(ImageUtil.getImagePath(conversation.getAvatar()))
                .error(R.drawable.load_error)
                .into(holder.avatarCiv);
    }

    @Override
    public void onViewRecycled(@NonNull ConversationAdapter.ViewHolder holder) {
        super.onViewRecycled(holder);
        // 在 onViewRecycled 中取消加载请求
        Glide.with(mContext).clear(holder.avatarCiv);
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }


}
