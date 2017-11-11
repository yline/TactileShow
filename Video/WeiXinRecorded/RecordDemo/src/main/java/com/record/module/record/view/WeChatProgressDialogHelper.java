package com.record.module.record.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Window;

/**
 * 处理过程 dialog
 *
 * @author yline 2017/11/9 -- 10:02
 * @version 1.0.0
 */
public class WeChatProgressDialogHelper {
    private ProgressDialog mProgressDialog;

    public WeChatProgressDialogHelper(Context context) {
        if (null == mProgressDialog) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCanceledOnTouchOutside(false);// 不能取消
            mProgressDialog.setIndeterminate(true);// 设置进度条是否不明确
        }
    }

    public void show(String title, String message) {
        if (null != mProgressDialog && !mProgressDialog.isShowing()) {
            if (!TextUtils.isEmpty(title)) {
                mProgressDialog.setTitle(title);
            }
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }
    }

    public void dismiss() {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
