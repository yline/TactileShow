package com.yline.record.module.player;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Surface;

import com.video.lib.FfmpegManager;

import java.io.IOException;

/**
 * 媒体播放器帮助类
 *
 * @author yline 2017/11/15 -- 9:54
 * @version 1.0.0
 */
public class MediaPlayerManager {
    private MediaPlayer mMediaPlayer;

    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private MediaPlayer.OnCompletionListener mOnCompletionListener;
    private MediaPlayer.OnErrorListener mOnErrorListener;
    private MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener;

    public MediaPlayerManager() {
    }

    /**
     * 初始化MediaPlayer
     * Idle 状态：当使用new()方法创建一个MediaPlayer对象或者调用了其reset()方法时，该MediaPlayer对象处于idle状态。
     * End 状态：通过release()方法可以进入End状态，只要MediaPlayer对象不再被使用，就应当尽快将其通过release()方法释放掉
     * Initialized 状态：这个状态比较简单，MediaPlayer调用setDataSource()方法就进入Initialized状态，表示此时要播放的文件已经设置好了。
     * Prepared 状态：初始化完成之后还需要通过调用prepare()或prepareAsync()方法，这两个方法一个是同步的一个是异步的，只有进入Prepared状态，才表明MediaPlayer到目前为止都没有错误，可以进行文件播放。
     */
    public void initMediaPlayer(Context context, SurfaceTexture surfaceTexture) {
        if (null == mMediaPlayer) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    FfmpegManager.v("initMediaPlayer", "onPrepared");

                    if (null != mOnPreparedListener) {
                        mOnPreparedListener.onPrepared(mp);
                    }
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    FfmpegManager.v("initMediaPlayer", "onCompletion");

                    if (null != mOnCompletionListener) {
                        mOnCompletionListener.onCompletion(mp);
                    }
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    FfmpegManager.v("initMediaPlayer", "setOnErrorListener");

                    return (null != mOnErrorListener) && mOnErrorListener.onError(mp, what, extra);
                }
            });
            mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    FfmpegManager.v("initMediaPlayer", "onSeekComplete");
                    if (null != mOnSeekCompleteListener) {
                        mOnSeekCompleteListener.onSeekComplete(mp);
                    }
                }
            });
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            int volume = 0;
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (null != audioManager) {
                volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            }
            mMediaPlayer.setVolume(volume, volume);
            mMediaPlayer.setSurface(new Surface(surfaceTexture));
        } else {
            mMediaPlayer.reset();
        }
    }

    /**
     * 准备加载视频
     *
     * @param context 环境变量
     * @param uri     视频路径
     */
    public void prepareAsync(Context context, Uri uri) {
        if (null != mMediaPlayer && null != uri) {
            try {
                mMediaPlayer.setDataSource(context, uri);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
                // we don't set the target state here either, but preserve the target state that was there before.
                if (null != mOnErrorListener) {
                    mOnErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, MediaPlayer.MEDIA_ERROR_IO);
                }
            }
        }
    }

    public boolean start() {
        if (null != mMediaPlayer) {
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
            }
            return true;
        }
        return false;
    }

    public boolean pause() {
        if (null != mMediaPlayer) {
            mMediaPlayer.pause();
            return true;
        }
        return false;
    }

    public boolean stop() {
        if (null != mMediaPlayer) {
            mMediaPlayer.stop();
            return true;
        }
        return false;
    }

    public void setVolume(float volume) {
        if (null != mMediaPlayer) {
            mMediaPlayer.setVolume(volume, volume);
        }
    }

    public void setLooping(boolean looping) {
        if (null != mMediaPlayer) {
            mMediaPlayer.setLooping(looping);
        }
    }

    public void seekTo(int msec) {
        if (null != mMediaPlayer) {
            mMediaPlayer.seekTo(Math.max(0, msec));
        }
    }

    public int getCurrentPosition() {
        if (null != mMediaPlayer) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public boolean isPlaying() {
        if (null != mMediaPlayer) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    public boolean isEmpty() {
        return (null == mMediaPlayer);
    }

    /**
     * 彻底释放，MediaPlayer
     */
    public void release() {
        if (null != mMediaPlayer) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener onPreparedListener) {
        this.mOnPreparedListener = onPreparedListener;
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener) {
        this.mOnCompletionListener = onCompletionListener;
    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener onErrorListener) {
        this.mOnErrorListener = onErrorListener;
    }

    public void setOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener onSeekCompleteListener) {
        this.mOnSeekCompleteListener = onSeekCompleteListener;
    }
}
