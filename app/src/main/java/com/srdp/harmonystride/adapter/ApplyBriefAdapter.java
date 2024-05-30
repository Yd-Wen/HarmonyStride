package com.srdp.harmonystride.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.activity.ProfileActivity;
import com.srdp.harmonystride.dialog.BaseDialog;
import com.srdp.harmonystride.dialog.factory.DialogFactory;
import com.srdp.harmonystride.dialog.factory.TipDialogFactory;
import com.srdp.harmonystride.entity.Application;
import com.srdp.harmonystride.entity.Result;
import com.srdp.harmonystride.entity.User;
import com.srdp.harmonystride.util.HTTPUtil;
import com.srdp.harmonystride.util.ImageUtil;
import com.srdp.harmonystride.util.LogUtil;
import com.srdp.harmonystride.util.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ApplyBriefAdapter extends RecyclerView.Adapter<ApplyBriefAdapter.ViewHolder>{
    private static final int RECEIVE_SUCCESS = 1;
    private static final int RECEIVE_EXIST = 2;
    private Context mContext;
    private List<User> userList;
    private List<Application> applicationList;
    String pid;

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case RECEIVE_SUCCESS:
                    new ToastUtil(mContext).showToast("通过申请");
                    notifyDataSetChanged();
                    break;
                case RECEIVE_EXIST:
                    new ToastUtil(mContext).showToast("已接受其他用户申请");
                    break;
                default:
                    break;
            }
        }
    };


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

    public ApplyBriefAdapter(List<User> userList, List<Application> applicationList, String pid){
        this.userList = userList;
        this.applicationList = applicationList;
        this.pid = pid;
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
        viewHolder.moreIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通过不通过
                DialogFactory factory = new TipDialogFactory();
                List<String> data = new ArrayList<>();
                data.add("是否接受该用户申请");
                BaseDialog dialog = factory.createDialog(mContext, DialogFactory.DIALOG_TITLE_APPLY_AGREE, data, null, new BaseDialog.MyDialogListener() {
                    @Override
                    public void onClick(Boolean isConfirm, String data) {
                        if(isConfirm) {
                            int position = viewHolder.getAdapterPosition();
                            Map<String, Object> params = new HashMap<>();
                            params.put("pid",  String.valueOf(pid));
                            params.put("uid", String.valueOf( userList.get(position).getUid()));
                            HTTPUtil.POST(params, "/application/receive", new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    LogUtil.e("application receive", "error" + e);
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    LogUtil.d("application receive", "success");
                                    Gson gson = new Gson();
                                    String responseBody = response.body().string();
                                    Result result = gson.fromJson(responseBody, Result.class);
                                    //传递消息
                                    Message message = new Message();
                                    if(result.getCode() == 1){
                                        message.what = RECEIVE_SUCCESS;
                                        applicationList.get(position).setStatus("1");

                                    }else if(result.getCode() == 2){
                                        message.what = RECEIVE_EXIST;
                                    }
                                    handler.sendMessage(message);

                                }
                            });
                        }
                    }
                });
                dialog.show();
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
