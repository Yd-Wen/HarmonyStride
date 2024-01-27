package com.srdp.harmonystride.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.srdp.harmonystride.R;
import com.srdp.harmonystride.activity.PostActivity;
import com.srdp.harmonystride.entity.MyEntity;

import java.util.List;

public class MyEntityAdapter extends RecyclerView.Adapter<MyEntityAdapter.ViewHolder> {
    private Context mContext;
    private List<MyEntity> myEntityList;
    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imageView;
        TextView textView;
        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            imageView = (ImageView) view.findViewById(R.id.entity_image);
            textView = (TextView) view.findViewById(R.id.entity_name);
        }
    }
    public MyEntityAdapter(List<MyEntity> MyEntityList) {
        myEntityList = MyEntityList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_post, parent, false);

        final ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                MyEntity myEntity = myEntityList.get(position);

                Intent intent = new Intent(mContext, PostActivity.class);
                intent.putExtra(PostActivity.POST_NAME, myEntity.getName());
                intent.putExtra(PostActivity.POST_IMAGE, myEntity.getImageId());

                mContext.startActivity(intent);
            }
        });

        return viewHolder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyEntity myEntity = myEntityList.get(position);
        holder.textView.setText(myEntity.getName());
        Glide.with(mContext)
                .load(myEntity.getImageId())
                .error(R.drawable.load_error)
                .into(holder.imageView);
    }
    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        // 在 onViewRecycled 中取消加载请求
        Glide.with(mContext).clear(holder.imageView);
    }
    @Override
    public int getItemCount() {
        return myEntityList.size();
    }
}