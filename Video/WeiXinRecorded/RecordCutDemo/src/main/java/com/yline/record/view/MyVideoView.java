package com.yline.record.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.TextureView;

import com.yline.record.module.player.MediaPlayerManager;

import java.io.IOException;

public class MyVideoView extends TextureView {
    private SurfaceTexture mSurfaceHolder = null;

    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_STOP = 5;
    /**
     * PlaybackCompleted状态：文件正常播放完毕，而又没有设置循环播放的话就进入该状态，
     * 并会触发OnCompletionListener的onCompletion
     * ()方法。此时可以调用start()方法重新从头播放文件，也可以stop()停止MediaPlayer，或者也可以seekTo()来重新定位播放位置。
     */
    private static final int STATE_PLAYBACK_COMPLETED = 5;
    /**
     * Released/End状态：通过release()方法可以进入End状态
     */
    private static final int STATE_RELEASED = 5;

    private int mCurrentState = STATE_IDLE;

    private int mDuration;
    private Uri mUri;

    // 监听事件
    private MediaPlayer.OnCompletionListener mOnCompletionListener;
    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private MediaPlayer.OnErrorListener mOnErrorListener;
    private MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener;

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

        mCurrentState = STATE_IDLE;

        //		setFocusable(true);
        //		setFocusableInTouchMode(true);
        //		requestFocus();
        setSurfaceTextureListener(new SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                boolean needReOpen = (mSurfaceHolder == null);
                mSurfaceHolder = surface;

                if (needReOpen) {
                    openVideo(mUri);
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                // 画布失效
                mSurfaceHolder = null;
                release();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });

        mMediaPlayerManager.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //必须是正常状态
                if (mCurrentState == STATE_PREPARING) {
                    mCurrentState = STATE_PREPARED;

                    mDuration = mp.getDuration();

                    if (mOnPreparedListener != null) {
                        mOnPreparedListener.onPrepared(mp);
                    }
                }
            }
        });
        mMediaPlayerManager.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mCurrentState = STATE_PLAYBACK_COMPLETED;
                if (mOnCompletionListener != null) {
                    mOnCompletionListener.onCompletion(mp);
                }
            }
        });
        mMediaPlayerManager.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mCurrentState = STATE_ERROR;
                if (mOnErrorListener != null) {
                    mOnErrorListener.onError(mp, what, extra);
                }

                return true;
            }
        });
        mMediaPlayerManager.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                if (mOnSeekCompleteListener != null) {
                    mOnSeekCompleteListener.onSeekComplete(mp);
                }
            }
        });
    }

    public void setVideoPath(String path) {
        mUri = Uri.parse(path);
    }

    public void start() {
        //可用状态{Prepared, Started, Paused, PlaybackCompleted}
        if ((mCurrentState == STATE_PREPARED || mCurrentState == STATE_PAUSED || mCurrentState == STATE_PLAYING || mCurrentState == STATE_PLAYBACK_COMPLETED)) {
            boolean isStarted = mMediaPlayerManager.start();
            if (isStarted) {
                mCurrentState = STATE_PLAYING;
            } else {
                mCurrentState = STATE_ERROR;
                openVideo(mUri);
            }
        }
    }

    public void pause() {
        //可用状态{Started, Paused}
        if ((mCurrentState == STATE_PLAYING || mCurrentState == STATE_PAUSED)) {
            boolean isPaused = mMediaPlayerManager.pause();
            if (isPaused) {
                mCurrentState = STATE_PAUSED;
            } else {
                mCurrentState = STATE_ERROR;
                openVideo(mUri);
            }
        }
    }

    public void stop() {
        if ((mCurrentState == STATE_PLAYING || mCurrentState == STATE_PAUSED)) {
            boolean isStoped = mMediaPlayerManager.stop();
            if (isStoped) {
                mCurrentState = STATE_STOP;
            } else {
                mCurrentState = STATE_ERROR;
                openVideo(mUri);
            }
        }
    }

    public void setVolume(float volume) {
        //可用状态{Idle, Initialized, Stopped, Prepared, Started, Paused, PlaybackCompleted}
        if ((mCurrentState == STATE_PREPARED || mCurrentState == STATE_PLAYING || mCurrentState == STATE_PAUSED || mCurrentState == STATE_PLAYBACK_COMPLETED)) {
            mMediaPlayerManager.setVolume(volume);
        }
    }

    public void setLooping(boolean looping) {
        //可用状态{Idle, Initialized, Stopped, Prepared, Started, Paused, PlaybackCompleted}
        if ((mCurrentState == STATE_PREPARED || mCurrentState == STATE_PLAYING || mCurrentState == STATE_PAUSED || mCurrentState == STATE_PLAYBACK_COMPLETED)) {
            mMediaPlayerManager.setLooping(looping);
        }
    }

    public void seekTo(int msec) {
        // 可用状态{Prepared, Started, Paused, PlaybackCompleted}
        if ((mCurrentState == STATE_PREPARED || mCurrentState == STATE_PLAYING || mCurrentState == STATE_PAUSED || mCurrentState == STATE_PLAYBACK_COMPLETED)) {
            mMediaPlayerManager.seekTo(msec);
        }
    }

    /**
     * 获取当前播放位置
     */
    public int getCurrentPosition() {
        int position = 0;
        //可用状态{Idle, Initialized, Prepared, Started, Paused, Stopped, PlaybackCompleted}
        if (mCurrentState == STATE_PLAYBACK_COMPLETED) {
            position = mDuration;
        } else if (mCurrentState == STATE_PLAYING || mCurrentState == STATE_PAUSED) {
            return mMediaPlayerManager.getCurrentPosition();
        }
        return position;
    }

    private boolean isPlaying() {
        // 可用状态{Idle, Initialized, Prepared, Started, Paused, Stopped, PlaybackCompleted}
        return mMediaPlayerManager.isPlaying() && (mCurrentState == STATE_PLAYING);
    }

    /**
     * 调用release方法以后MediaPlayer无法再恢复使用
     */
    public void release() {
        mCurrentState = STATE_RELEASED;
        mMediaPlayerManager.release();
    }

    public void openVideo(Uri uri) {
        if (uri == null || mSurfaceHolder == null || getContext() == null) {
            // not ready for playback just yet, will try again later
            if (mSurfaceHolder == null && uri != null) {
                mUri = uri;
            }
            return;
        }

        mUri = uri;
        mDuration = 0;

        initMediaPlayer(uri);
    }

    private void initMediaPlayer(Uri uri) {
        mMediaPlayerManager.initMediaPlayer(getContext(), mSurfaceHolder);

        try {
            mMediaPlayerManager.prepareAsync(getContext(), mUri);

            // we don't set the target state here either, but preserve the target state that was there before.
            mCurrentState = STATE_PREPARING;
        } catch (IOException ex) {
            ex.printStackTrace();
            mCurrentState = STATE_ERROR;
            if (mOnErrorListener != null) {
                mOnErrorListener.onError(mMediaPlayerManager.getMediaPlayer(), MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            }
        }
    }

    /**
     * 是否可用
     */
    public boolean isPrepared() {
        return !mMediaPlayerManager.isEmpty() && (mCurrentState == STATE_PREPARED);
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
        this.mOnCompletionListener = onCompletionListener;
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener onPreparedListener) {
        this.mOnPreparedListener = onPreparedListener;
    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener onErrorListener) {
        this.mOnErrorListener = onErrorListener;
    }

    public void setOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener onSeekCompleteListener) {
        this.mOnSeekCompleteListener = onSeekCompleteListener;
    }
}
