package com.zzh12138.jzvideodemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.zzh12138.jzvideodemo.adapter.NewsAdapter;
import com.zzh12138.jzvideodemo.bean.NewsBean;
import com.zzh12138.jzvideodemo.bean.ViewAttr;
import com.zzh12138.jzvideodemo.fragment.VideoListFragment;
import com.zzh12138.jzvideodemo.itemDecoration.LineItemDecoration;
import com.zzh12138.jzvideodemo.player.JZMediaManager;
import com.zzh12138.jzvideodemo.player.JZUtils;
import com.zzh12138.jzvideodemo.player.JZVideoPlayer;
import com.zzh12138.jzvideodemo.player.JZVideoPlayerManager;
import com.zzh12138.jzvideodemo.player.JZVideoPlayerStandard;
import com.zzh12138.jzvideodemo.view.PlayerContainer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NewsAdapter.onVideoTitleClickListener, VideoListFragment.OnBackClickListener {
    private static final String TAG = "MainActivity";
    @BindView(R.id.recycler)
    RecyclerView recycler;

    private List<NewsBean> mList;
    private LinearLayoutManager mLayoutManager;
    private NewsAdapter mAdapter;

    private VideoListFragment videoListFragment;
    private boolean isShowVideo;
    private int clickPosition;
    private boolean isAttach;
    private boolean isChanging;

    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info != null) {
                    if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                        if (JZMediaManager.isWiFi) {
                            JZMediaManager.isWiFi = false;
                            JZVideoPlayer.WIFI_TIP_DIALOG_SHOWED = false;
                            if (JZVideoPlayerManager.getCurrentJzvd() != null &&
                                    (JZVideoPlayerManager.getCurrentJzvd().currentState == JZVideoPlayer.CURRENT_STATE_PLAYING ||
                                            JZVideoPlayerManager.getCurrentJzvd().currentState == JZVideoPlayer.CURRENT_STATE_PREPARING ||
                                            JZVideoPlayerManager.getCurrentJzvd().currentState == JZVideoPlayer.CURRENT_STATE_PREPARING_CHANGING_URL)) {
                                JZMediaManager.instance().jzMediaInterface.pause();
                                JZVideoPlayerManager.getCurrentJzvd().onStatePause();
                            }
                        }
                    } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                        if (!JZMediaManager.isWiFi) {
                            JZMediaManager.isWiFi = true;
                            JZVideoPlayer.WIFI_TIP_DIALOG_SHOWED = true;
                            if (JZVideoPlayerManager.getCurrentJzvd() != null &&
                                    JZVideoPlayerManager.getCurrentJzvd().currentState == JZVideoPlayer.CURRENT_STATE_PAUSE) {
                                JZVideoPlayer.goOnPlayOnResume();
                            }
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        mAdapter = new NewsAdapter(mList, this);
        mAdapter.setOnVideoTitleClickListener(this);
        mLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(mLayoutManager);
        recycler.setAdapter(mAdapter);
        recycler.addItemDecoration(new LineItemDecoration(this, R.drawable.line));
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    JZUtils.onScrollPlayVideo(recyclerView, mLayoutManager.findFirstVisibleItemPosition(), mLayoutManager.findLastVisibleItemPosition());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy != 0) {
                    JZUtils.onScrollReleaseAllVideos(mLayoutManager.findFirstVisibleItemPosition(), mLayoutManager.findLastVisibleItemPosition(), 1f);
                }
            }
        });
    }

    private void initData() {
        mList = new ArrayList<>(20);
        for (int i = 0; i < 17; i++) {
            NewsBean bean = new NewsBean();
            bean.setType(R.layout.adapter_news_normal);
            bean.setTitle("我是新闻标题新闻标题我是新闻标题新闻标题我是新闻标题新闻标题" + i);
            int result = i % 3;
            switch (result) {
                case 0:
                    bean.setImageUrl("http://img5.imgtn.bdimg.com/it/u=2539397329,4056054332&fm=27&gp=0.jpg");
                    break;
                case 1:
                    bean.setImageUrl("http://img3.imgtn.bdimg.com/it/u=3159360602,2315537063&fm=27&gp=0.jpg");
                    break;
                case 2:
                    bean.setImageUrl("http://img1.imgtn.bdimg.com/it/u=2156236282,1270726641&fm=27&gp=0.jpg");
                    break;
            }
            mList.add(bean);
        }

        NewsBean v1 = new NewsBean();
        v1.setTitle("视频新闻1");
        v1.setType(R.layout.adapter_news_video);
        v1.setImageUrl("http://img5.imgtn.bdimg.com/it/u=3577771133,2332148944&fm=27&gp=0.jpg");
        v1.setVideoUrl("https://mov.bn.netease.com/open-movie/nos/mp4/2016/01/11/SBC46Q9DV_hd.mp4");
        v1.setCommentNum(666);
        v1.setDuration(100);
        mList.add(4, v1);

        NewsBean v2 = new NewsBean();
        v2.setTitle("视频新闻2视频新闻2视频新闻2视频新闻2视频新闻2视频新闻2视频新闻2");
        v2.setType(R.layout.adapter_news_video);
        v2.setImageUrl("http://img0.imgtn.bdimg.com/it/u=3622851037,3121030191&fm=27&gp=0.jpg");
        v2.setVideoUrl("https://mov.bn.netease.com/open-movie/nos/mp4/2018/01/12/SD70VQJ74_sd.mp4");
        v2.setCommentNum(666);
        v2.setDuration(100);
        mList.add(9, v2);

        NewsBean v3 = new NewsBean();
        v3.setTitle("视频新闻3视频新闻3视频新闻3视频新闻3视频新闻3视频新闻3视频新闻3");
        v3.setType(R.layout.adapter_news_video);
        v3.setImageUrl("http://img5.imgtn.bdimg.com/it/u=3974436224,4269321529&fm=27&gp=0.jpg");
        v3.setVideoUrl("https://mov.bn.netease.com/open-movie/nos/mp4/2017/12/04/SD3SUEFFQ_hd.mp4");
        v3.setCommentNum(666);
        v3.setDuration(100);
        mList.add(10, v3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JZVideoPlayer.releaseAllVideos();
        if (mList != null) {
            mList.clear();
            mList = null;
        }
        recycler.clearOnScrollListeners();
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            if (isShowVideo) {
                recycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        videoListFragment.showMask(JZVideoPlayerManager.getCurrentJzvd());
                    }
                }, 300);
            }
        } else if (isShowVideo) {
            if (videoListFragment.isShowComment()) {
                videoListFragment.closeCommentFragment();
            } else {
                closeVideoListFragment();
            }
        } else {
            super.onBackPressed();
        }
    }


    private void closeVideoListFragment() {
        isShowVideo = false;
        final NewsBean bean = mList.get(clickPosition);
        if (videoListFragment.isPlayingFirst()) {
            videoListFragment.removeVideoList();
            JZMediaManager.instance().positionInList = clickPosition;
            int first = mLayoutManager.findFirstVisibleItemPosition();
            View v = recycler.getChildAt(clickPosition - first);
            if (v != null) {
                final PlayerContainer container = v.findViewById(R.id.adapter_video_container);
                if (!isAttach) {
                    container.removeAllViews();
                }
                recycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        JZVideoPlayerManager.getCurrentJzvd().attachToContainer(container);
                        JZVideoPlayerStandard p = (JZVideoPlayerStandard) JZVideoPlayerManager.getCurrentJzvd();
                        p.showAnimationView();
                        p.positionInList = clickPosition;
                        p.setVideoList(false);
                        p.setVideoActivity(false);
                        p.setVideoTime(bean.getDuration())
                                .setOnVideoPlayerContainerClickListener(new JZVideoPlayerStandard.OnVideoPlayerContainerClickListener() {
                                    @Override
                                    public void videoPlayerContainerClick() {
                                        int[] location = new int[2];
                                        container.getLocationOnScreen(location);
                                        ViewAttr attr = new ViewAttr();
                                        attr.setX(location[0]);
                                        attr.setY(location[1]);
                                        attr.setWidth(container.getWidth());
                                        attr.setHeight(container.getHeight());
                                        onTitleClick(clickPosition, attr);
                                    }
                                });

                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.remove(videoListFragment);
                        transaction.commitAllowingStateLoss();
                    }
                }, 800);
            }
        } else {
            JZVideoPlayer.releaseAllVideos();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(videoListFragment);
            transaction.commitAllowingStateLoss();
            if (isAttach) {
                int first = mLayoutManager.findFirstVisibleItemPosition();
                View v = recycler.getChildAt(clickPosition - first);
                if (v != null) {
                    final PlayerContainer container = v.findViewById(R.id.adapter_video_container);
                    container.removeAllViews();
                    JZVideoPlayerStandard p = new JZVideoPlayerStandard(MainActivity.this);
                    Glide.with(MainActivity.this).load(bean.getImageUrl()).into(p.coverImageView);
                    p.positionInList = clickPosition;
                    p.setUp(bean.getVideoUrl(), JZVideoPlayer.SCREEN_WINDOW_NORMAL, bean.getTitle());
                    p.setVideoTime(bean.getDuration())
                            .setOnVideoPlayerContainerClickListener(new JZVideoPlayerStandard.OnVideoPlayerContainerClickListener() {
                                @Override
                                public void videoPlayerContainerClick() {
                                    int[] location = new int[2];
                                    container.getLocationOnScreen(location);
                                    ViewAttr attr = new ViewAttr();
                                    attr.setX(location[0]);
                                    attr.setY(location[1]);
                                    attr.setWidth(container.getWidth());
                                    attr.setHeight(container.getHeight());
                                    onTitleClick(clickPosition, attr);
                                }
                            });
                    container.addView(p);
                }
            }
        }
//        clickPosition = -1;
//        isAttach = false;
    }


    @Override
    public void onTitleClick(int position, ViewAttr attr) {
        if (!isChanging) {
            isChanging = true;
            isShowVideo = true;
            clickPosition = position;
            isAttach = JZVideoPlayerManager.getCurrentJzvd() != null && JZMediaManager.isPlaying();
            videoListFragment = new VideoListFragment();
            videoListFragment.setAttr(attr);
            videoListFragment.setNews(mList.get(position));
            videoListFragment.setAttach(isAttach);
            if (!videoListFragment.isAdded() && getSupportFragmentManager().findFragmentByTag("acg") == null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.root, videoListFragment, "acg");
                transaction.commitAllowingStateLoss();
                recycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isChanging = false;
                    }
                }, 250);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        JZVideoPlayer.goOnPlayOnResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            //weChat moment share will execute twice so try catch
            unregisterReceiver(wifiReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackClick() {
        onBackPressed();
    }
}
