package com.zzh12138.jzvideodemo.fragment;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zzh12138.jzvideodemo.DensityUtil;
import com.zzh12138.jzvideodemo.MainActivity;
import com.zzh12138.jzvideodemo.R;
import com.zzh12138.jzvideodemo.adapter.VideoListAdapter;
import com.zzh12138.jzvideodemo.bean.NewsBean;
import com.zzh12138.jzvideodemo.bean.ViewAttr;
import com.zzh12138.jzvideodemo.player.JZMediaManager;
import com.zzh12138.jzvideodemo.player.JZUtils;
import com.zzh12138.jzvideodemo.player.JZVideoPlayer;
import com.zzh12138.jzvideodemo.player.JZVideoPlayerManager;
import com.zzh12138.jzvideodemo.player.JZVideoPlayerStandard;
import com.zzh12138.jzvideodemo.view.MaskView;
import com.zzh12138.jzvideodemo.view.PlayerContainer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.zzh12138.jzvideodemo.fragment.VideoCommentFragment.DURATION;
import static com.zzh12138.jzvideodemo.player.JZVideoPlayer.CURRENT_STATE_AUTO_COMPLETE;
import static com.zzh12138.jzvideodemo.player.JZVideoPlayer.CURRENT_STATE_NORMAL;
import static com.zzh12138.jzvideodemo.player.JZVideoPlayer.SCREEN_WINDOW_FULLSCREEN;

/**
 * Created by zhangzhihao on 2018/9/12 17:59.
 */
