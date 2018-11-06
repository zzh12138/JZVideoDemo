package com.zzh12138.jzvideodemo.fragment;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zzh12138.jzvideodemo.DensityUtil;
import com.zzh12138.jzvideodemo.R;
import com.zzh12138.jzvideodemo.adapter.CommentAdapter;
import com.zzh12138.jzvideodemo.bean.CommentBean;
import com.zzh12138.jzvideodemo.bean.NewsBean;
import com.zzh12138.jzvideodemo.bean.ViewAttr;
import com.zzh12138.jzvideodemo.itemDecoration.LineItemDecoration;
import com.zzh12138.jzvideodemo.player.JZVideoPlayerManager;
import com.zzh12138.jzvideodemo.view.PlayerContainer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by zhangzhihao on 2018/9/12 17:59.
 */
public class VideoCommentFragment extends Fragment {
    private static final String TAG = "VideoCommentFragment";

    public static final long DURATION = 300;
    @BindView(R.id.container)
    PlayerContainer container;
    @BindView(R.id.close)
    TextView close;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.root)
    LinearLayout root;
    Unbinder unbinder;


    private NewsBean mNews;
    private ViewAttr mAttr;
    private int[] location;
    private List<CommentBean> mList;
    private CommentAdapter mAdapter;
    private OnCloseClickListener onCloseClickListener;
    private ObjectAnimator animator;
    private boolean isChanging;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_video_comment, container, false);
        unbinder = ButterKnife.bind(this, mView);
        initData();
        animate();
        initRecyclerView();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isChanging) {
                    isChanging = true;
                    if (onCloseClickListener != null) {
                        onCloseClickListener.closeCommentFragment();
                        close.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isChanging = false;
                            }
                        }, 250);
                    }
                }
            }
        });
        return mView;
    }

    private void initData() {
        location = new int[2];
        mList = new ArrayList<>(20);
        for (int i = 0; i < 20; i++) {
            CommentBean bean = new CommentBean();
            bean.setId(String.valueOf(i));
            bean.setUserName("我就是个开发仔" + i);
            bean.setContent("大佬不要再秀了，bug仔学不动啦......");
            bean.setPraiseNum(12138);
            if ((i & 1) == 0) {
                bean.setParentComment(null);
            } else {
                CommentBean parent = new CommentBean();
                parent.setId(String.valueOf(100 + i));
                parent.setUserName("大佬" + (100 + i));
                parent.setContent("太简单了吧，hhhhhh");
                bean.setParentComment(parent);
            }
            mList.add(bean);
        }
        container.setWidthRatio(16f)
                .setHeightRatio(9f);
    }

    private void animate() {
        animator = ObjectAnimator.ofInt(root, "backgroundColor", 0x00000000, 0xff000000);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setDuration(DURATION);
        animator.start();
        container.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                container.getViewTreeObserver().removeOnPreDrawListener(this);
                container.getLocationOnScreen(location);
                container.setTranslationY(mAttr.getY() - location[1]);
                container.animate().translationY(0).setDuration(DURATION);
                JZVideoPlayerManager.getCurrentJzvd().attachToContainer(container);
                container.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (close != null) {
                            close.setVisibility(View.VISIBLE);
                        }
                        if (recycler != null) {
                            recycler.setVisibility(View.VISIBLE);
                        }
                    }
                }, DURATION);
                return true;
            }
        });
    }

    private void initRecyclerView() {
        mAdapter = new CommentAdapter(getContext(), mList);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.addItemDecoration(new LineItemDecoration(getContext(), R.drawable.line, DensityUtil.dipTopx(getContext(), 55)
                , DensityUtil.dipTopx(getContext(), 15)));
        recycler.setAdapter(mAdapter);
    }

    public void closeCommentFragment() {
        recycler.animate().alpha(0).setDuration(DURATION);
        close.animate().alpha(0).setDuration(DURATION);
        container.animate().translationY(mAttr.getY() - location[1]).setDuration(DURATION);
        animator.reverse();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    interface OnCloseClickListener {
        void closeCommentFragment();
    }

    public void setOnCloseClickListener(OnCloseClickListener onCloseClickListener) {
        this.onCloseClickListener = onCloseClickListener;
    }

    public void setNews(NewsBean mNews) {
        this.mNews = mNews;
    }

    public void setAttr(ViewAttr mAttr) {
        this.mAttr = mAttr;
    }
}
