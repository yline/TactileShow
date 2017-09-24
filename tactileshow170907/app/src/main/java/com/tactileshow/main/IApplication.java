package com.tactileshow.main;

import com.tactileshow.helper.DataFileUtil;
import com.yline.application.BaseApplication;

/**
 * 工程入口
 *
 * @author yline 2017/9/15 -- 8:55
 * @version 1.0.0
 */
public class IApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        DataFileUtil.init(this);
    }
}
