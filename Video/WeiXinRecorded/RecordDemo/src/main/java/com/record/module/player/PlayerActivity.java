package com.record.module.player;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.record.R;

/**
 * 就是获取视频路径，进行简单的播放
 *
 * @author yline 2017/11/8 -- 19:40
 * @version 1.0.0
 */
public class PlayerActivity extends Activity {
    private VideoView mVideoView;

    public static void launcher(Context context, String videoPath) {
        if (null != context) {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("path", videoPath);
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mVideoView = (VideoView) findViewById(R.id.player_videoview);
        String videoPath = getIntent().getStringExtra("path");

        startPlay(videoPath);
    }

    private void startPlay(final String path) {
        MediaController mediaController = new MediaController(this);
        mVideoView.setVideoPath(path);

        // 设置VideView与MediaController建立关联
        mVideoView.setMediaController(mediaController);
        // 设置MediaController与VideView建立关联
        mediaController.setMediaPlayer(mVideoView);
        mediaController.setVisibility(View.VISIBLE);

        // 开始播放
        mVideoView.start();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.i("xxx-tag", " width = " + mp.getVideoWidth());
                Log.i("xxx-tag", " height = " + mp.getVideoHeight());
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
    }
}
