package com.yline.record;

import android.app.Application;

import com.video.lib.FfmpegManager;

import java.io.File;

/**
 * Created by zhaoshuang on 17/2/8.
 */

public class IApplication extends Application {
    public static String VIDEO_PATH = "/sdcard/WeiXinRecordedDemo/";

    @Override
    public void onCreate() {

        VIDEO_PATH += String.valueOf(System.currentTimeMillis());
        File file = new File(VIDEO_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }

        FfmpegManager.init(this, "SimpleCut");
    }
}