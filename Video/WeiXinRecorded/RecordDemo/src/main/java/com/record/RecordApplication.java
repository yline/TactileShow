package com.record;

import com.video.lib.FfmpegManager;
import com.yline.application.BaseApplication;

public class RecordApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化拍摄SDK，必须; 测试执行时间 13ms
        FfmpegManager.init(this, "WeChatJuns");
    }
}
