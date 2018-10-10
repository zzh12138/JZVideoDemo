package com.zzh12138.jzvideodemo.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by zhangzhihao on 2018/8/30 9:56.
 * 播放器容器
 * 只有当需要做页面平移的时候才需要使用
 */
public class PlayerContainer extends FrameLayout {
    private float widthRatio, heightRatio;

    public PlayerContainer(@NonNull Context context) {
        super(context);
    }

    public PlayerContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayerContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (widthRatio == 0f || heightRatio == 0f) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            int specWidth = MeasureSpec.getSize(widthMeasureSpec);
            int specHeight = (int) (specWidth * heightRatio / widthRatio);
            setMeasuredDimension(specWidth, specHeight);
            if(getChildCount()>0) {
                int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(specWidth, MeasureSpec.EXACTLY);
                int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(specHeight, MeasureSpec.EXACTLY);
                getChildAt(0).measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
    }

    public PlayerContainer setWidthRatio(float widthRatio) {
        this.widthRatio = widthRatio;
        return this;
    }

    public PlayerContainer setHeightRatio(float heightRatio) {
        this.heightRatio = heightRatio;
        return this;
    }
}
