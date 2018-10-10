package com.zzh12138.jzvideodemo.player;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;


import com.zzh12138.jzvideodemo.R;

import java.util.Formatter;
import java.util.LinkedHashMap;
import java.util.Locale;


/**
 * Created by Nathen
 * On 2016/02/21 12:25
 */
public class JZUtils {
    public static final String TAG = "JiaoZiVideoPlayer";

    public static String stringForTime(long timeMs) {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        long totalSeconds = timeMs / 1000;
        int seconds = (int) (totalSeconds % 60);
        int minutes = (int) ((totalSeconds / 60) % 60);
        int hours = (int) (totalSeconds / 3600);
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * This method requires the caller to hold the permission ACCESS_NETWORK_STATE.
     *
     * @param context context
     * @return if wifi is connected,return true
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * Get activity from context object
     *
     * @param context context
     * @return object of Activity or null if it is not Activity
     */
    public static Activity scanForActivity(Context context) {
        if (context == null) return null;

        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return scanForActivity(((ContextWrapper) context).getBaseContext());
        }

        return null;
    }

    /**
     * Get AppCompatActivity from context
     *
     * @param context context
     * @return AppCompatActivity if it's not null
     */
    public static AppCompatActivity getAppCompActivity(Context context) {
        if (context == null) return null;
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextThemeWrapper) {
            return getAppCompActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }

    public static void setRequestedOrientation(Context context, int orientation) {
        if (JZUtils.getAppCompActivity(context) != null) {
            JZUtils.getAppCompActivity(context).setRequestedOrientation(
                    orientation);
        } else {
            JZUtils.scanForActivity(context).setRequestedOrientation(
                    orientation);
        }
    }

