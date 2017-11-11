package com.record.module.record.view;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

/**
 * 提示信息判断，帮助类
 *
 * @author yline 2017/11/9 -- 11:25
 * @version 1.0.0
 */
public class HintTextHelper {
    private TextView mHintTextView;

    public HintTextHelper(TextView hintTextView) {
        this.mHintTextView = hintTextView;
    }

    public void startRecord(){
        mHintTextView.setText("上滑取消录制");
        mHintTextView.setTextColor(Color.WHITE);
        mHintTextView.setBackgroundColor(Color.TRANSPARENT);
        mHintTextView.setVisibility(View.VISIBLE);
    }

    public void setVisibility(int visibility){
        mHintTextView.setVisibility(visibility);
    }

    public void releaseRecord(){
        mHintTextView.setTextColor(Color.WHITE);
        mHintTextView.setBackgroundColor(Color.RED);
        mHintTextView.setVisibility(View.VISIBLE);
        mHintTextView.setText("释放取消录制");
    }

    public void slideCancelRecord(){
        mHintTextView.setText("上滑取消录制");
        mHintTextView.setTextColor(Color.WHITE);
        mHintTextView.setBackgroundColor(Color.TRANSPARENT);
        mHintTextView.setVisibility(View.VISIBLE);
    }

    public void recordTooShort(){
        mHintTextView.setText("录制时间太短");
        mHintTextView.setVisibility(View.VISIBLE);
        mHintTextView.setTextColor(Color.WHITE);
        mHintTextView.setBackgroundColor(Color.RED);
    }
}
