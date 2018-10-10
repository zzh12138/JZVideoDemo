package com.zzh12138.jzvideodemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zzh12138.jzvideodemo.DensityUtil;
import com.zzh12138.jzvideodemo.R;
import com.zzh12138.jzvideodemo.bean.NewsBean;
import com.zzh12138.jzvideodemo.bean.ViewAttr;
import com.zzh12138.jzvideodemo.player.JZVideoPlayer;
import com.zzh12138.jzvideodemo.player.JZVideoPlayerStandard;
import com.zzh12138.jzvideodemo.view.PlayerContainer;

import java.util.List;


/**
 * Created by zhangzhihao on 2018/6/19 17:35.
 */

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<NewsBean> mList;
    private Context mContext;
    private onVideoTitleClickListener onVideoTitleClickListener;

    public NewsAdapter(List<NewsBean> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(viewType, parent, false);
        if (viewType == R.layout.adapter_news_normal) {
            return new NormalHolder(view);
        } else {
            return new VideoHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NormalHolder) {
            setNormalData((NormalHolder) holder, position);
        } else {
            setVideoData((VideoHolder) holder, position);
        }
    }

    private void setNormalData(NormalHolder holder, int position) {
        NewsBean bean = mList.get(position);
        holder.title.setText(bean.getTitle());
        Glide.with(mContext).load(bean.getImageUrl()).into(holder.image);
    }

    private void setVideoData(final VideoHolder holder, int position) {
        final NewsBean bean = mList.get(position);
        holder.title.setText(bean.getTitle());
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onVideoTitleClickListener != null) {
                    int[] location = new int[2];
                    holder.container.getLocationOnScreen(location);
                    ViewAttr attr = new ViewAttr();
                    attr.setX(location[0]);
                    attr.setY(location[1]);
                    attr.setWidth(holder.container.getWidth());
                    attr.setHeight(holder.container.getHeight());
                    onVideoTitleClickListener.onTitleClick(holder.getLayoutPosition(), attr);
                }
            }
        });
        Glide.with(mContext).load(bean.getImageUrl()).into(holder.player.coverImageView);
        holder.player.positionInList = position;
        holder.player.setUp(bean.getVideoUrl(), JZVideoPlayer.SCREEN_WINDOW_NORMAL, bean.getTitle());
        holder.player.setVideoTime(bean.getDuration())
                .setOnVideoPlayerContainerClickListener(new JZVideoPlayerStandard.OnVideoPlayerContainerClickListener() {
                    @Override
                    public void videoPlayerContainerClick() {
                        if (onVideoTitleClickListener != null) {
                            int[] location = new int[2];
                            holder.container.getLocationOnScreen(location);
                            ViewAttr attr = new ViewAttr();
                            attr.setX(location[0]);
                            attr.setY(location[1]);
                            attr.setWidth(holder.container.getWidth());
                            attr.setHeight(holder.container.getHeight());
                            onVideoTitleClickListener.onTitleClick(holder.getLayoutPosition(), attr);
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getType();
    }

    public void setOnVideoTitleClickListener(NewsAdapter.onVideoTitleClickListener onVideoTitleClickListener) {
        this.onVideoTitleClickListener = onVideoTitleClickListener;
    }

    class NormalHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView image;

        public NormalHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.adapter_news_normal_title);
            image = itemView.findViewById(R.id.adapter_news_normal_image);
        }
    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        JZVideoPlayerStandard player;
        TextView title;
        PlayerContainer container;

        public VideoHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.adapter_video_title);
            player = itemView.findViewById(R.id.player);
            container = itemView.findViewById(R.id.adapter_video_container);
            container.setHeightRatio(9f)
                    .setWidthRatio(16f);
        }
    }

    public interface onVideoTitleClickListener {
        void onTitleClick(int position, ViewAttr attr);
    }

}
