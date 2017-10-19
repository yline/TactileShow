package com.yline.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yline.R;
import com.yline.utils.UIScreenUtil;

public class SliderBarLayout extends RelativeLayout implements SliderBarView.OnLetterTouchedListener {
    /**
     * 显示时间
     */
    private static final int TEXTVIEW_DISAPPEAR_TIME = 1000;

    private SliderBarView mSliderBarView;

    private TextView mTextView;

    private SliderBarView.OnLetterTouchedListener onLetterTouchedListener;

    public SliderBarLayout(Context context) {
        this(context, null);
    }

    public SliderBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.view_slider_bar, this, true);
        mSliderBarView = findViewById(R.id.slider_bar);
        ViewGroup.LayoutParams layoutParams = mSliderBarView.getLayoutParams();
        layoutParams.height = UIScreenUtil.getScreenHeight(context) * 2 / 3;
        mSliderBarView.setLayoutParams(layoutParams);
        mSliderBarView.setOnLetterTouchedListener(this);

        mTextView = findViewById(R.id.slider_bar_tv);
    }

    public void setOnLetterTouchedListener(SliderBarView.OnLetterTouchedListener onLetterTouchedListener) {
        this.onLetterTouchedListener = onLetterTouchedListener;
    }

    public void setLetter(String[] letter) {
        this.mSliderBarView.setLetter(letter);
    }

    @Override
    public void onTouched(int position, String str) {
        mTextView.setText(str);
        mTextView.setVisibility(View.VISIBLE);
        mTextView.removeCallbacks(runnable); // 解决闪烁的问题
        mTextView.postDelayed(runnable, TEXTVIEW_DISAPPEAR_TIME);

        if (null != onLetterTouchedListener) {
            onLetterTouchedListener.onTouched(position, str);
        }
    }

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            mTextView.setVisibility(View.GONE);
        }
    };
}
