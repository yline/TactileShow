package com.tactileshow.maintab.viewhelper;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.tactileshow.manager.TactileModel;
import com.tactileshow.util.macro;
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

    private static final int Run = 1;

    private boolean isRunning;

    private int perTime;

    private WeakReference<Context> sWeakContext;

    private TactileModel broadcastModel;

    public MockDataHandler(Context context) {

        this.sWeakContext = new WeakReference<>(context);
        this.perTime = 1000;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        LogFileUtil.i("TimeHandler", "msg.what = " + msg.what);

        if (msg.what == Start) {
            isRunning = true;
            sendEmptyMessageDelayed(Run, perTime);
        } else if (msg.what == Stop) {
            isRunning = false;
        } else {
            if (isRunning) {
                sendEmptyMessageDelayed(Run, perTime);

                if (null != broadcastModel && !broadcastModel.isDataEmpty()) {
                    String actionMsg = TactileModel.toJson(broadcastModel);
                    LogFileUtil.i("TimeHandler", "handleMessage: actionMsg = " + actionMsg);

                    Intent broadIntent = new Intent(macro.BROADCAST_ADDRESS);
                    broadIntent.putExtra("msg", actionMsg);
                    sWeakContext.get().sendBroadcast(broadIntent);

                    broadcastModel = null;
                } else {
                    LogFileUtil.i("TimeHandler", "handleMessage: broadcastModel is null or empty");
                }
            }
        }
    }

    public void setBroadcastModel(TactileModel model) {
        this.broadcastModel = model;
    }

    public int getPerTime() {
        return perTime;
    }

    public void setPerTime(int perTime) {
        if (perTime < 50) {
            this.perTime = 1000;
            return;
        }

        if (perTime > 30 * 1000) {
            this.perTime = 10 * 1000;
            return;
        }

        this.perTime = perTime;
    }
}
