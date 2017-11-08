package com.tactileshow.maintab.viewhelper.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.tactileshow.main.R;
import com.tactileshow.manager.SQLiteManager;
import com.tactileshow.manager.TactileModel;
import com.tactileshow.util.macro;
import com.yline.application.SDKManager;
import com.yline.utils.LogUtil;
import com.yline.view.recycler.holder.ViewHolder;

/**
 * 设置界面
 *
 * @author yline 2017/9/21 -- 20:07
 * @version 1.0.0
 */
public class SettingViewHelper {
    private ViewHolder mViewHolder;

    private SettingExitDialogHelper mExitDialogHelper;
    private SettingBroadcastDialogHelper mBroadcastDialogHelper;
    private SettingThresholdDialogHelper mThresholdDialogHelper;

    private MockDataHandler mHandler;

    public SettingViewHelper(Context context) {
        mHandler = new MockDataHandler(context);

        View view = LayoutInflater.from(context).inflate(R.layout.view_tab_setting, null);
        this.mViewHolder = new ViewHolder(view);

        this.mExitDialogHelper = new SettingExitDialogHelper(context);
        this.mBroadcastDialogHelper = new SettingBroadcastDialogHelper(context);
        this.mThresholdDialogHelper = new SettingThresholdDialogHelper(context);

        initViewClick();

        // 开启模拟数据
        MockThread mockThread = new MockThread();
        mockThread.start();
    }

    private void initViewClick() {
        // 声音
        CheckBox checkBoxSound = mViewHolder.get(R.id.setting_cb_sound);
        checkBoxSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (macro.SETTINGS_SOUND) {
                    macro.SETTINGS_SOUND = false;
                    SDKManager.toast("声音提醒已关闭");
                } else {
                    macro.SETTINGS_SOUND = true;
                    SDKManager.toast("声音提醒已开启");
                }
            }
        });

        // 震动
        CheckBox checkBoxVibrate = mViewHolder.get(R.id.setting_cb_vibrate);
        checkBoxVibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (macro.SETTINGS_VIBRA) {
                    macro.SETTINGS_VIBRA = false;
                    SDKManager.toast("震动提醒已关闭");
                } else {
                    macro.SETTINGS_VIBRA = true;
                    SDKManager.toast("震动提醒已开启");
                }
            }
        });

        // 更改数据周期
        mBroadcastDialogHelper.setOnPerSureClickListener(new SettingBroadcastDialogHelper.OnPerSureClickListener() {
            @Override
            public void onSureClick(int per) {
                // 更新发送周期
                if (mHandler.getPerTime() != per) {
                    mHandler.setPerTime(per * 1000);
                }
            }
        });

        // 测试广播
        CheckBox checkBoxBroadcast = mViewHolder.get(R.id.setting_cb_broadcast_toggle);
        checkBoxBroadcast.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (macro.SETTINGS_BCAST) {
                    macro.SETTINGS_BCAST = false;

                    mHandler.sendEmptyMessageAtTime(MockDataHandler.Stop, 0);

                    SDKManager.toast("测试广播已关闭");
                } else {
                    macro.SETTINGS_BCAST = true;

                    mHandler.sendEmptyMessageAtTime(MockDataHandler.Start, 100);

                    SDKManager.toast("测试广播已开启");
                }
            }
        });

        // 设定数据广播
        mViewHolder.setOnClickListener(R.id.setting_rl_broadcast, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBroadcastDialogHelper.show();
            }
        });

        // 阈值
        mViewHolder.setOnClickListener(R.id.setting_rl_threshold, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThresholdDialogHelper.show();
            }
        });

        // 数据条数
        mViewHolder.setText(R.id.setting_tv_data_count, SQLiteManager.getInstance().count() + "");
        mViewHolder.setOnClickListener(R.id.setting_rl_data, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewHolder.setText(R.id.setting_tv_data_count, SQLiteManager.getInstance().count() + "");
            }
        });

        // 退出
        mViewHolder.setOnClickListener(R.id.setting_rl_exit, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExitDialogHelper.show();
            }
        });
    }

    public View getView() {
        return mViewHolder.getItemView();
    }

    public void showExitDialog() {
        if (null != mExitDialogHelper) {
            mExitDialogHelper.show();
        }
    }

    public void finish() {
        if (null != mHandler) {
            mHandler.sendEmptyMessageAtTime(MockDataHandler.Stop, 0);
            mHandler.removeCallbacks(null);
            mHandler = null;
        }
    }

    /* 模拟数据源，不停的发送信息 */
    private class MockThread extends Thread {
        @Override
        public void run() {
            super.run();

            while (true) {
                if (null != mHandler && macro.SETTINGS_BCAST) {
                    // temp
                    float temp = mBroadcastDialogHelper.getMockTemp();
                    float hum = mBroadcastDialogHelper.getMockHum();
                    float header = mBroadcastDialogHelper.getMockHeader();

                    LogUtil.i("Mock:" + "temp = " + temp + ", hum = " + hum);
                    TactileModel model = new TactileModel(System.currentTimeMillis(), hum, temp, header);

                    mHandler.setBroadcastModel(model);
                    if (!model.isDataEmpty()) {
                        SQLiteManager.getInstance().insert(model);
                    }
                }

                try {
                    Thread.sleep(800); // 最小的频率
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
