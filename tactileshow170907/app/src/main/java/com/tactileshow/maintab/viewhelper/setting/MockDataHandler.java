package com.tactileshow.maintab.viewhelper.setting;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.tactileshow.manager.SQLiteManager;
import com.tactileshow.manager.TactileModel;
import com.yline.log.LogFileUtil;

import java.lang.ref.WeakReference;

/**
 * 定时发送广播，可设置间隔时间、温度等参数
 *
 * @author yline 2017/9/22 -- 9:56
 * @version 1.0.0
 */
public class MockDataHandler extends Handler {
    public static final int Start = -100;
    public static final int Stop = -200;
    public static final int Run = 1;

    private boolean isRunning;

    private WeakReference<Context> mWeakContext;
    private SettingBroadcastDialogHelper mGenDataHelper;

    public MockDataHandler(Context context, SettingBroadcastDialogHelper dialogHelper) {
        this.mWeakContext = new WeakReference<>(context);
        this.mGenDataHelper = dialogHelper;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        int perTime = getPerTime();
        if (msg.what == Start) {
            isRunning = true;
            sendEmptyMessageDelayed(Run, perTime);
        } else if (msg.what == Stop) {
            isRunning = false;
        } else {
            if (isRunning) {
                sendEmptyMessageDelayed(Run, perTime);

                TactileModel tactileModel = getTactileModel();
                if (null != tactileModel && !tactileModel.isDataEmpty()) {
                    if (!tactileModel.isDataEmpty()) {
                        SQLiteManager.getInstance().insert(tactileModel);
                    }
                    TactileModel.sendBroadcast(mWeakContext.get(), tactileModel);
                } else {
                    LogFileUtil.i("TimeHandler", "handleMessage: broadcastModel is null or empty");
                }
            }
        }
    }

    private TactileModel getTactileModel() {
        if (null != mGenDataHelper) {
            return mGenDataHelper.getMockModel();
        }
        return null;
    }

    private int getPerTime() {
        if (null != mGenDataHelper) {
            int perTime = mGenDataHelper.getPer();
            return Math.min(30 * 1000, Math.max(300, perTime));
        }
        return 1000;
    }
}
