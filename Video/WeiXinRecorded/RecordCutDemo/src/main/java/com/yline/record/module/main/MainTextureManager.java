package com.yline.record.module.main;

import android.view.View;

import com.yline.record.R;
import com.yline.record.view.AbstractViewHolder;

/**
 * 负责 播放部分
 *
 * @author yline 2017/11/17 -- 10:05
 * @version 1.0.0
 */
public class MainTextureManager {
    private AbstractViewHolder mViewHolder;
    private OnMainTextureCallback mTextureCallback;

    public MainTextureManager(View parentView) {
        mViewHolder = new AbstractViewHolder(parentView);

        mViewHolder.setOnClickListener(R.id.main_iv_close, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mTextureCallback) {
                    mTextureCallback.onTextureCloseClick();
                }
            }
        });

        mViewHolder.setOnClickListener(R.id.main_iv_next, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mTextureCallback) {
                    mTextureCallback.onTextureFinishClick();
                }
            }
        });
    }

    public void setRelativeVisibility(int visibility) {
        mViewHolder.setVisibility(R.id.rl_bottom2, visibility);
    }

    public void setTextureVisibility(int visibility) {
        mViewHolder.setVisibility(R.id.vv_play, visibility);
    }

    public void setOnMainTextureCallback(OnMainTextureCallback callback) {
        this.mTextureCallback = callback;
    }

    public interface OnMainTextureCallback {
        /**
         * 点击结束
         */
        void onTextureFinishClick();

        /**
         * 点击关闭
         */
        void onTextureCloseClick();
    }
}
