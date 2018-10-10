package com.zzh12138.jzvideodemo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zzh12138.jzvideodemo.R;
import com.zzh12138.jzvideodemo.bean.CommentBean;

import java.util.List;

/**
 * Created by zhangzhihao on 2018/9/21 10:13.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {
    private Context mContext;
    private List<CommentBean> mList;

    public CommentAdapter(Context mContext, List<CommentBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        CommentBean bean = mList.get(position);
        holder.name.setText(bean.getUserName());
        holder.content.setText(bean.getContent());
        holder.praise.setText(String.valueOf(bean.getPraiseNum()));
        RequestOptions options = new RequestOptions().optionalCircleCrop();
        Glide.with(mContext).load(R.mipmap.ic_launcher_round).apply(options).into(holder.avatar);
        if (bean.getParentComment() != null) {
            holder.parentContent.setVisibility(View.VISIBLE);
            StringBuilder builder = new StringBuilder();
            builder.append("@")
                    .append(bean.getParentComment().getUserName())
                    .append(":  ")
                    .append(bean.getParentComment().getContent());
            holder.parentContent.setText(builder.toString());
        } else {
            holder.parentContent.setVisibility(View.GONE);
        }
        holder.time.setText("2019-2-30");
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class CommentHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView content;
        TextView parentContent;
        TextView time;
        TextView praise;
        ImageView avatar;

        public CommentHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.adapter_comment_avatar);
            name = itemView.findViewById(R.id.adapter_comment_user_name);
            praise = itemView.findViewById(R.id.adapter_comment_praise);
            parentContent = itemView.findViewById(R.id.adapter_comment_parent_content);
            content = itemView.findViewById(R.id.adapter_comment_content);
            time = itemView.findViewById(R.id.adapter_comment_time);
        }
    }
}
