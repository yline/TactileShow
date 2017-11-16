package com.yline.record.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.TextureView;

import com.yline.record.module.player.MediaPlayerManager;

public class MyVideoView extends TextureView {
    private Uri mUri;

    private MediaPlayerManager mMediaPlayerManager;

    public MyVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initVideoView();
    }

    public MyVideoView(Context context) {
        super(context);
        initVideoView();
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVideoView();
    }

    protected void initVideoView() {
        mMediaPlayerManager = new MediaPlayerManager();
        setSurfaceTextureListener(new SurfaceTextureListener() {
            private boolean isInit = true;

            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                if (isInit) {
                    isInit = false;

                    mMediaPlayerManager.initMediaPlayer(getContext(), surface);
                    mMediaPlayerManager.prepareAsync(getContext(), mUri);
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                // 画布失效
                isInit = true;
                mMediaPlayerManager.release();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });
    }

    public boolean setVideoPath(String path) {
        if (!TextUtils.isEmpty(path)) {
            mUri = Uri.parse(path);
            return true;
        }
        return false;
    }

    public void start() {
        //可用状态{Prepared, Started, Paused, PlaybackCompleted}
        boolean isStarted = mMediaPlayerManager.start();
        if (!isStarted) {
            mMediaPlayerManager.prepareAsync(getContext(), mUri);
        }
    }

    public void pause() {
        //可用状态{Started, Paused}
        boolean isPaused = mMediaPlayerManager.pause();
        if (!isPaused) {
            mMediaPlayerManager.prepareAsync(getContext(), mUri);
        }
    }

    public void stop() {
        boolean isStoped = mMediaPlayerManager.stop();
        if (!isStoped) {
            mMediaPlayerManager.prepareAsync(getContext(), mUri);
        }
    }

    public void setVolume(float volume) {
        //可用状态{Idle, Initialized, Stopped, Prepared, Started, Paused, PlaybackCompleted}
        mMediaPlayerManager.setVolume(volume);
    }

    public void setLooping(boolean looping) {
        //可用状态{Idle, Initialized, Stopped, Prepared, Started, Paused, PlaybackCompleted}
        mMediaPlayerManager.setLooping(looping);
    }

    public void seekTo(int msec) {
        // 可用状态{Prepared, Started, Paused, PlaybackCompleted}
        mMediaPlayerManager.seekTo(msec);
    }

    /**
     * 获取当前播放位置
     */
    public int getCurrentPosition() {
        //可用状态{Idle, Initialized, Prepared, Started, Paused, Stopped, PlaybackCompleted}
        return mMediaPlayerManager.getCurrentPosition();
    }

    private boolean isPlaying() {
        // 可用状态{Idle, Initialized, Prepared, Started, Paused, Stopped, PlaybackCompleted}
        return mMediaPlayerManager.isPlaying();
    }

    /**
     * 是否可用
     */
    public boolean isPrepared() {
        return !mMediaPlayerManager.isEmpty();
    }

    private static final int HANDLER_MESSAGE_PARSE = 0;
    private static final int HANDLER_MESSAGE_LOOP = 1;

    private Handler mVideoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_MESSAGE_PARSE:
                    pause();
                    break;
                case HANDLER_MESSAGE_LOOP:
                    if (isPlaying()) {
                        seekTo(msg.arg1);
                        sendMessageDelayed(mVideoHandler.obtainMessage(HANDLER_MESSAGE_LOOP, msg.arg1, msg.arg2), msg.arg2);
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 定时暂停
     */
    public void pauseDelayed(int delayMillis) {
        if (mVideoHandler.hasMessages(HANDLER_MESSAGE_PARSE)) {
            mVideoHandler.removeMessages(HANDLER_MESSAGE_PARSE);
        }
        mVideoHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_PARSE, delayMillis);
    }

    /**
     * 暂停并且清除定时任务
     */
    public void pauseClearDelayed() {
        pause();
        if (mVideoHandler.hasMessages(HANDLER_MESSAGE_PARSE)) {
            mVideoHandler.removeMessages(HANDLER_MESSAGE_PARSE);
        }
        if (mVideoHandler.hasMessages(HANDLER_MESSAGE_LOOP)) {
            mVideoHandler.removeMessages(HANDLER_MESSAGE_LOOP);
        }
    }

    /**
     * 区域内循环播放
     */
    public void loopDelayed(int startTime, int endTime) {
        int delayMillis = endTime - startTime;
        seekTo(startTime);
        if (!isPlaying()) {
            start();
        }
        if (mVideoHandler.hasMessages(HANDLER_MESSAGE_LOOP)) {
            mVideoHandler.removeMessages(HANDLER_MESSAGE_LOOP);
        }
        mVideoHandler.sendMessageDelayed(mVideoHandler.obtainMessage(HANDLER_MESSAGE_LOOP, getCurrentPosition(), delayMillis), delayMillis);
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener) {
        mMediaPlayerManager.setOnCompletionListener(onCompletionListener);
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener onPreparedListener) {
        mMediaPlayerManager.setOnPreparedListener(onPreparedListener);
    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener onErrorListener) {
        mMediaPlayerManager.setOnErrorListener(onErrorListener);
    }

    public void setOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener onSeekCompleteListener) {
        mMediaPlayerManager.setOnSeekCompleteListener(onSeekCompleteListener);
    }
}
