package com.zzh12138.jzvideodemo.player;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zzh12138.jzvideodemo.CenterImageSpan;
import com.zzh12138.jzvideodemo.R;
import com.zzh12138.jzvideodemo.view.VoiceAnimationView;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by zhangzhihao on 2018/7/23 18:31.
 */

public class JZVideoPlayerStandard extends JZVideoPlayer {

    private static final String TAG = "VideoPlayerStandard";

    /**
     * 标题
     */
    public TextView titleTextView;
    /**
     * 封面图
     */
    public ImageView coverImageView;
    /**
     * 返回按钮
     */
    private ImageView backImageView;
    /**
     * 加载中
     */
    private ProgressBar loadingProgressBar;
    /**
     * 封面图上的视频总时间
     */
    private TextView coverTotalTimeTextView;
    /**
     * 非wifi提示布局
     */
    private LinearLayout consumeLayout;
    /**
     * 继续播放
     */
    private TextView continuePlayTextView;
    /**
     * 分享布局
     */
    private FrameLayout shareLayout;
    /**
     * 分享图标
     */
    private ImageView weChat, moment, qq, weibo;

    /**
     * 重播文字
     */
    private TextView replayTextView;

    /**
     * 错误提示布局
     */
    private LinearLayout errorLayout;
    /**
     * 出错重试
     */
    private TextView errorReplayText;

    /**
     * 静音文字提示
     */
    private TextView muteTextView;

    private static Timer dismissViewTimer;
    private DismissControlViewTimerTask mDismissControlViewTimerTask;
    /**
     * 是否视频列表
     * 点击会出现控制器
     */
    private boolean isVideoList;

    private VoiceAnimationView voiceAnimateView;
    private LinearLayout voiceLayout;
    private ObjectAnimator animator;
    private Drawable muteDrawable, enMuteDrawable;
    private boolean isShowedMuteTip;

    public static boolean videoActivityIsMute = true;
    /**
     * 是否显示了下一条提示文字
     */
    private boolean isShowWillPlayNextTip;
    private OnVideoPlayClickListener onVideoPlayClickListener;
    private OnVideoTimeChangeListener onVideoTimeChangeListener;
    private OnVideoPlayerContainerClickListener onVideoPlayerContainerClickListener;
    private OnADKnowMoreClickListener onADKnowMoreClickListener;
    private OnBackImageClickListener onBackImageClickListener;
    private OnVideoReplayClickListener onVideoReplayClickListener;
    private OnVideoClickListener onVideoClickListener;
    private long duration;
    /**
     * 是否竖视频
     */
    private boolean isVerticalVideo;


    public JZVideoPlayerStandard(Context context) {
        super(context);
    }

