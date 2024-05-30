package com.srdp.harmonystride.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.srdp.harmonystride.activity.CertificationActivity;
import com.srdp.harmonystride.activity.PostingActivity;
import com.srdp.harmonystride.activity.ProfileActivity;
import com.srdp.harmonystride.entity.Certification;
import com.srdp.harmonystride.util.ToastUtil;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

/**
 * 自定义布局，下面是常见的图片样式，更多实现可以看demo，可以自己随意发挥
 */
public class MyBannerAdapter extends BannerAdapter<Integer, MyBannerAdapter.BannerViewHolder> {
    private Context context;

    public MyBannerAdapter(Context context, List<Integer> mDatas) {
        //设置数据，也可以调用banner提供的方法,或者自己在adapter中实现
        super(mDatas);
        this.context = context;
    }

    //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        if (context == null) {
            context = parent.getContext();
        }
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new BannerViewHolder(imageView);
    }

    @Override
    public void onBindView(BannerViewHolder holder, Integer data, int position, int size) {
        holder.imageView.setImageResource(data);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                //点击事件
                switch (position){
                    case 0:
                        intent = new Intent(context, CertificationActivity.class);
                        break;
                    case 1:
                        intent = new Intent(context, PostingActivity.class);
                        break;
                    case 2:
                        //intent = new Intent(context, ProfileActivity.class);
                        break;
                    default:
                        break;
                }
                if(intent != null) context.startActivity(intent);
            }
        });
    }


    class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public BannerViewHolder(@NonNull ImageView view) {
            super(view);
            this.imageView = view;
        }
    }
}
