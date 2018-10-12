package com.zzh12138.jzvideodemo.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zzh12138.jzvideodemo.R;
import com.zzh12138.jzvideodemo.bean.NewsBean;
import com.zzh12138.jzvideodemo.bean.ViewAttr;
import com.zzh12138.jzvideodemo.player.JZMediaManager;
import com.zzh12138.jzvideodemo.player.JZVideoPlayer;
import com.zzh12138.jzvideodemo.player.JZVideoPlayerManager;
import com.zzh12138.jzvideodemo.player.JZVideoPlayerStandard;
import com.zzh12138.jzvideodemo.view.PlayerContainer;

import java.util.List;

import static com.zzh12138.jzvideodemo.fragment.VideoCommentFragment.DURATION;

/**
 * Created by zhangzhihao on 2018/9/13 10:03.
 */
public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoHolder> {
    private static final String TAG = "VideoListAdapter";

    private Context mContext;
    private List<NewsBean> mList;
    private boolean isAttach;
    private ViewAttr attr;
    private OnVideoPlayCompleteListener completeListener;
    private OnVideoPlayClickListener playClickListener;
    private OnCommentClickListener commentClickListener;
    private OnAttachAnimationFinishListener onAnimationFinishListener;


    public VideoListAdapter(Context mContext, List<NewsBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_video, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoHolder holder, int position) {
        final NewsBean bean = mList.get(position);
        holder.title.setText(bean.getTitle());
        holder.source.setText("来源来源来源");
        holder.praise.setText("666");
        holder.comment.setText("777");
        if (isAttach && position == 0) {
            holder.container.removeAllViews();
            holder.player = (JZVideoPlayerStandard) JZVideoPlayerManager.getCurrentJzvd();
            holder.player.hideAnimationView();
            holder.itemView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    holder.itemView.getViewTreeObserver().removeOnPreDrawListener(this);
                    int[] l = new int[2];
                    holder.itemView.getLocationOnScreen(l);
                    holder.itemView.setTranslationY(attr.getY() - l[1] - (holder.container.getMeasuredHeight() - attr.getHeight()) / 2 - holder.title.getMeasuredHeight());
                    holder.container.setScaleX(attr.getWidth() / (float) holder.container.getMeasuredWidth());
                    holder.container.setScaleY(attr.getHeight() / (float) holder.container.getMeasuredHeight());
                    holder.title.setAlpha(0);
                    holder.bottomLayout.setAlpha(0);
                    holder.itemView.animate().translationY(0).setDuration(DURATION);
                    holder.title.animate().alpha(1f).setDuration(DURATION);
                    holder.bottomLayout.animate().alpha(1f).setDuration(DURATION);
                    holder.container.animate().scaleX(1f).scaleY(1f).setDuration(DURATION);
                    holder.container.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (onAnimationFinishListener != null) {
                                onAnimationFinishListener.onAnimationFinish();
                            }
                        }
                    }, DURATION);
                    isAttach = false;
                    JZMediaManager.instance().positionInList = 0;
                    JZVideoPlayerManager.getCurrentJzvd().attachToContainer(holder.container);
                    JZVideoPlayerManager.setFirstFloor(holder.player);
                    return true;
                }
            });
        } else {
            holder.player.setUp(bean.getVideoUrl(), JZVideoPlayer.SCREEN_WINDOW_NORMAL);
        }
        holder.player.setVideoList(true);
        holder.player.setPlayerContainer(holder.container);
        holder.player.positionInList = position;
        holder.player.setVideoActivity(true);
        holder.player.setOnVideoCompleteListener(new JZVideoPlayerStandard.OnVideoCompleteListener() {
            @Override
            public void onVideoPlayComplete() {
                if (completeListener != null) {
                    completeListener.playNextVideo();
                }
            }
        }).setOnVideoPlayClickListener(new JZVideoPlayerStandard.OnVideoPlayClickListener() {
            @Override
            public void videoPlayClick() {
                if (playClickListener != null) {
                    playClickListener.scrollToPosition(holder.getLayoutPosition());
                }
            }
        }).setOnVideoTimeChangeListener(new JZVideoPlayerStandard.OnVideoTimeChangeListener() {
            @Override
            public void showWillPlayNextTip() {
                if (JZVideoPlayerManager.getCurrentJzvd() == holder.player && completeListener != null) {
                    completeListener.showWillPlayNextTip();
                }
            }
        });
        Glide.with(mContext).load(bean.getImageUrl()).into(holder.player.coverImageView);
        Glide.with(mContext).load(R.mipmap.ic_launcher_round).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                resource.setBounds(0, 0, 50, 50);
                holder.source.setCompoundDrawables(resource, null, null, null);
            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long delay = 0L;
                if (JZMediaManager.instance().positionInList != holder.getLayoutPosition() || holder.itemView.getTop() != 0) {
                    if (JZVideoPlayerManager.getCurrentJzvd() != null && JZVideoPlayerManager.getCurrentJzvd() != holder.player) {
                        holder.player.startButton.performClick();
                    }
                    delay = 350L;
                }
                holder.comment.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (commentClickListener != null) {
                            ViewAttr attr = new ViewAttr();
                            int[] location = new int[2];
                            holder.container.getLocationOnScreen(location);
                            attr.setX(location[0]);
                            attr.setY(location[1]);
                            commentClickListener.onCommentClick(bean, attr);
                            if (JZVideoPlayerManager.getCurrentJzvd() == null) {
                                JZVideoPlayerManager.FIRST_FLOOR_JZVD = holder.player;
                                JZMediaManager.instance().positionInList = holder.getLayoutPosition();
                            }
                        }
                    }
                }, delay);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class VideoHolder extends RecyclerView.ViewHolder {

        PlayerContainer container;
        JZVideoPlayerStandard player;
        TextView source;
        TextView title;
        TextView praise;
        TextView comment;
        ImageView share;
        RelativeLayout bottomLayout;

        public VideoHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            container.setHeightRatio(9f)
                    .setWidthRatio(16f);
            player = itemView.findViewById(R.id.player);
            source = itemView.findViewById(R.id.source);
            title = itemView.findViewById(R.id.video_title);
            praise = itemView.findViewById(R.id.praise);
            comment = itemView.findViewById(R.id.comment);
            share = itemView.findViewById(R.id.share);
            bottomLayout = itemView.findViewById(R.id.bottom_layout);
        }
    }

    public void setCompleteListener(OnVideoPlayCompleteListener completeListener) {
        this.completeListener = completeListener;
    }

    public void setCommentClickListener(OnCommentClickListener commentClickListener) {
        this.commentClickListener = commentClickListener;
    }

    public void setPlayClickListener(OnVideoPlayClickListener playClickListener) {
        this.playClickListener = playClickListener;
    }

    public void setOnAnimationFinishListener(OnAttachAnimationFinishListener onAnimationFinishListener) {
        this.onAnimationFinishListener = onAnimationFinishListener;
    }

    public void setAttach(boolean attach) {
        isAttach = attach;
    }

    public void setAttr(ViewAttr attr) {
        this.attr = attr;
    }

    public interface OnVideoPlayCompleteListener {
        void playNextVideo();

        void showWillPlayNextTip();
    }

    public interface OnVideoPlayClickListener {
        void scrollToPosition(int position);
    }

    public interface OnCommentClickListener {
        void onCommentClick(NewsBean article, ViewAttr attr);
    }

    public interface OnAttachAnimationFinishListener {
        void onAnimationFinish();
    }

}
