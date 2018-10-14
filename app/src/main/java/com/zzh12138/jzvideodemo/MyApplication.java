package com.zzh12138.jzvideodemo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.zzh12138.jzvideodemo.player.JZExoPlayer;
import com.zzh12138.jzvideodemo.player.JZVideoPlayer;
import com.zzh12138.jzvideodemo.player.SoDownloadIntentService;

/**
 * Created by zhangzhihao on 2018/9/12 11:31.
 */
public class MyApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        JZVideoPlayer.setMediaInterface(new JZExoPlayer());
        Intent so = new Intent(mContext, SoDownloadIntentService.class);
        mContext.startService(so);
    }

    public static Context getInstance() {
        return mContext;
    }
}
