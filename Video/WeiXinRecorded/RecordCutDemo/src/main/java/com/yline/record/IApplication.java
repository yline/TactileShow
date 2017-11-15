package com.yline.record;

import com.video.lib.FfmpegManager;
import com.yline.application.BaseApplication;

import java.io.File;

/**
 * 入口
 * @author yline 2017/11/14 -- 11:29
 * @version 1.0.0
 */
public class IApplication extends BaseApplication {
    public static String VIDEO_PATH = "/sdcard/WeiXinRecordedDemo/";

    @Override
    public void onCreate() {
        super.onCreate();

        VIDEO_PATH += String.valueOf(System.currentTimeMillis());
        File file = new File(VIDEO_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }

        FfmpegManager.init(this, "SimpleCut");
    }
}
