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
import com.srdp.harmonystride.dialog.TipDialog;
import com.srdp.harmonystride.entity.Application;
import com.srdp.harmonystride.entity.Post;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.ImageUtil;
import com.srdp.harmonystride.util.StringUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ApplyBriefAdapter extends RecyclerView.Adapter<ApplyBriefAdapter.ViewHolder>{
    private Context mContext;
    private List<User> userList;
    private List<Application> applicationList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout applyBriefPageLinearLayout;

        CircleImageView userAvatarCiv;
        TextView userNicknameTv;
        ImageView moreIv;

        TextView applyReasonTv;
        TextView applyStatusTv;

        public ViewHolder(View view) {
            super(view);
            applyBriefPageLinearLayout = view.findViewById(R.id.ll_apply_brief_page);

            userAvatarCiv = view.findViewById(R.id.civ_avatar);
            userNicknameTv = view.findViewById(R.id.tv_user_nickname);
            moreIv = view.findViewById(R.id.iv_more);

            applyReasonTv = view.findViewById(R.id.tv_apply_reason);
            applyStatusTv = view.findViewById(R.id.tv_apply_status);
        }
    }

    public ApplyBriefAdapter(List<User> userList, List<Application> applicationList){
        this.userList = userList;
        this.applicationList = applicationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_apply_brief_page, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        //点击事件监听器
        viewHolder.moreIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:通过不通过
                new TipDialog(mContext, "是否接受该用户申请", "拒绝", "接受", new TipDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(Boolean isConfirm) {
                        if(isConfirm){

                        }
                    }
                }).show();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //绑定数据
        User user = userList.get(position);
        Application application = applicationList.get(position);

        Glide.with(mContext)
                .load(ImageUtil.getImagePath(user.getAvatar()))
                .error(R.drawable.load_error)
                .into(holder.userAvatarCiv);
        holder.userNicknameTv.setText(user.getNickname());

        holder.applyReasonTv.setText(application.getReason());
        switch (application.getStatus()){
            case "0":
                holder.applyStatusTv.setText("待接受");
                break;
            case "1":
                holder.applyStatusTv.setText("拒绝");
                break;
            case "2":
                holder.applyStatusTv.setText("接受");
                break;
            default:
                break;
        }

    }

    @Override
    public void onViewRecycled(@NonNull ApplyBriefAdapter.ViewHolder holder) {
        super.onViewRecycled(holder);
        // 在 onViewRecycled 中取消加载请求
        Glide.with(mContext).clear(holder.userAvatarCiv);
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }


}
