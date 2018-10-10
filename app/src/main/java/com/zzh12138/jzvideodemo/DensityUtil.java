package com.zzh12138.jzvideodemo;

import android.content.Context;

/**
 * Created by zhangzhihao on 2018/9/21 11:00.
 */
public class DensityUtil {
    public static int dipTopx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f * (dpValue >= 0 ? 1 : -1));
    }
}
