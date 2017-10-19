package com.yline.sockpuppet;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.yline.test.BaseTestActivity;
import com.yline.utils.UIScreenUtil;

public class MainActivity extends BaseTestActivity {
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowLayoutParams;
    private TextView mTextView;

    @Override
    public void testStart(View view, Bundle savedInstanceState) {
        addButton("添加一个", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initWindowManager();
                initWindowLayoutParam();
                initTextView();

                if (null == mTextView.getParent()) {
                    mWindowManager.addView(mTextView, mWindowLayoutParams);
                }
            }
        });
    }

    private void initTextView() {
        if (null == mTextView) {
            mTextView = new TextView(this);
            mTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100));
            mTextView.setText("100高度");
            mTextView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light));
        }
    }

    private void initWindowManager() {
        if (null == mWindowManager) {
            this.mWindowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        }
    }

    private void initWindowLayoutParam() {
        if (null == mWindowLayoutParams) {
            mWindowLayoutParams = new WindowManager.LayoutParams();
            mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            mWindowLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
            mWindowLayoutParams.format = PixelFormat.RGBA_8888;
            mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            mWindowLayoutParams.alpha = 1.0F;

            mWindowLayoutParams.x = UIScreenUtil.getScreenWidth(this) - UIScreenUtil.dp2px(this, 75);
            mWindowLayoutParams.y = UIScreenUtil.getScreenHeight(this) / 2;
        }
    }


}
