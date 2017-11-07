package com.tactileshow.maintab.viewhelper.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.tactileshow.util.macro;

/**
 * 退出界面弹框
 * @author yline 2017/11/7 -- 23:32
 * @version 1.0.0
 */
public class SettingExitDialogHelper {
    private AlertDialog mDialog;

    public SettingExitDialogHelper(final Context context) {
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

        mDialog = dialogBuilder.create();
    }

    public void show() {
        if (null != mDialog && !mDialog.isShowing()) {
            mDialog.show();
        }
    }
}