    public static Window getWindow(Context context) {
        if (JZUtils.getAppCompActivity(context) != null) {
            return JZUtils.getAppCompActivity(context).getWindow();
        } else {
            return JZUtils.scanForActivity(context).getWindow();
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static void saveProgress(Context context, Object url, long progress) {
        if (!JZVideoPlayer.SAVE_PROGRESS) return;
        Log.i(TAG, "saveProgress: " + progress);
        if (progress < 500) {
            progress = 0;
        }
        SharedPreferences spn = context.getSharedPreferences("JZVD_PROGRESS",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spn.edit();
        editor.putLong("newVersion:" + url.toString(), progress).apply();
    }

    public static long getSavedProgress(Context context, Object url) {
        if (!JZVideoPlayer.SAVE_PROGRESS) return 0;
        SharedPreferences spn = context.getSharedPreferences("JZVD_PROGRESS",
                Context.MODE_PRIVATE);
        return spn.getLong("newVersion:" + url.toString(), 0);
    }

    /**
     * if url == null, clear all progress
     *
     * @param context context
     * @param url     if url!=null clear this url progress
     */
    public static void clearSavedProgress(Context context, Object url) {
        if (url == null) {
            SharedPreferences spn = context.getSharedPreferences("JZVD_PROGRESS",
                    Context.MODE_PRIVATE);
            spn.edit().clear().apply();
        } else {
            SharedPreferences spn = context.getSharedPreferences("JZVD_PROGRESS",
                    Context.MODE_PRIVATE);
            spn.edit().putLong("newVersion:" + url.toString(), 0).apply();
        }
    }

    public static Object getCurrentFromDataSource(Object[] dataSourceObjects, int index) {
        LinkedHashMap<String, Object> map = (LinkedHashMap) dataSourceObjects[0];
        if (map != null && map.size() > 0) {
            return getValueFromLinkedMap(map, index);
        }
        return null;
    }

    public static Object getValueFromLinkedMap(LinkedHashMap<String, Object> map, int index) {
        int currentIndex = 0;
        for (String key : map.keySet()) {
            if (currentIndex == index) {
                return map.get(key);
            }
            currentIndex++;
        }
        return null;
    }

    public static boolean dataSourceObjectsContainsUri(Object[] dataSourceObjects, Object object) {
        LinkedHashMap<String, Object> map = (LinkedHashMap) dataSourceObjects[0];
        if (map != null && object != null) {
            return map.containsValue(object);
        }
        return false;
    }

    public static String getKeyFromDataSource(Object[] dataSourceObjects, int index) {
        LinkedHashMap<String, Object> map = (LinkedHashMap) dataSourceObjects[0];
        int currentIndex = 0;
        for (String key : map.keySet()) {
            if (currentIndex == index) {
                return key;
            }
            currentIndex++;
        }
        return null;
    }

    public static float getViewVisiblePercent(View view) {
        if (view == null) {
            return 0f;
        }
        float height = view.getHeight();
        Rect rect = new Rect();
        if (!view.getLocalVisibleRect(rect)) {
            return 0f;
        }
        float visibleHeight = rect.bottom - rect.top;
        Log.d(TAG, "getViewVisiblePercent: emm " + visibleHeight);
        return visibleHeight / height;
    }


    public static void onScrollPlayVideo(AbsListView absListView, int firstVisiblePosition, int lastVisiblePosition) {
        for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
            View ad = absListView.getChildAt(i - firstVisiblePosition);
            View view = ad.findViewById(R.id.player);
            if (view != null && view instanceof JZVideoPlayerStandard) {
                JZVideoPlayerStandard player = (JZVideoPlayerStandard) view;
                Log.d(TAG, "onScrollStateChanged: " + JZUtils.getViewVisiblePercent(player));
                if (JZMediaManager.instance().positionInList != i && getViewVisiblePercent(player) == 1f) {
                    player.startButton.performClick();
                    break;
                }
            }
        }
    }

    public static void onScrollPlayVideo(RecyclerView recyclerView, int firstVisiblePosition, int lastVisiblePosition) {
//        if (JZMediaManager.isWiFi) {
        if (true) {
            Log.d(TAG, "onScrollPlayVideo: first:" + firstVisiblePosition);
            Log.d(TAG, "onScrollPlayVideo: last:" + lastVisiblePosition);
            Log.d(TAG, "onScrollPlayVideo: current:" + JZMediaManager.instance().positionInList);
            for (int i = 0; i <= lastVisiblePosition - firstVisiblePosition; i++) {
                View child = recyclerView.getChildAt(i);
                View view = child.findViewById(R.id.player);
                if (view != null && view instanceof JZVideoPlayerStandard) {
                   JZVideoPlayerStandard player = (JZVideoPlayerStandard) view;
                    if (getViewVisiblePercent(player) == 1f) {
                        if (JZMediaManager.instance().positionInList != i + firstVisiblePosition) {
                            player.startButton.performClick();
                        }
                        break;
                    }
                }
            }
        }
    }

    public static void onScrollReleaseAllVideos(AbsListView view, int firstVisiblePosition, int visibleItemCount, int totalItemCount) {
        onScrollReleaseAllVideos(firstVisiblePosition, firstVisiblePosition + visibleItemCount,0);
    }

    public static void onScrollReleaseAllVideos(int firstVisiblePosition, int lastVisiblePosition,float percent) {
        int currentPlayPosition = JZMediaManager.instance().positionInList;
        Log.d(TAG, "onScrollReleaseAllVideos: current:" + currentPlayPosition);
        Log.d(TAG, "onScrollReleaseAllVideos: first:" + firstVisiblePosition);
        Log.d(TAG, "onScrollReleaseAllVideos: last:" + lastVisiblePosition);
        if (currentPlayPosition >= 0) {
            if ((currentPlayPosition <= firstVisiblePosition || currentPlayPosition >= lastVisiblePosition - 1)) {
                Log.d(TAG, "onScrollReleaseAllVideos: percent:" + getViewVisiblePercent(JZVideoPlayerManager.getCurrentJzvd()));
                if (getViewVisiblePercent(JZVideoPlayerManager.getCurrentJzvd()) < percent) {
                    JZVideoPlayer.releaseAllVideos();
                }
            }
        }
    }
}
