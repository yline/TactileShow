package com.yline.record;

import android.app.Application;

import com.video.lib.FfmpegManager;

import java.io.File;

/**
 * 入口
 * @author yline 2017/11/14 -- 11:29
 * @version 1.0.0
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
