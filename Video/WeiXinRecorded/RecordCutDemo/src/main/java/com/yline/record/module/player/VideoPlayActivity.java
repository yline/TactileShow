package com.yline.record.module.player;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.WindowManager;

import com.video.lib.manager.MediaRecorderBase;
import com.yline.base.BaseActivity;
import com.yline.record.R;
import com.yline.record.view.MyVideoView;
import com.yline.utils.LogUtil;

/**
 * 播放界面
 *
 * @author yline 2017/11/14 -- 12:01
 * @version 1.0.0
 */
public class VideoPlayActivity extends BaseActivity {
    private static final String KEY_VIDEO_PATH = "path";

    private MyVideoView vv_play;

    public static void launcher(Context context, String path) {
        if (null != context) {
            Intent intent = new Intent(context, VideoPlayActivity.class);
            intent.putExtra(KEY_VIDEO_PATH, path);
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_play);

        vv_play = findViewById(R.id.video_play_view);

        Intent intent = getIntent();
        String path = intent.getStringExtra(KEY_VIDEO_PATH);

        vv_play.setVideoPath(path);
        vv_play.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                LogUtil.v("width = " + mp.getVideoWidth() + ", height = " + mp.getVideoHeight() + ", videoWidth = " + MediaRecorderBase.VIDEO_WIDTH + ", videoHeight = " + MediaRecorderBase.VIDEO_HEIGHT);

                vv_play.setLooping(true);
                vv_play.start();
            }
        });
    }
}
