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
import com.srdp.harmonystride.util.LogUtil;
import com.srdp.harmonystride.util.StringUtil;

import java.util.List;

public class PostVisitAdapter extends RecyclerView.Adapter<PostVisitAdapter.ViewHolder>{
    private Context mContext;
    private List<Post> postList;
    private User user;

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout postVisitPageLinearLayout;

        TextView postDatetimeTv;
        ImageView moreIv;

        TextView postTitleTv;
        TextView postContentTv;

        ImageView postImageIv;

        TextView postLabelTv;
        ImageView postCommentIv;


        public ViewHolder(View view) {
            super(view);
            postVisitPageLinearLayout = view.findViewById(R.id.ll_post_visit_page);

            postDatetimeTv = view.findViewById(R.id.tv_post_datatime);
            moreIv = view.findViewById(R.id.iv_more);

            postTitleTv = view.findViewById(R.id.tv_post_title);
            postContentTv = view.findViewById(R.id.tv_post_content);

            postImageIv = view.findViewById(R.id.iv_post_image);

            postLabelTv = view.findViewById(R.id.tv_post_label);
            postCommentIv = view.findViewById(R.id.iv_post_comment);
        }
    }

    public PostVisitAdapter(List<Post> postList, User user){
        this.postList = postList;
        this.user = user;
        LogUtil.e("user", user.toString());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_post_visit_page, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        //点击事件监听器
        viewHolder.postVisitPageLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Post post = postList.get(position);
                Intent intent = new Intent(mContext, PostActivity.class);
                //TODO:传递数据进入帖子详情页

                intent.putExtra("user_id", user.getUid());
                intent.putExtra("user_avatar", user.getAvatar());
                intent.putExtra("user_nickname", user.getNickname());
                intent.putExtra("post", post);
                mContext.startActivity(intent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //绑定数据
        Post post = postList.get(position);

        holder.postDatetimeTv.setText(post.getDatetime());

        holder.postTitleTv.setText(post.getTitle());
        holder.postContentTv.setText(post.getContentWithoutHtmlTags());

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
    public void onViewRecycled(@NonNull PostVisitAdapter.ViewHolder holder) {
        super.onViewRecycled(holder);
        // 在 onViewRecycled 中取消加载请求
        Glide.with(mContext).clear(holder.postImageIv);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


}
