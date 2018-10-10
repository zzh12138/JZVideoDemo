package com.zzh12138.jzvideodemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.zzh12138.jzvideodemo.R;


/**
 * Created by zhangzhihao on 2018/7/26 10:10.
 */

public class VoiceAnimationView extends View {
    private static final String TAG = "VoiceAnimationView";

    private static final int DEFAULT_COLOR = 0xffffffff;
    private static final int DEFAULT_NUM = 3;
    private static final int DEFAULT_WIDTH = 5;
    private Paint mPaint;
    /**
     * 矩形个数
     */
    private int mCount;
    /**
     * 矩形间距
     */
    private float mPadding;
    /**
     * 矩形最大高度，默认为控件高度
     */
    private float maxRectangleHeight;
    /**
     * 矩形最小高度
     */
    private float minRectangleHeight;
    private float mRectangleWidth;
    /**
     * 颜色
     */
    private int mColor;

    private float percent;

    public VoiceAnimationView(Context context) {
        this(context, null);
    }

    public VoiceAnimationView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceAnimationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.VoiceAnimationView);
            mColor = arr.getColor(R.styleable.VoiceAnimationView_rectangleColor, DEFAULT_COLOR);
            mCount = arr.getInteger(R.styleable.VoiceAnimationView_rectangleNum, DEFAULT_NUM);
            mPadding = arr.getDimension(R.styleable.VoiceAnimationView_rectanglePadding, DEFAULT_WIDTH);
        }
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        maxRectangleHeight = h-getPaddingBottom()-getPaddingTop();
        minRectangleHeight = h / 3f;
        mRectangleWidth = (w - getPaddingLeft() - getPaddingRight() - mPadding * (mCount - 1)) / (float) mCount;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mCount; i++) {
            float top ;
            if ((i & 1) != 0) {
                top = (maxRectangleHeight - minRectangleHeight) * (1 - percent);
            } else {
                top = (maxRectangleHeight - minRectangleHeight - 5) * percent + i * 5;
            }
            if (top > maxRectangleHeight - minRectangleHeight) {
                top = maxRectangleHeight - minRectangleHeight;
            }
            canvas.drawRect(getPaddingLeft() + i * (mRectangleWidth + mPadding), top+getPaddingTop(),
                    getPaddingLeft() + mRectangleWidth + i * (mRectangleWidth + mPadding), maxRectangleHeight , mPaint);
        }
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
        invalidate();
    }
}
