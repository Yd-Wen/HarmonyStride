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
import com.srdp.harmonystride.entity.Application;
import com.srdp.harmonystride.entity.Post;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.ImageUtil;
import com.srdp.harmonystride.util.StringUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ApplyMyAdapter extends RecyclerView.Adapter<ApplyMyAdapter.ViewHolder>{
    private Context mContext;
    private List<Application> applicationList;
    private List<Post> postList;
    private List<User> userList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout applyMyPageLinearLayout;

        TextView applyReasonTv;
        TextView applyStatusTv;
        TextView postTitleTv;

        public ViewHolder(View view) {
            super(view);
            applyMyPageLinearLayout = view.findViewById(R.id.ll_apply_my_page);

            applyReasonTv = view.findViewById(R.id.tv_apply_reason);
            applyStatusTv = view.findViewById(R.id.tv_apply_status);
            postTitleTv = view.findViewById(R.id.tv_post_title);
        }
    }

    public ApplyMyAdapter(List<Application> applicationList, List<Post> postList, List<User> userList){
        this.applicationList = applicationList;
        this.postList = postList;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_apply_my_page, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        //点击事件监听器
        viewHolder.postTitleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:跳转到帖子详情页
                int position = viewHolder.getAdapterPosition();
                Post post = postList.get(position);
                User user = userList.get(position);
                Intent intent = new Intent(mContext, PostActivity.class);
                //TODO:传递数据进入帖子详情页
                intent.putExtra("user_id", user.getUid());
                intent.putExtra("user_avatar", user.getAvatar());
                intent.putExtra("user_nickname", user.getNickname());
                intent.putExtra("post_id", post.getPid());
                mContext.startActivity(intent);

            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //绑定数据
        Application application = applicationList.get(position);
        Post post = postList.get(position);

        holder.applyReasonTv.setText(application.getReason());
        switch (application.getStatus()){
            case "0":
                holder.applyStatusTv.setText("状态：申请待通过");
                break;
            case "1":
                holder.applyStatusTv.setText("状态：申请已通过");
                break;
            default:
                break;
        }
        holder.postTitleTv.setText(post.getTitle());

    }

    @Override
    public void onViewRecycled(@NonNull ApplyMyAdapter.ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }


}
