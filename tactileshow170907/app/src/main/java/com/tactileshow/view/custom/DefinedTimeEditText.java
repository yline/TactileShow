package com.tactileshow.view.custom;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import java.util.Locale;

/**
 * 小时+分钟，选择时间
 *
 * @author yline 2017/9/28 -- 10:06
 * @version 1.0.0
 */
public class DefinedTimeEditText extends android.support.v7.widget.AppCompatEditText {
    private static final String FormatOfTime = "%02d-%02d";

    private AlertDialog alertDialog;

    public DefinedTimeEditText(Context context) {
        super(context);
        initView(context);
    }

    public DefinedTimeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DefinedTimeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private OnTimeSelectCallback onTimeSelectCallback;

    private void initView(Context context) {
        this.setFocusable(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("时间设定");

        final TimePicker timePicker = new TimePicker(context);
        builder.setView(timePicker);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (null != onTimeSelectCallback) {
                    int hour = timePicker.getCurrentHour();
                    int minute = timePicker.getCurrentMinute();
                    setText(String.format(Locale.CHINA, FormatOfTime, hour, minute));

                    onTimeSelectCallback.onSelected(hour, minute);
                }
            }
        });
        alertDialog = builder.create();

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != alertDialog && !alertDialog.isShowing()) {
                    alertDialog.show();
                }
            }
        });
    }

    public void setText(int hour, int minute){
        setText(String.format(Locale.CHINA, FormatOfTime, hour, minute));
    }

    public void setOnTimeSelectCallback(OnTimeSelectCallback onTimeSelectCallback) {
        this.onTimeSelectCallback = onTimeSelectCallback;
    }

    public interface OnTimeSelectCallback {
        /**
         * 用户选择的时间
         *
         * @param hour   小时
         * @param minute 分钟
         */
        void onSelected(int hour, int minute);
    }
}
