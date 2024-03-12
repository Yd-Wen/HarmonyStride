package com.srdp.harmonystride.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.activity.PostActivity;
import com.srdp.harmonystride.entity.Post;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.ImageUtil;
import com.srdp.harmonystride.util.StringUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostBriefAdapter extends RecyclerView.Adapter<PostBriefAdapter.ViewHolder>{
    private Context mContext;
    private List<Post> postList;
    private List<User> userList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout postBriefPageLinearLayout;

        CircleImageView userAvatarCiv;
        TextView userNicknameTv;
        TextView postDatetimeTv;
        ImageView moreIv;

        TextView postTitleTv;
        TextView postContentTv;

        ImageView postImageIv;

        TextView postLabelTv;
        ImageView postCommentIv;


        public ViewHolder(View view) {
            super(view);
            postBriefPageLinearLayout = view.findViewById(R.id.ll_post_brief_page);

            userAvatarCiv = view.findViewById(R.id.civ_avatar);
            userNicknameTv = view.findViewById(R.id.tv_user_nickname);
            postDatetimeTv = view.findViewById(R.id.tv_post_datatime);
            moreIv = view.findViewById(R.id.iv_more);

            postTitleTv = view.findViewById(R.id.tv_post_title);
            postContentTv = view.findViewById(R.id.tv_post_content);

            postImageIv = view.findViewById(R.id.iv_post_image);

            postLabelTv = view.findViewById(R.id.tv_post_label);
            postCommentIv = view.findViewById(R.id.iv_post_comment);
        }
    }

    public PostBriefAdapter(List<Post> postList, List<User> userList){
        this.postList = postList;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_post_brief_page, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        //点击事件监听器
        viewHolder.postBriefPageLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Post post = postList.get(position);
                User user = userList.get(position);
                Intent intent = new Intent(mContext, PostActivity.class);
                //TODO:传递数据进入帖子详情页
                intent.putExtra(PostActivity.POST_NAME, post.getTitle());
                intent.putExtra(PostActivity.POST_IMAGE, 1);
                mContext.startActivity(intent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //绑定数据
        Post post = postList.get(position);
        User user = userList.get(position);

        Glide.with(mContext)
                .load(ImageUtil.getImagePath(user.getAvatar()))
                .error(R.drawable.load_error)
                .into(holder.userAvatarCiv);
        holder.userNicknameTv.setText(user.getNickname());
        holder.postDatetimeTv.setText(post.getDatetime());

        holder.postTitleTv.setText(post.getTitle());
        holder.postContentTv.setText(StringUtil.removeHtmlTags(post.getContent()));

        if(post.getImages() == null){
            holder.postImageIv.setVisibility(View.GONE);
        }else {
            Glide.with(mContext)
                    .load(ImageUtil.getImagePath(post.getImages()))
                    .error(R.drawable.load_error)
                    .into(holder.postImageIv);
        }

        holder.postLabelTv.setText(post.getLabel());
    }

    @Override
    public void onViewRecycled(@NonNull PostBriefAdapter.ViewHolder holder) {
        super.onViewRecycled(holder);
        // 在 onViewRecycled 中取消加载请求
        Glide.with(mContext).clear(holder.userAvatarCiv);
        Glide.with(mContext).clear(holder.postImageIv);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


}