public class VideoListFragment extends Fragment implements VideoListAdapter.OnCommentClickListener, VideoListAdapter.OnVideoPlayClickListener, VideoListAdapter.OnVideoPlayCompleteListener, VideoCommentFragment.OnCloseClickListener, VideoListAdapter.OnAttachAnimationFinishListener {
    private static final String TAG = "VideoListFragment";

    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.root)
    FrameLayout mRoot;
    Unbinder unbinder;
    @BindView(R.id.next)
    TextView mNext;
    @BindView(R.id.mask)
    MaskView mask;

    private View mView;
    private VideoListAdapter mAdapter;
    private List<NewsBean> mList;
    private String[] url = {
            "http://jiajunhui.cn/video/kaipao.mp4",
            "https://bmob-cdn-21848.b0.upaiyun.com/2018/10/12/ac83886740c3ab6c808ba41b78e7ce1f.mp4",
            "http://jiajunhui.cn/video/kongchengji.mp4",
            "https://bmob-cdn-21848.b0.upaiyun.com/2018/10/12/6d6311b740f31dea80fd16e8d9a26b98.mp4",
            "http://jiajunhui.cn/video/allsharestar.mp4",
            "http://jiajunhui.cn/video/edwin_rolling_in_the_deep.flv",
            "http://jiajunhui.cn/video/crystalliz.flv",
            "http://jiajunhui.cn/video/big_buck_bunny.mp4",
            "http://jiajunhui.cn/video/trailer.mp4",
            "https://mov.bn.netease.com/open-movie/nos/mp4/2018/01/12/SD70VQJ74_sd.mp4",
            "https://mov.bn.netease.com/open-movie/nos/mp4/2017/12/04/SD3SUEFFQ_hd.mp4",
            "https://mov.bn.netease.com/open-movie/nos/mp4/2017/12/04/SD3SUEFFQ_hd.mp4",
            "https://mov.bn.netease.com/open-movie/nos/mp4/2016/01/11/SBC46Q9DV_hd.mp4"
    };
    private String[] cover = {
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1539513473553&di=9dc2c9b5c6b63558072ca61d55c9d30f&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201611%2F13%2F20161113214253_NWdzE.jpeg",
            "http://i1.hdslb.com/bfs/archive/650939780b3174fe7d3277e3288979cc6223a8ad.jpg",
            "http://img3.duitang.com/uploads/item/201401/27/20140127213202_4Tmsa.jpeg",
            "http://pic.feizl.com/upload/allimg/170616/0401544L6-9.jpg",
            "http://android-wallpapers.25pp.com/20140415/1446/534cd60d01d7047_900x675.jpg",
            "http://cdn.duitang.com/uploads/item/201602/27/20160227181133_YjZVe.jpeg",
            "http://img3.imgtn.bdimg.com/it/u=999553423,3288263012&fm=26&gp=0.jpg",
            "http://pic27.photophoto.cn/20130630/0036036878082529_b.jpg",
            "http://img4.imgtn.bdimg.com/it/u=1995890925,817602913&fm=26&gp=0.jpg",
            "http://img1.imgtn.bdimg.com/it/u=3655565462,314827133&fm=26&gp=0.jpg",
            "http://img1.imgtn.bdimg.com/it/u=1627185924,1925590495&fm=26&gp=0.jpg",
            "http://img0.imgtn.bdimg.com/it/u=3622851037,3121030191&fm=27&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=3159360602,2315537063&fm=27&gp=0.jpg"};
    private LinearLayoutManager mLayoutManager;
    private boolean isShowComment;
    private boolean isNeedToPlayNext;
    private VideoCommentFragment commentFragment;
    private OnBackClickListener onBackClickListener;
    private ObjectAnimator showAnimator;
    private NewsBean mNews;
    private ViewAttr mAttr;
    private boolean isAttach;
    private ObjectAnimator bgAnimator;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            onBackClickListener = (OnBackClickListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_video_list, container, false);
        unbinder = ButterKnife.bind(this, mView);
        JZVideoPlayerStandard.videoActivityIsMute = true;
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecycler.setLayoutManager(mLayoutManager);
        mList = new ArrayList<>();
        mList.add(mNews);
        initRecyclerView();
        if (!isAttach) {
            getData();
            mRecycler.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mRecycler.getViewTreeObserver().removeOnPreDrawListener(this);
                    View v = mRecycler.getChildAt(0);
                    if (v != null) {
                        JZVideoPlayerStandard p = v.findViewById(R.id.player);
                        p.startButton.performClick();
                    }
                    return true;
                }
            });
        }
        bgAnimator = ObjectAnimator.ofInt(mRoot, "backgroundColor", 0x00000000, 0xff000000);
        bgAnimator.setEvaluator(new ArgbEvaluator());
        bgAnimator.setDuration(DURATION);
        bgAnimator.start();
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBackClickListener != null) {
                    onBackClickListener.onBackClick();
                }
            }
        });
        return mView;
    }

    private void getData() {
        for (int i = 0; i < url.length; i++) {
            NewsBean v3 = new NewsBean();
            v3.setTitle("视频新闻视频新闻视频新闻视频新闻视频新闻视频新闻视频新闻" + i);
            v3.setType(R.layout.adapter_video);
            v3.setImageUrl(cover[i]);
            v3.setVideoUrl(url[i]);
            v3.setCommentNum(666);
            mList.add(v3);
        }
        NewsBean noMore = new NewsBean();
        noMore.setType(R.layout.layout_no_more);
        mList.add(noMore);
        mAdapter.notifyItemRangeInserted(1, url.length);
        mBack.animate().alpha(1f).setDuration(DURATION);
    }

    private void initRecyclerView() {
        mAdapter = new VideoListAdapter(getContext(), mList);
        mAdapter.setCommentClickListener(this);
        mAdapter.setPlayClickListener(this);
        mAdapter.setCompleteListener(this);
        mAdapter.setOnAnimationFinishListener(this);
        mAdapter.setAttach(isAttach);
        mAdapter.setAttr(mAttr);
        mRecycler.setAdapter(mAdapter);
        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                boolean flag = true;
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mNext.setVisibility(View.GONE);
                    int first = mLayoutManager.findFirstVisibleItemPosition();
                    int pos = mLayoutManager.findLastVisibleItemPosition();
                    for (int i = 0; i <= pos - first; i++) {
                        View view = recyclerView.getChildAt(i);
                        JZVideoPlayerStandard player = view.findViewById(R.id.player);
                        if (player != null && JZUtils.getViewVisiblePercent(player) == 1f) {
                            if (JZMediaManager.instance().positionInList != i + first) {
                                player.startButton.performClick();
                                flag = false;
                            }
                            break;
                        }
                    }
                }
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        mask.setVisibility(View.GONE);
                        break;
                    case RecyclerView.SCROLL_STATE_IDLE:
                        if (flag) {
                            showMask(JZVideoPlayerManager.getCurrentJzvd());
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy != 0) {
                    JZUtils.onScrollReleaseAllVideos(mLayoutManager.findFirstVisibleItemPosition(), mLayoutManager.findLastVisibleItemPosition(), 0.2f);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecycler.clearOnScrollListeners();
        if (showAnimator != null && showAnimator.isRunning()) {
            showAnimator.cancel();
            showAnimator = null;
        }
        unbinder.unbind();
    }

    @Override
    public void onCommentClick(NewsBean article, ViewAttr attr) {
        isShowComment = true;
        mask.setVisibility(View.GONE);
        mNext.setVisibility(View.GONE);
        commentFragment = new VideoCommentFragment();
        commentFragment.setOnCloseClickListener(this);
        commentFragment.setAttr(attr);
        commentFragment.setNews(article);
        if (!commentFragment.isAdded() && getFragmentManager().findFragmentByTag("ff") == null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.comment_container, commentFragment, "ff");
            transaction.commitAllowingStateLoss();
        }
    }

    @Override
    public void scrollToPosition(int position) {
        if (mRecycler != null && !isShowComment && position >= 0 && position < mList.size()) {
            int first = mLayoutManager.findFirstVisibleItemPosition();
            View view = mRecycler.getChildAt(position - first);
            if (view != null) {
                if (position == mList.size() - 2) {
                    //最后一个
                    JZVideoPlayerStandard player = view.findViewById(R.id.player);
                    showMask(player);
                } else {
                    int scrollY = view.getTop();
                    scrollY -= DensityUtil.dipTopx(getContext(), 50);
                    if (position > JZMediaManager.instance().positionInList) {
                        //点击下一个
                        if (scrollY < 0) {
                            scrollY = 0;
                        }
                    }
                    mRecycler.smoothScrollBy(0, scrollY);
                    if (scrollY == 0) {
                        JZVideoPlayerStandard player = view.findViewById(R.id.player);
                        showMask(player);
                    }
                }
            }
        }
    }

    @Override
    public void playNextVideo() {
        if (isShowComment) {
            JZVideoPlayerManager.getCurrentJzvd().setState(CURRENT_STATE_NORMAL);
            JZVideoPlayer.backPress();
            isNeedToPlayNext = true;
        } else {
            if (JZVideoPlayerManager.getCurrentJzvd().currentScreen == SCREEN_WINDOW_FULLSCREEN) {
                JZMediaManager.instance().positionInList++;
                JZVideoPlayerManager.getCurrentJzvd().changeUrl(mList.get(JZMediaManager.instance().positionInList).getVideoUrl());
                mLayoutManager.scrollToPositionWithOffset(JZMediaManager.instance().positionInList, 0);
                mRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        JZVideoPlayerManager.getFirstFloor().setState(CURRENT_STATE_NORMAL);
                        mask.setVisibility(View.GONE);
                        JZVideoPlayerManager.setFirstFloor((JZVideoPlayer) mRecycler.getChildAt(0).findViewById(R.id.player));
                    }
                }, 500);
            } else {
                long delay = 500;
                mRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int position = JZMediaManager.instance().positionInList;
                        if (position >= 0 && position < mList.size() - 2) {
                            int first = mLayoutManager.findFirstVisibleItemPosition();
                            View view = mRecycler.getChildAt(position + 1 - first);
                            if (view != null) {
                                int scrollY;
                                scrollY = view.getTop() - DensityUtil.dipTopx(getContext(), 50);
                                mRecycler.smoothScrollBy(0, scrollY);
                            }
                        }
                    }
                }, delay);
            }
        }
    }

    @Override
    public void showWillPlayNextTip() {
        if (!isShowComment && JZMediaManager.instance().positionInList < mList.size() - 2) {
            mNext.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void closeCommentFragment() {
        isShowComment = false;
        commentFragment.closeCommentFragment();
        mRecycler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.remove(commentFragment);
                transaction.commitAllowingStateLoss();
                int first = mLayoutManager.findFirstVisibleItemPosition();
                View v = mRecycler.getChildAt(JZMediaManager.instance().positionInList - first);
                if (v != null) {
                    JZVideoPlayerManager.getCurrentJzvd().attachToContainer((ViewGroup) v.findViewById(R.id.container));
                }
                int state = JZVideoPlayerManager.getCurrentJzvd().currentState;
                if (state != CURRENT_STATE_NORMAL && state != CURRENT_STATE_AUTO_COMPLETE) {
                    showMask(JZVideoPlayerManager.getCurrentJzvd());
                } else if (isNeedToPlayNext) {
                    isNeedToPlayNext = false;
                    playNextVideo();
                }
            }
        }, DURATION);
    }

    public boolean isShowComment() {
        return isShowComment;
    }

    public void showMask(JZVideoPlayer player) {
        if (player != null && !isShowComment && mask.getVisibility() == View.GONE) {
            int[] a = new int[2];
            player.getLocationOnScreen(a);
            a[1] -= getStatusBarHeight(getContext());
            mask.setVisibility(View.VISIBLE);
            mask.changeMaskLocation(a[1], a[1] + player.getHeight());
            if (showAnimator == null) {
                showAnimator = ObjectAnimator.ofFloat(mask, "alpha", 0f, 1f);
                showAnimator.setDuration(150);
            }
            showAnimator.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAnimationFinish() {
        getData();
        mRecycler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showMask(JZVideoPlayerManager.getCurrentJzvd());
            }
        }, 100);
    }

    public void removeVideoList() {
        mask.setVisibility(View.GONE);
        int size = mList.size() - 1;
        for (int i = size; i > 0; i--) {
            mList.remove(i);
        }
        mAdapter.notifyItemRangeRemoved(1, size);
        final View v = mRecycler.getChildAt(0);
        final int[] location = new int[2];
        v.getLocationOnScreen(location);
        final PlayerContainer container = v.findViewById(R.id.container);
        final TextView title = v.findViewById(R.id.video_title);
        final RelativeLayout bottomLayout = v.findViewById(R.id.bottom_layout);
        title.postDelayed(new Runnable() {
            @Override
            public void run() {
                title.animate().alpha(0f).setDuration(DURATION);
                bottomLayout.animate().alpha(0f).setDuration(DURATION);
                container.animate().scaleX((float) mAttr.getWidth() / container.getWidth())
                        .scaleY((float) mAttr.getHeight() / container.getHeight())
                        .setDuration(DURATION);
                v.animate().translationY(mAttr.getY() - location[1] - (container.getHeight() - mAttr.getHeight()) / 2 - title.getHeight()).setDuration(DURATION);
                bgAnimator.reverse();
            }
        }, 250);
    }

    public interface OnBackClickListener {
        void onBackClick();
    }

    public void setNews(NewsBean mNews) {
        this.mNews = mNews;
    }

    public void setAttr(ViewAttr mAttr) {
        this.mAttr = mAttr;
    }

    public void setAttach(boolean attach) {
        isAttach = attach;
    }

    public boolean isPlayingFirst() {
        return JZMediaManager.isPlaying() && JZMediaManager.instance().positionInList == 0;
    }

    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(o);
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return statusBarHeight;
        }

    }
}
