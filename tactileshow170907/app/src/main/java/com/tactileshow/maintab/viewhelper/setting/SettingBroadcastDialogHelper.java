package com.tactileshow.maintab.viewhelper.setting;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.tactileshow.main.R;
import com.tactileshow.manager.TactileModel;
import com.yline.view.recycler.holder.ViewHolder;

/**
 * 设定广播信息(mock蓝牙数据)
 * @author yline 2017/11/7 -- 23:34
 * @version 1.0.0
 */
public class SettingBroadcastDialogHelper {
    private Dialog dialog;
    private ViewHolder sViewHolder;

    private boolean isTempRange, isTempAble, isHumRange, isHumAble, isHeaderRange, isHeaderAble;
    private float tempMin, tempMax, humMin, humMax, headerMin, headerMax;

    private int per;
    private OnPerSureClickListener onPerSureClickListener;

    public SettingBroadcastDialogHelper(Context context) {
        dialog = new Dialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.view_tab_setting_dialog_broadcast, null);
        dialog.setTitle("设定数据广播");
        dialog.setContentView(view);
        sViewHolder = new ViewHolder(view);

        sViewHolder.setOnClickListener(R.id.broadcast_dialog_btn_sure, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != dialog && dialog.isShowing()) {
                    saveData();

                    if (null != onPerSureClickListener){
                        onPerSureClickListener.onSureClick(per);
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
        isHeaderRange = false;
        isHeaderAble = false;

        tempMin = 20;
        tempMax = 50;
        humMin = 0;
        humMax = 100;
        headerMin = 20;
        headerMax = 50;

        per = 1;
    }

    public void setOnPerSureClickListener(OnPerSureClickListener onPerSureClickListener) {
        this.onPerSureClickListener = onPerSureClickListener;
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
            return TactileModel.Empty;
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
            return TactileModel.Empty;
        }
    }

    // 生成 第三个渠道 的模拟数据
    public float getMockHeader() {
        if (isHeaderAble) {
            if (isHeaderRange) {
                return (float) (headerMin + Math.random() * Math.abs(headerMax - headerMin + 1));
            } else {
                return headerMin;
            }
        } else {
            return TactileModel.Empty;
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

        CheckBox checkBoxHeaderRange = sViewHolder.get(R.id.broadcast_dialog_cb_header_range);
        checkBoxHeaderRange.setChecked(isHeaderRange);
        CheckBox checkBoxHeaderAble = sViewHolder.get(R.id.broadcast_dialog_cb_header);
        checkBoxHeaderAble.setChecked(isHeaderAble);
        sViewHolder.setText(R.id.broadcast_dialog_et_header_min, headerMin + "");
        sViewHolder.setText(R.id.broadcast_dialog_et_header_max, headerMax + "");

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

        CheckBox checkBoxHeaderRange = sViewHolder.get(R.id.broadcast_dialog_cb_header_range);
        isHeaderRange = checkBoxHeaderRange.isChecked();
        CheckBox checkBoxHeaderAble = sViewHolder.get(R.id.broadcast_dialog_cb_header);
        isHeaderAble = checkBoxHeaderAble.isChecked();
        headerMin = Float.parseFloat(sViewHolder.getText(R.id.broadcast_dialog_et_header_min));
        headerMax = Float.parseFloat(sViewHolder.getText(R.id.broadcast_dialog_et_header_max));

        per = Integer.parseInt(sViewHolder.getText(R.id.broadcast_dialog_et_per));
    }

    public interface OnPerSureClickListener{
        /**
         * 点击确定时，响应
         * @param per 设置的间隔周期; unit: s
         */
        void onSureClick(int per);
    }
}
