package com.tactileshow.view.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.tactileshow.helper.BroadcastHandler;
import com.tactileshow.helper.BroadcastModel;
import com.tactileshow.main.R;
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
public class SettingView {
    private ViewHolder mViewHolder;

    private ExitDialogHelper exitDialogHelper;

    private BroadcastDialogHelper broadcastDialogHelper;

    private ThresholdDialogHelper thresholdDialogHelper;

    private BroadcastHandler mHandler;

    public SettingView(Context context) {
        mHandler = new BroadcastHandler(context);

        View view = LayoutInflater.from(context).inflate(R.layout.view_main_setting, null);
        this.mViewHolder = new ViewHolder(view);

        this.exitDialogHelper = new ExitDialogHelper(context);
        this.broadcastDialogHelper = new BroadcastDialogHelper(context);
        this.thresholdDialogHelper = new ThresholdDialogHelper(context);

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

        // 测试广播
        CheckBox checkBoxBroadcast = mViewHolder.get(R.id.setting_cb_broadcast_toggle);
        checkBoxBroadcast.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (macro.SETTINGS_BCAST) {
                    macro.SETTINGS_BCAST = false;

                    mHandler.sendEmptyMessageAtTime(BroadcastHandler.Stop, 0);

                    SDKManager.toast("测试广播已关闭");
                } else {
                    macro.SETTINGS_BCAST = true;

                    mHandler.sendEmptyMessageAtTime(BroadcastHandler.Start, 100);

                    SDKManager.toast("测试广播已开启");
                }
            }
        });

        // 设定数据广播
        mViewHolder.setOnClickListener(R.id.setting_rl_broadcast, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                broadcastDialogHelper.show();
            }
        });

        // 阈值
        mViewHolder.setOnClickListener(R.id.setting_rl_threshold, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thresholdDialogHelper.show();
            }
        });

        // 退出
        mViewHolder.setOnClickListener(R.id.setting_rl_exit, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialogHelper.show();
            }
        });
    }

    public View getView() {
        return mViewHolder.getItemView();
    }

    public void showExitDialog() {
        if (null != exitDialogHelper) {
            exitDialogHelper.show();
        }
    }

    public void finish() {
        if (null != mHandler) {
            mHandler.sendEmptyMessageAtTime(BroadcastHandler.Stop, 0);
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
                if (null != mHandler) {
                    // temp
                    float temp = broadcastDialogHelper.getMockTemp();
                    float hum = broadcastDialogHelper.getMockHum();
                    LogUtil.i("Mock:" + "temp = " + temp + ", hum = " + hum);
                    BroadcastModel model = new BroadcastModel(System.currentTimeMillis(), temp, hum);

                    mHandler.setBroadcastModel(model);
                }

                try {
                    Thread.sleep(300); // 最小的频率
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* 设定广播信息(mock蓝牙数据) */
    private class BroadcastDialogHelper {
        private Dialog dialog;

        private ViewHolder sViewHolder;

        private boolean isTempRange, isTempAble, isHumRange, isHumAble;

        private float tempMin, tempMax, humMin, humMax;

        private int per;

        private View.OnClickListener listener;

        public BroadcastDialogHelper(Context context) {
            dialog = new Dialog(context);
            View view = LayoutInflater.from(context).inflate(R.layout.view_main_setting_dialog_broadcast, null);
            dialog.setTitle("设定数据广播");
            dialog.setContentView(view);
            sViewHolder = new ViewHolder(view);

            sViewHolder.setOnClickListener(R.id.broadcast_dialog_btn_sure, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != dialog && dialog.isShowing()) {
                        saveData();

                        // 更新发送周期
                        if (mHandler.getPerTime() != per) {
                            mHandler.setPerTime(per * 1000);
                        }

                        dialog.dismiss();
                    }
                }
            });

            initData();
        }

        private void initData() {
            isTempRange = false;
            isTempAble = false;
            isHumRange = false;
            isHumAble = false;

            tempMin = 20;
            tempMax = 50;
            humMin = 0;
            humMax = 100;

            per = 1;
        }

        public void show() {
            if (null != dialog && !dialog.isShowing()) {
                initView();
                dialog.show();
            }
        }

        // 生成 湿度 的模拟数据
        public float getMockHum() {
            if (isHumAble) {
                if (isHumRange) {
                    return (float) (humMin + Math.random() * Math.abs(humMax - humMin + 1));
                } else {
                    return humMin;
                }
            } else {
                return BroadcastModel.Empty;
            }
        }

        // 生成 温度 的模拟数据
        public float getMockTemp() {
            if (isTempAble) {
                if (isTempRange) {
                    return (float) (tempMin + Math.random() * Math.abs(tempMax - tempMin + 1));
                } else {
                    return tempMin;
                }
            } else {
                return BroadcastModel.Empty;
            }
        }

        private void initView() {
            CheckBox checkBoxTempRange = sViewHolder.get(R.id.broadcast_dialog_cb_temp_range);
            checkBoxTempRange.setChecked(isTempRange);
            CheckBox checkBoxTempAble = sViewHolder.get(R.id.broadcast_dialog_cb_temp);
            checkBoxTempAble.setChecked(isTempAble);
            sViewHolder.setText(R.id.broadcast_dialog_et_temp_min, tempMin + "");
            sViewHolder.setText(R.id.broadcast_dialog_et_temp_max, tempMax + "");

            CheckBox checkBoxHumRange = sViewHolder.get(R.id.broadcast_dialog_cb_hum_range);
            checkBoxHumRange.setChecked(isHumRange);
            CheckBox checkBoxHumAble = sViewHolder.get(R.id.broadcast_dialog_cb_hum);
            checkBoxHumAble.setChecked(isHumAble);
            sViewHolder.setText(R.id.broadcast_dialog_et_hum_min, humMin + "");
            sViewHolder.setText(R.id.broadcast_dialog_et_hum_max, humMax + "");

            sViewHolder.setText(R.id.broadcast_dialog_et_per, per + "");
        }

        private void saveData() {
            CheckBox checkBoxTempRange = sViewHolder.get(R.id.broadcast_dialog_cb_temp_range);
            isTempRange = checkBoxTempRange.isChecked();
            CheckBox checkBoxTempAble = sViewHolder.get(R.id.broadcast_dialog_cb_temp);
            isTempAble = checkBoxTempAble.isChecked();
            tempMin = Float.parseFloat(sViewHolder.getText(R.id.broadcast_dialog_et_temp_min));
            tempMax = Float.parseFloat(sViewHolder.getText(R.id.broadcast_dialog_et_temp_max));

            CheckBox checkBoxHumRange = sViewHolder.get(R.id.broadcast_dialog_cb_hum_range);
            isHumRange = checkBoxHumRange.isChecked();
            CheckBox checkBoxHumAble = sViewHolder.get(R.id.broadcast_dialog_cb_hum);
            isHumAble = checkBoxHumAble.isChecked();
            humMin = Float.parseFloat(sViewHolder.getText(R.id.broadcast_dialog_et_hum_min));
            humMax = Float.parseFloat(sViewHolder.getText(R.id.broadcast_dialog_et_hum_max));

            per = Integer.parseInt(sViewHolder.getText(R.id.broadcast_dialog_et_per));
        }
    }

    /* 设置 阈值(全局) */
    private class ThresholdDialogHelper {
        private Dialog dialog;

        private ViewHolder sViewHolder;

        public ThresholdDialogHelper(Context context) {
            dialog = new Dialog(context);
            View view = LayoutInflater.from(context).inflate(R.layout.view_main_setting_dialog_threshold, null);
            dialog.setTitle("设定阈值信息");
            dialog.setContentView(view);
            sViewHolder = new ViewHolder(view);

            sViewHolder.setOnClickListener(R.id.threshold_dialog_sure, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != dialog) {
                        saveData();
                        dialog.dismiss();
                    }
                }
            });
        }

        public void show() {
            if (null != dialog && !dialog.isShowing()) {
                initData();
                dialog.show();
            }
        }

        private void initData() {
            sViewHolder.setText(R.id.threshold_temp_min, macro.SETTING_TEMP_RANGE[0] + "");
            sViewHolder.setText(R.id.threshold_temp_mid, macro.SETTING_TEMP_RANGE[1] + "");
            sViewHolder.setText(R.id.threshold_temp_max, macro.SETTING_TEMP_RANGE[2] + "");

            sViewHolder.setText(R.id.threshold_press_min, macro.SETTING_PRESS_RANGE[0] + "");
            sViewHolder.setText(R.id.threshold_press_mid, macro.SETTING_PRESS_RANGE[1] + "");
            sViewHolder.setText(R.id.threshold_press_max, macro.SETTING_PRESS_RANGE[2] + "");

            sViewHolder.setText(R.id.threshold_germ_min, macro.SETTING_GERM_RANGE[0] + "");
            sViewHolder.setText(R.id.threshold_germ_max, macro.SETTING_GERM_RANGE[1] + "");
        }

        private void saveData() {
            if (null != sViewHolder) {
                macro.SETTING_TEMP_RANGE[0] = Double.parseDouble(sViewHolder.getText(R.id.threshold_temp_min));
                macro.SETTING_TEMP_RANGE[1] = Double.parseDouble(sViewHolder.getText(R.id.threshold_temp_mid));
                macro.SETTING_TEMP_RANGE[2] = Double.parseDouble(sViewHolder.getText(R.id.threshold_temp_max));

                macro.SETTING_PRESS_RANGE[0] = Double.parseDouble(sViewHolder.getText(R.id.threshold_press_min));
                macro.SETTING_PRESS_RANGE[1] = Double.parseDouble(sViewHolder.getText(R.id.threshold_press_mid));
                macro.SETTING_PRESS_RANGE[2] = Double.parseDouble(sViewHolder.getText(R.id.threshold_press_max));

                macro.SETTING_GERM_RANGE[0] = Double.parseDouble(sViewHolder.getText(R.id.threshold_germ_min));
                macro.SETTING_GERM_RANGE[1] = Double.parseDouble(sViewHolder.getText(R.id.threshold_germ_max));
            }
        }
    }

    /* 退出界面弹框 */
    private class ExitDialogHelper {
        private AlertDialog dialog;

        public ExitDialogHelper(final Context context) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            dialogBuilder.setTitle("退出");
            dialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialogBuilder.setPositiveButton("断开链接", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                }
            });
            dialogBuilder.setNeutralButton("直接退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    macro.SETTING_EXIT_DIRECTLY = true;
                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                }
            });

            dialog = dialogBuilder.create();
        }

        public void show() {
            if (null != dialog && !dialog.isShowing()) {
                dialog.show();
            }
        }
    }
}
