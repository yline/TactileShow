package com.yline.record.view;

import android.app.Activity;
import android.support.v4.util.SparseArrayCompat;
import android.view.View;
import android.widget.TextView;

/**
 * ListView ViewHolder
 * 除了google自带的api, 不加入任何其他方法
 *
 * @author yline 2017/9/4 -- 15:03
 * @version 1.0.0
 */
public class AbstractViewHolder {
    private SparseArrayCompat<View> sArray;

    private View sView;

    public AbstractViewHolder(View view) {
        this.sView = view;
        sArray = new SparseArrayCompat<>();
    }

    public AbstractViewHolder(Activity activity) {
        this.sView = activity.getWindow().getDecorView();
        this.sArray = new SparseArrayCompat<>();
    }

    public <T extends View> T get(int viewId) {
        if (sArray.get(viewId) == null) {
            View view = sView.findViewById(viewId);
            sArray.put(viewId, view);
        }
        return (T) sArray.get(viewId);
    }

    public TextView setText(int viewId, String content) {
        TextView textView = this.get(viewId);
        textView.setText(content);
        return textView;
    }

    public void setVisibility(int viewId, int visibility) {
        get(viewId).setVisibility(visibility);
    }

    public void setOnClickListener(int viewId, View.OnClickListener listener) {
        get(viewId).setOnClickListener(listener);
    }

    public void setClickable(int viewId, boolean clickable) {
        get(viewId).setClickable(clickable);
    }
}
