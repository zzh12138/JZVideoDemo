package com.zzh12138.jzvideodemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/**
 * Created by zhangzhihao on 2018/6/10 15:11.
 */

public class CenterImageSpan extends ImageSpan {
    private static final String TAG = "CenterImageSpan";
    public CenterImageSpan(Drawable d) {
        super(d);
    }

    public CenterImageSpan(Context context, int resourceId) {
        super(context, resourceId);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        Drawable drawable = getDrawable();
        float transY =(bottom - top) / 2f - drawable.getBounds().bottom / 2f;
        canvas.save();
        canvas.translate(x, transY);
        drawable.draw(canvas);
        canvas.restore();
    }

}

