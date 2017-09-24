package com.tactileshow.view.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class TabBodyDialogHelper {
    private AlertDialog dialog;

    public TabBodyDialogHelper(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("该区域无电子皮肤");

        builder.setNegativeButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
    }

    public void show() {
        if (null != dialog && !dialog.isShowing()) {
            dialog.show();
        }
    }
}
