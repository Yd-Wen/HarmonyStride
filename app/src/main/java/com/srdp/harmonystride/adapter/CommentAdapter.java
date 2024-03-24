package com.srdp.harmonystride.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.activity.ProfileActivity;
import com.srdp.harmonystride.entity.Comment;
import com.srdp.harmonystride.entity.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{
    private Context mContext;
    private List<User> userList;
    private List<Comment> commentList;
    String pid;


    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userAvatarCiv;
        TextView userNicknameTv;
        ImageView moreIv;

        TextView commentDatetimeTv;
        TextView commentContentTv;

        public ViewHolder(View view) {
            super(view);

            userAvatarCiv = view.findViewById(R.id.civ_avatar);
            userNicknameTv = view.findViewById(R.id.tv_user_nickname);
            moreIv = view.findViewById(R.id.iv_more);

            commentDatetimeTv = view.findViewById(R.id.tv_comment_datetime);
            commentContentTv = view.findViewById(R.id.tv_comment_content);
        }
    }

    public CommentAdapter(List<User> userList, List<Comment> commentList){
        this.userList = userList;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_comment, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        //点击事件监听器
        viewHolder.userAvatarCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Intent profileIntent = new Intent(mContext, ProfileActivity.class);
                profileIntent.putExtra("is_self", false);
                profileIntent.putExtra("user_id", userList.get(position).getUid());
                mContext.startActivity(profileIntent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //绑定数据
        User user = userList.get(position);
        Comment comment = commentList.get(position);

        Glide.with(mContext)
                .load(user.getAvatarUrl())
                .error(R.drawable.load_error)
                .into(holder.userAvatarCiv);
        holder.userNicknameTv.setText(user.getNickname());

        holder.commentDatetimeTv.setText(comment.getDatetime());
        holder.commentContentTv.setText(comment.getContent());

    }

    @Override
    public void onViewRecycled(@NonNull CommentAdapter.ViewHolder holder) {
        super.onViewRecycled(holder);
        // 在 onViewRecycled 中取消加载请求
        Glide.with(mContext).clear(holder.userAvatarCiv);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }


}
