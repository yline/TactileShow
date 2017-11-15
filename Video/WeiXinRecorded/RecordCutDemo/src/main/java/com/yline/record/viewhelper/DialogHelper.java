package com.yline.record.viewhelper;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yline.record.R;

/**
 * Dialog 弹框
 *
 * @author yline 2017/11/14 -- 11:46
 * @version 1.0.0
 */
public class DialogHelper {
    private AlertDialog mProgressDialog;
    private TextView mHintTextView;

    public DialogHelper(Context context) {
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ProgressBar loadingProgressBar = view.findViewById(R.id.dialog_loading);
            loadingProgressBar.setIndeterminateTintList(ContextCompat.getColorStateList(context, R.color.dialog_pro_color));
        }

        mHintTextView = view.findViewById(R.id.dialog_tv_hint);
        mHintTextView.setText("视频编译中");

        mProgressDialog = builder.create();
    }

    public void setText(String content) {
        if (null != mHintTextView) {
            mHintTextView.setText(content);
        }
    }

    public void show() {
        if (null != mProgressDialog && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public void dismiss() {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
