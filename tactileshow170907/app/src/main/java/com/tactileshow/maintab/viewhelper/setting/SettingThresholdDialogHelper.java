package com.tactileshow.maintab.viewhelper.setting;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.tactileshow.main.R;
import com.tactileshow.util.macro;
import com.yline.view.recycler.holder.ViewHolder;

/**
 * 设置 阈值(全局)
 * @author yline 2017/11/7 -- 23:52
 * @version 1.0.0
 */
public class SettingThresholdDialogHelper {
    private Dialog dialog;

    private ViewHolder sViewHolder;

    public SettingThresholdDialogHelper(Context context) {
        dialog = new Dialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.view_tab_setting_dialog_threshold, null);
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
