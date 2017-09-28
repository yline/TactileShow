package com.tactileshow.view.custom;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

import java.util.Locale;

/**
 * 年-月-日，选择日期
 *
 * @author yline 2017/9/28 -- 10:26
 * @version 1.0.0
 */
public class DefinedDateEditText extends AppCompatEditText {
    private static final String FormatOfDate = "%04d-%2d-%2d";

    private AlertDialog alertDialog;

    private OnDateSelectCallback onDateSelectCallback;

    public DefinedDateEditText(Context context) {
        super(context);
        initView(context);
    }

    public DefinedDateEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DefinedDateEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.setFocusable(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("日期设定");

        final DatePicker datePicker = new DatePicker(context);
        builder.setView(datePicker);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (null != onDateSelectCallback) {
                    int year = datePicker.getYear();
                    int month = datePicker.getMonth() + 1;
                    int day = datePicker.getDayOfMonth();
                    setText(String.format(Locale.CHINA, FormatOfDate, year, month, day));

                    onDateSelectCallback.onSelected(year, month, day);
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

    public void setText(int year, int month, int day){
        setText(String.format(Locale.CHINA, FormatOfDate, year, month, day));
    }

    public void setOnDateSelectCallback(OnDateSelectCallback onDateSelectCallback) {
        this.onDateSelectCallback = onDateSelectCallback;
    }

    public interface OnDateSelectCallback {
        /**
         * 日期选择
         *
         * @param year  年
         * @param month 月
         * @param day   日
         */
        void onSelected(int year, int month, int day);
    }
}
