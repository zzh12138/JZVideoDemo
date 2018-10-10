package com.zzh12138.jzvideodemo.player;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.zzh12138.jzvideodemo.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by zhangzhihao on 2018/10/10 14:37.
 */
public class SoDownloadIntentService extends IntentService {


    public SoDownloadIntentService() {
        super("SoDownloadIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        File dir = getDir("libs", Context.MODE_PRIVATE);
        File soFile = new File(dir, "ijkffmpeg.so");
        if (soFile.exists()) {
            if (JZVideoPlayerManager.getCurrentJzvd() == null) {
                JZVideoPlayer.setMediaInterface(new JZMediaIjkplayer());
            }
        } else {
            String url = "http://devstatic.nfapp.southcn.com/video/libijkffmpeg.so";
            try {
                URL downUrl = new URL(url);
                URLConnection connection = downUrl.openConnection();
                InputStream is = connection.getInputStream();
                int fileSize = connection.getContentLength();
                if (fileSize <= 0) {
                    throw new RuntimeException("file error");
                }
                if (is == null) {
                    throw new RuntimeException("stream is null");
                }
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(soFile);
                byte buf[] = new byte[1024];
                do {
                    int num = is.read(buf);
                    if (num == -1) {
                        break;
                    }
                    fos.write(buf, 0, num);
                } while (true);
                is.close();
                if (JZVideoPlayerManager.getCurrentJzvd() == null) {
                    JZVideoPlayer.setMediaInterface(new JZMediaIjkplayer());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
