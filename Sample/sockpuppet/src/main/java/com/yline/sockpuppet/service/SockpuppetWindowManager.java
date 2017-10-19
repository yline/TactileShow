package com.yline.sockpuppet.service;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;

import com.yline.sockpuppet.activity.IApplication;
import com.yline.utils.UIScreenUtil;

public class SockpuppetWindowManager {

    public SockpuppetWindowManager() {
        WindowManager windowManager = (WindowManager) IApplication.getApplication().getSystemService(Context.WINDOW_SERVICE);

    }

    private int lastX, lastY;

    private void initWindowView(Context context) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        if (0 != lastX && 0 != lastY) {
            layoutParams.x = lastX;
            layoutParams.y = lastY;
        } else {
            layoutParams.x = UIScreenUtil.getScreenWidth(context) - UIScreenUtil.dp2px(context, 75);
            layoutParams.y = UIScreenUtil.getAbsoluteScreenHeight(context) / 2;
        }


    }
}