    public JZVideoPlayerStandard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_player_standard;
    }

    @Override
    public void init(Context context) {
        super.init(context);
        titleTextView = findViewById(R.id.title);
        coverImageView = findViewById(R.id.cover);
        backImageView = findViewById(R.id.back);
        loadingProgressBar = findViewById(R.id.loading);
        coverTotalTimeTextView = findViewById(R.id.time);
        consumeLayout = findViewById(R.id.consume_layout);
        continuePlayTextView = findViewById(R.id.continue_play);
        shareLayout = findViewById(R.id.share_layout);
        weChat = findViewById(R.id.share_weChat);
        moment = findViewById(R.id.share_moment);
        qq = findViewById(R.id.share_qq);
        weibo = findViewById(R.id.share_weibo);
        errorLayout = findViewById(R.id.error_layout);
        mute = findViewById(R.id.mute);
        voiceAnimateView = findViewById(R.id.voice_view);
        voiceLayout = findViewById(R.id.voice_layout);
        muteTextView = findViewById(R.id.mute_text);
        muteDrawable = context.getResources().getDrawable(R.drawable.icon_player_mute);
        muteDrawable.setBounds(0, 0, muteDrawable.getIntrinsicWidth(), muteDrawable.getIntrinsicHeight());
        enMuteDrawable = context.getResources().getDrawable(R.drawable.icon_player_not_mute);
        enMuteDrawable.setBounds(0, 0, enMuteDrawable.getIntrinsicWidth(), enMuteDrawable.getIntrinsicHeight());
        SpannableString str = new SpannableString("  重播");
        str.setSpan(new CenterImageSpan(getContext(), R.drawable.icon_player_replay), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        replayTextView = findViewById(R.id.replay_text);
        replayTextView.setText(str);
        errorReplayText = findViewById(R.id.error_replay_text);

        animator = ObjectAnimator.ofFloat(voiceAnimateView, "percent", 0, 1f);
        animator.setDuration(350);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);

        coverImageView.setOnClickListener(this);
        backImageView.setOnClickListener(this);
        continuePlayTextView.setOnClickListener(this);
        weChat.setOnClickListener(this);
        moment.setOnClickListener(this);
        qq.setOnClickListener(this);
        weibo.setOnClickListener(this);
        replayTextView.setOnClickListener(this);
        mute.setOnClickListener(this);
        errorReplayText.setOnClickListener(this);
        muteTextView.setOnClickListener(this);
        //宽高默认为16:9
        widthRatio = 16;
        heightRatio = 9;
    }

    @Override
    public void setUp(Object[] dataSourceObjects, int defaultUrlMapIndex, int screen, Object... objects) {
        super.setUp(dataSourceObjects, defaultUrlMapIndex, screen, objects);
        // object[0] 标题 object[1] 来源 object[2] articleBean object[3] 广告id
        if (objects.length != 0) {
            titleTextView.setText(objects[0].toString());
        }
        switch (currentScreen) {
            case SCREEN_WINDOW_FULLSCREEN:
                //全屏
                fullscreenButton.setVisibility(View.GONE);
                backImageView.setVisibility(View.VISIBLE);
                break;
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                //非全屏
                fullscreenButton.setImageResource(R.drawable.icon_player_full_screen);
                backImageView.setVisibility(View.GONE);
                break;
            case SCREEN_WINDOW_RECYCLE_VERTICAL_FULLSCREEN:
                fullscreenButton.setVisibility(View.GONE);
            default:
                break;
        }

    }

    /**
     * 普通状态
     */
    @Override
    public void onStateNormal() {
        super.onStateNormal();
        Log.d(TAG, "onStateNormal: ");
        changeUiToNormal();
    }

    /**
     * 加载中
     */
    @Override
    public void onStatePreparing() {
        super.onStatePreparing();
        Log.d(TAG, "onStatePreparing: ");
        changeUiToPreparing();
    }

    @Override
    public void onStatePreparingChangingUrl(int urlMapIndex, long seekToInAdvance) {
        super.onStatePreparingChangingUrl(urlMapIndex, seekToInAdvance);
        Log.d(TAG, "onStatePreparingChangingUrl: ");
        if (objects.length != 0) {
            titleTextView.setText(objects[0].toString());
        }
        loadingProgressBar.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.GONE);
        textureViewContainer.setVisibility(View.GONE);
        coverImageView.setVisibility(GONE);
        coverTotalTimeTextView.setVisibility(GONE);
    }

    /**
     * 开始播放
     */
    @Override
    public void onStatePlaying() {
        super.onStatePlaying();
        Log.d(TAG, "onStatePlaying: ");
        changeUiToPlayingClear();
        if (textureViewContainer.getVisibility() == View.GONE) {
            textureViewContainer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 暂停
     */
    @Override
    public void onStatePause() {
        super.onStatePause();
        Log.d(TAG, "onStatePause: ");
        if (!isVideoList) {
            animator.cancel();
        }
        changeUiToPauseShow();
        cancelDismissControlViewTimer();
        if (!WIFI_TIP_DIALOG_SHOWED) {
            showWifiDialog();
        }
    }

    @Override
    public void onStatePlaybackBufferingStart() {
        super.onStatePlaybackBufferingStart();
        loadingProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * 播放出错
     */
    @Override
    public void onStateError() {
        super.onStateError();
        Log.d(TAG, "onStateError: ");
        JZUtils.saveProgress(getContext(), JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex), getCurrentPositionWhenPlaying());
        if (!isVideoList) {
            animator.cancel();
        }
        changeUiToError();
    }

    /**
     * 播放完毕
     */
    @Override
    public void onStateAutoComplete() {
        super.onStateAutoComplete();
        Log.d(TAG, "onStateAutoComplete: ");
        animator.cancel();
        changeUiToComplete();
        cancelDismissControlViewTimer();
        if (onVideoCompleteListener != null) {
            onVideoCompleteListener.onVideoPlayComplete();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN || isVideoList) {
            int id = v.getId();
            Log.d(TAG, "onTouch: ");
            if (id == R.id.surface_container) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        startDismissControlViewTimer();
                        if (!mChangePosition && !mChangeVolume) {
                            onClickUiToggle();
                        }
                        break;
                    default:
                        break;
                }
            } else if (id == R.id.bottom_seek_progress) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        cancelDismissControlViewTimer();
                        break;
                    case MotionEvent.ACTION_UP:
                        startDismissControlViewTimer();
                        break;
                    default:
                        break;
                }
            }
        }
        return super.onTouch(v, event);

    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ");
        switch (v.getId()) {
            case R.id.start:
                if (dataSourceObjects == null || JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex) == null) {
                    return;
                }
                if (currentState == CURRENT_STATE_NORMAL) {
                    if (onVideoPlayClickListener != null) {
                        onVideoPlayClickListener.videoPlayClick();
                    }
                    if (!JZMediaManager.isWiFi && onVideoClickListener != null) {
                        onVideoClickListener.onVideoClick();
                        return;
                    }
                    if (!JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex).toString().startsWith("file") && !
                            JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex).toString().startsWith("/") &&
                            !JZUtils.isWifiConnected(getContext()) && !WIFI_TIP_DIALOG_SHOWED) {
                        showWifiDialog();
                        return;
                    }
                    startVideo();
                    onEvent(JZUserAction.ON_CLICK_START_ICON);
                } else if (currentState == CURRENT_STATE_PLAYING) {
                    onEvent(JZUserAction.ON_CLICK_PAUSE);
                    Log.d(TAG, "pauseVideo [" + this.hashCode() + "] ");
                    JZMediaManager.pause();
                    onStatePause();
                } else if (currentState == CURRENT_STATE_PAUSE) {
                    onEvent(JZUserAction.ON_CLICK_RESUME);
                    JZMediaManager.start();
                    onStatePlaying();
                } else if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
                    onEvent(JZUserAction.ON_CLICK_START_AUTO_COMPLETE);
                    startVideo();
                }
                break;
            case R.id.fullscreen:
                if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
                    return;
                }
                if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
                    //quit fullscreen
                    backPress();
                } else {
                    onEvent(JZUserAction.ON_ENTER_FULLSCREEN);
                    startWindowFullscreen();
                }
                break;
            case R.id.cover:
                //封面图点击事件
                if (dataSourceObjects == null || JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex) == null) {
                    return;
                }
                if (currentState == CURRENT_STATE_NORMAL) {
                    if (onVideoPlayClickListener != null) {
                        onVideoPlayClickListener.videoPlayClick();
                    }
                    if (onVideoClickListener != null) {
                        onVideoClickListener.onVideoClick();
                        return;
                    }
                    if (!JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex).toString().startsWith("file") &&
                            !JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex).toString().startsWith("/")
                            && !JZUtils.isWifiConnected(getContext()) && !WIFI_TIP_DIALOG_SHOWED) {
                        //非wifi环境切播放的不是本地文件
                        showWifiDialog();
                    } else {
                        startVideo();
                    }
                } else if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
                    //播放完成后点击封面图
                }
                break;
            case R.id.back:
                if (currentScreen == SCREEN_WINDOW_RECYCLE_VERTICAL_FULLSCREEN) {
                    if (onBackImageClickListener != null) {
                        onBackImageClickListener.onBackImageClick();
                    }
                } else {
                    backPress();
                }
                break;
            case R.id.continue_play:
                consumeLayout.setVisibility(GONE);
                if (currentState == CURRENT_STATE_PAUSE) {
                    onEvent(JZUserAction.ON_CLICK_RESUME);
                    JZMediaManager.start();
                    onStatePlaying();
                } else {
                    startVideo();
                }
                WIFI_TIP_DIALOG_SHOWED = true;
                break;
            case R.id.share_weChat:
                break;
            case R.id.share_moment:
                break;
            case R.id.share_qq:
                break;
            case R.id.share_weibo:
                break;
            case R.id.replay_text:
                //重播
                if (!isVideoList) {
                    if (onVideoReplayClickListener != null) {
                        onVideoReplayClickListener.onVideoReplayClick();
                    }
                } else {
                    onEvent(JZUserAction.ON_CLICK_START_AUTO_COMPLETE);
                    startVideo();
                }
                break;
            case R.id.surface_container:
                //播放器点击事件
                if (!isVideoList && currentState != CURRENT_STATE_AUTO_COMPLETE) {
                    if (onVideoPlayerContainerClickListener != null) {
                        onVideoPlayerContainerClickListener.videoPlayerContainerClick();
                    }
                }
                break;
            case R.id.mute:
                //静音图标点击事件
                boolean flag = isVideoActivity ? videoActivityIsMute : isMute;
                if (flag) {
                    JZMediaManager.instance().jzMediaInterface.setVolume(1f, 1f);
                    mute.setImageResource(R.drawable.icon_player_not_mute);
                } else {
                    JZMediaManager.instance().jzMediaInterface.setVolume(0f, 0f);
                    mute.setImageResource(R.drawable.icon_player_mute);
                }
                if (isVideoActivity) {
                    videoActivityIsMute = !videoActivityIsMute;
                } else {
                    isMute = !isMute;
                }
                break;
            case R.id.error_replay_text:
                initTextureView();
                addTextureView();
                JZMediaManager.setDataSource(dataSourceObjects);
                JZMediaManager.setCurrentDataSource(JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex));
                isShowedMuteTip = false;
                onStatePreparing();
                onEvent(JZUserAction.ON_CLICK_START_ERROR);
                break;
            case R.id.mute_text:
                //静音提示文字点击事件
                boolean f = isVideoActivity ? videoActivityIsMute : isMute;
                if (f) {
                    JZMediaManager.instance().jzMediaInterface.setVolume(1f, 1f);
                    muteTextView.setCompoundDrawables(enMuteDrawable, null, null, null);
                    muteTextView.setText("");
                    mute.setImageResource(R.drawable.icon_player_not_mute);
                } else {
                    JZMediaManager.instance().jzMediaInterface.setVolume(0f, 0f);
                    muteTextView.setCompoundDrawables(muteDrawable, null, null, null);
                    mute.setImageResource(R.drawable.icon_player_mute);
                }
                if (isVideoActivity) {
                    videoActivityIsMute = !videoActivityIsMute;
                } else {
                    isMute = !isMute;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onPrepared() {
        boolean flag = isVideoActivity ? videoActivityIsMute : isMute;
        if (flag) {
            JZMediaManager.instance().jzMediaInterface.setVolume(0f, 0f);
        } else {
            JZMediaManager.instance().jzMediaInterface.setVolume(1f, 1f);
        }
        super.onPrepared();
    }

    @Override
    public void startVideo() {
        super.startVideo();
    }

    /**
     * 显示流量提示
     */
    @Override
    public void showWifiDialog() {
        JZVideoPlayerManager.completeAll();
        consumeLayout.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.GONE);
        voiceLayout.setVisibility(View.GONE);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        super.onStartTrackingTouch(seekBar);
        Log.d(TAG, "onStartTrackingTouch: ");
        if (currentScreen == SCREEN_WINDOW_RECYCLE_VERTICAL_FULLSCREEN) {
            return;
        }
        cancelDismissControlViewTimer();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        super.onStopTrackingTouch(seekBar);
        Log.d(TAG, "onStopTrackingTouch: ");
        if (currentScreen == SCREEN_WINDOW_RECYCLE_VERTICAL_FULLSCREEN) {
            return;
        }
        if (currentState == CURRENT_STATE_PLAYING) {
            dismissControlView();
        } else {
            startDismissControlViewTimer();
        }
    }

    public void onClickUiToggle() {
        Log.d(TAG, "onClickUiToggle: ");
        Log.d(TAG, "onClickUiToggle: " + bottomContainer);
        switch (currentState) {
            case CURRENT_STATE_PREPARING:
                changeUiToPreparing();
                break;
            case CURRENT_STATE_PLAYING:
                if (bottomContainer.getVisibility() == View.VISIBLE) {
                    changeUiToPlayingClear();
                } else {
                    changeUiToPlayingShow();
                    bottomContainer.requestLayout();
                    bottomContainer.setVisibility(VISIBLE);
                }
                break;
            case CURRENT_STATE_PAUSE:
                if (bottomContainer.getVisibility() == View.VISIBLE) {
                    changeUiToPauseClear();
                } else {
                    changeUiToPauseShow();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void setProgressAndText(int progress, long position, long duration) {
        super.setProgressAndText(progress, position, duration);
        long del = duration - position;
        if (coverTotalTimeTextView.getVisibility() == View.VISIBLE) {
            coverTotalTimeTextView.setText(JZUtils.stringForTime(del));
        }
        if (isVideoList && currentScreen == SCREEN_WINDOW_NORMAL && del >= 0 && del <= 5000 && !isShowWillPlayNextTip && onVideoTimeChangeListener != null) {
            onVideoTimeChangeListener.showWillPlayNextTip();
            isShowWillPlayNextTip = true;
        }
    }

    public void changeUiToNormal() {
        Log.d(TAG, "changeUiToNormal: ");
        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                setControlViewsVisibility(View.GONE, View.GONE, View.VISIBLE, View.GONE, View.VISIBLE, View.GONE, View.GONE, View.GONE, isVideoList ? View.GONE : View.VISIBLE);
                updateStartImage();
                voiceAnimateView.setVisibility(View.GONE);
                coverTotalTimeTextView.setText(JZUtils.stringForTime(duration * 1000));
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setControlViewsVisibility(View.VISIBLE, View.GONE, View.VISIBLE, View.GONE, View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_RECYCLE_VERTICAL_FULLSCREEN:
                setControlViewsVisibility(View.VISIBLE, View.GONE, View.GONE, View.GONE, View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);
                break;
            default:
                break;
        }
    }

    public void changeUiToPreparing() {
        Log.d(TAG, "changeUiToPreparing: ");
        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                setControlViewsVisibility(View.GONE, View.GONE, View.GONE, View.VISIBLE, View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);
                updateStartImage();
                if (!isVideoList) {
                    voiceAnimateView.setVisibility(View.VISIBLE);
                }
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setControlViewsVisibility(View.GONE, View.GONE, View.GONE, View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_RECYCLE_VERTICAL_FULLSCREEN:
                setControlViewsVisibility(View.VISIBLE, View.GONE, View.GONE, View.VISIBLE, View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);
                break;
            default:
                break;
        }
        boolean flag = isVideoActivity ? videoActivityIsMute : isMute;
        if (flag) {
            mute.setImageResource(R.drawable.icon_player_mute);
        } else {
            mute.setImageResource(R.drawable.icon_player_not_mute);
        }
    }

    public void changeUiToPlayingShow() {
        Log.d(TAG, "changeUiToPlayingShow: ");
        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                bottomContainer.clearAnimation();
                setControlViewsVisibility(View.GONE, View.VISIBLE, View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, isVideoList ? View.GONE : View.VISIBLE);
                updateStartImage();
                if (isShowedMuteTip) {
                    muteTextView.setVisibility(View.GONE);
                }
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setControlViewsVisibility(View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_RECYCLE_VERTICAL_FULLSCREEN:
                setControlViewsVisibility(View.VISIBLE, View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE);
                break;
            default:
                break;
        }
    }

    public void changeUiToPlayingClear() {
        Log.d(TAG, "changeUiToPlayingClear: ");
        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                setControlViewsVisibility(View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, isVideoList ? View.GONE : View.VISIBLE);
                if (!isVideoList) {
                    animator.start();
                }
                if (isVideoList && !isShowedMuteTip) {
                    muteTextView.setVisibility(View.VISIBLE);
                    if (isVideoActivity && !videoActivityIsMute) {
                        muteTextView.setCompoundDrawables(enMuteDrawable, null, null, null);
                        muteTextView.setText("");
                        muteTextView.setBackground(null);
                    } else {
                        muteTextView.setCompoundDrawables(muteDrawable, null, null, null);
                        muteTextView.setBackgroundResource(R.drawable.bg_mute_text);
                        muteTextView.setText("点击开启声音");
                        muteTextView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (muteTextView != null) {
                                    muteTextView.setText("");
                                    muteTextView.setBackground(null);
                                }
                            }
                        }, 3000);
                    }
                    isShowedMuteTip = true;
                }
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setControlViewsVisibility(View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE);
                break;
            case SCREEN_WINDOW_RECYCLE_VERTICAL_FULLSCREEN:
                setControlViewsVisibility(View.VISIBLE, View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE);
                break;
            default:
                break;
        }
    }

    public void changeUiToPauseShow() {
        Log.d(TAG, "changeUiToPauseShow: ");
        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                setControlViewsVisibility(View.GONE, isVideoActivity ? View.VISIBLE : View.GONE, View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setControlViewsVisibility(View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE);
                updateStartImage();
                break;
            default:
                break;
        }
    }

    public void changeUiToPauseClear() {
        Log.d(TAG, "changeUiToPauseClear: ");
        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                setControlViewsVisibility(View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setControlViewsVisibility(View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE);
                break;
            default:
                break;
        }
    }

    public void changeUiToComplete() {
        Log.d(TAG, "changeUiToComplete: ");
        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                setControlViewsVisibility(View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, isVideoList ? View.GONE : View.VISIBLE, View.GONE, View.GONE);
                updateStartImage();
                muteTextView.setVisibility(View.GONE);
                voiceLayout.setVisibility(View.GONE);
                if (isVideoList) {
                    setState(CURRENT_STATE_NORMAL);
                }
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setControlViewsVisibility(View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_RECYCLE_VERTICAL_FULLSCREEN:
                setControlViewsVisibility(View.VISIBLE, View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE);
                break;
            default:
                break;
        }
    }

    public void changeUiToError() {
        Log.d(TAG, "changeUiToError: ");
        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                setControlViewsVisibility(View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.VISIBLE, View.GONE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setControlViewsVisibility(View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.VISIBLE, View.GONE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_RECYCLE_VERTICAL_FULLSCREEN:
                setControlViewsVisibility(View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.VISIBLE, View.GONE);
                updateStartImage();
                break;
            default:
                break;
        }
    }

    public void setControlViewsVisibility(int topLayoutVisibility, int bottomLayoutVisibility, int startBtnVisibility,
                                          int loadingProgressVisibility, int coverImageVisibility, int consumeLayoutVisibility
            , int shareLayoutVisibility, int errorLayoutVisibility, int voiceLayoutVisibility) {
        topContainer.setVisibility(topLayoutVisibility);
        bottomContainer.setVisibility(bottomLayoutVisibility);
        startButton.setVisibility(startBtnVisibility);
        loadingProgressBar.setVisibility(loadingProgressVisibility);
        coverImageView.setVisibility(coverImageVisibility);
        consumeLayout.setVisibility(consumeLayoutVisibility);
        shareLayout.setVisibility(shareLayoutVisibility);
        errorLayout.setVisibility(errorLayoutVisibility);
        voiceLayout.setVisibility(voiceLayoutVisibility);
        if (voiceLayoutVisibility == GONE) {
            animator.cancel();
        }
    }

    public void updateStartImage() {
        switch (currentState) {
            case CURRENT_STATE_PLAYING:
                startButton.setVisibility(View.VISIBLE);
                startButton.setImageResource(R.drawable.icon_player_pause);
                break;
            case CURRENT_STATE_ERROR:
                startButton.setVisibility(View.GONE);
                break;
            default:
                startButton.setImageResource(R.drawable.icon_player_play);
                break;
        }
    }

    public void startDismissControlViewTimer() {
        cancelDismissControlViewTimer();
        dismissViewTimer = new Timer();
        mDismissControlViewTimerTask = new DismissControlViewTimerTask();
        dismissViewTimer.schedule(mDismissControlViewTimerTask, 2500);
    }

    public void cancelDismissControlViewTimer() {
        if (dismissViewTimer != null) {
            dismissViewTimer.cancel();
        }
        if (mDismissControlViewTimerTask != null) {
            mDismissControlViewTimerTask.cancel();
        }
    }

    /**
     * 播放完毕
     */
    @Override
    public void onAutoCompletion() {
        Log.d(TAG, "onAutoCompletion: ");
        if (currentScreen == SCREEN_WINDOW_RECYCLE_VERTICAL_FULLSCREEN) {
            JZUtils.saveProgress(getContext(), JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex), 0);
            onStateAutoComplete();
        } else if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            JZUtils.saveProgress(getContext(), JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex), 0);
            onStateAutoComplete();
        } else {
            super.onAutoCompletion();
        }
    }

    /**
     * 释放播放器
     */
    @Override
    public void onCompletion() {
        super.onCompletion();
        cancelDismissControlViewTimer();
        muteTextView.setVisibility(View.GONE);
        isShowedMuteTip = false;
        isShowWillPlayNextTip = false;
        if (!isVideoList) {
            animator.cancel();
            isMute = true;
        }
    }

    public void hideAnimationView() {
        voiceLayout.setVisibility(GONE);
        animator.cancel();
        muteTextView.setVisibility(VISIBLE);
        muteTextView.setCompoundDrawables(muteDrawable, null, null, null);
        muteTextView.setText("");
        muteTextView.setBackground(null);
        isShowedMuteTip = true;
    }

    public void showAnimationView() {
        voiceLayout.setVisibility(VISIBLE);
        animator.start();
        muteTextView.setVisibility(GONE);
        JZMediaManager.instance().jzMediaInterface.setVolume(0f, 0f);
        isShowedMuteTip = false;
    }

    public void dismissControlView() {
        if (currentState != CURRENT_STATE_NORMAL &&
                currentState != CURRENT_STATE_ERROR &&
                currentState != CURRENT_STATE_AUTO_COMPLETE) {
            post(new Runnable() {
                @Override
                public void run() {
                    bottomContainer.setVisibility(View.GONE);
                    topContainer.setVisibility(View.GONE);
                    startButton.setVisibility(View.GONE);
                }
            });
        }
    }


    public JZVideoPlayerStandard setVideoList(boolean flag) {
        isVideoList = flag;
        return this;
    }

    public class DismissControlViewTimerTask extends TimerTask {
        @Override
        public void run() {
            dismissControlView();
        }
    }


    public JZVideoPlayerStandard setVerticalVideo(boolean verticalVideo) {
        isVerticalVideo = verticalVideo;
        return this;
    }

    public interface OnVideoPlayClickListener {
        void videoPlayClick();
    }

    public interface OnVideoTimeChangeListener {
        void showWillPlayNextTip();
    }

    /**
     * 播放中点击播放器回调接口
     */
    public interface OnVideoPlayerContainerClickListener {
        void videoPlayerContainerClick();
    }

    public interface OnADKnowMoreClickListener {
        void adKnowMoreClick();
    }

    public interface OnBackImageClickListener {
        void onBackImageClick();
    }


    public interface OnVideoReplayClickListener {
        void onVideoReplayClick();
    }

    /**
     * 非wifi下点击视频回调
     */
    public interface OnVideoClickListener {
        void onVideoClick();
    }


    public JZVideoPlayerStandard setOnVideoCompleteListener(OnVideoCompleteListener onVideoCompleteListener) {
        this.onVideoCompleteListener = onVideoCompleteListener;
        return this;
    }

    public JZVideoPlayerStandard setOnVideoPlayClickListener(OnVideoPlayClickListener onVideoPlayClickListener) {
        this.onVideoPlayClickListener = onVideoPlayClickListener;
        return this;
    }

    public JZVideoPlayerStandard setOnVideoTimeChangeListener(OnVideoTimeChangeListener onVideoTimeChangeListener) {
        this.onVideoTimeChangeListener = onVideoTimeChangeListener;
        return this;
    }

    public JZVideoPlayerStandard setOnVideoPlayerContainerClickListener(OnVideoPlayerContainerClickListener onVideoPlayerContainerClickListener) {
        this.onVideoPlayerContainerClickListener = onVideoPlayerContainerClickListener;
        return this;
    }

    public JZVideoPlayerStandard setOnADKnowMoreClickListener(OnADKnowMoreClickListener onADKnowMoreClickListener) {
        this.onADKnowMoreClickListener = onADKnowMoreClickListener;
        return this;
    }

    public JZVideoPlayerStandard setOnBackImageClickListener(OnBackImageClickListener onBackImageClickListener) {
        this.onBackImageClickListener = onBackImageClickListener;
        return this;
    }

    public JZVideoPlayerStandard setOnVideoReplayClickListener(OnVideoReplayClickListener onVideoReplayClickListener) {
        this.onVideoReplayClickListener = onVideoReplayClickListener;
        return this;
    }

    public JZVideoPlayerStandard setOnVideoClickListener(OnVideoClickListener onVideoClickListener) {
        this.onVideoClickListener = onVideoClickListener;
        return this;
    }

    public JZVideoPlayerStandard setVideoTime(long time) {
        duration = time;
        coverTotalTimeTextView.setText(JZUtils.stringForTime(duration * 1000));
        return this;
    }

    public JZVideoPlayerStandard setVideoTitle(String title) {
        titleTextView.setText(title);
        return this;
    }

    public JZVideoPlayerStandard setVideoActivity(boolean videoActivity) {
        isVideoActivity = videoActivity;
        return this;
    }
}
