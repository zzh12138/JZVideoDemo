package com.zzh12138.jzvideodemo.itemDecoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by zhangzhihao on 2018/9/12 17:28.
 */
public class LineItemDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;
    private Drawable mDrawable;
    private int paddingLeft, paddingRight;

    public LineItemDecoration(Context mContext, int drawableId) {
        this.mContext = mContext;
        mDrawable = mContext.getResources().getDrawable(drawableId);
    }

    public LineItemDecoration(Context mContext, int drawableId, int paddingLeft, int paddingRight) {
        this.mContext = mContext;
        mDrawable = mContext.getResources().getDrawable(drawableId);
        this.paddingLeft = paddingLeft;
        this.paddingRight = paddingRight;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int count = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        for (int i = 0; i < count - 1; i++) {
            View view = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
            int top = view.getBottom() + params.bottomMargin;
            int bottom = top + mDrawable.getIntrinsicHeight();
            mDrawable.setBounds(left+paddingLeft, top, right-paddingRight, bottom);
            mDrawable.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        int count = parent.getChildCount();
        if (position < count - 1) {
            outRect.set(0, 0, 0, mDrawable.getIntrinsicHeight());
        }
    }
}
