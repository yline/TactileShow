package com.record.module.record.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 按下的过程中的 进度条
 *
 * @author yline 2017/11/8 -- 20:44
 * @version 1.0.0
 */
public class RecordProgressView extends View {
    public final static int MaxTime = 10_000; // 最大录屏时间
    public final static int MinTime = 2_000; // 最小录屏时间

    public final static int EnoughTimeColor = Color.GREEN; // 时间充裕时，显示颜色
    public final static int LowTimeColor = 0xFFFC2828; // 时间不足时，显示颜色

    private Paint mPaint;
    private RecordState mState;
    private long startTime;

    public RecordProgressView(Context context) {
        this(context, null);
    }

    public RecordProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(LowTimeColor);

        mState = RecordState.PAUSE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        long currentTime = System.currentTimeMillis();
        if (mState == RecordState.START) {
            int measuredWidth = getMeasuredWidth();

            float leftPosition;
            float secondWidth = measuredWidth / 2.0f / MaxTime;
            float diffTime = (currentTime - startTime);

            if (diffTime >= MinTime) {
                mPaint.setColor(EnoughTimeColor);
            }

            leftPosition = secondWidth * diffTime;

            if (leftPosition < measuredWidth / 2.0f) {
                canvas.drawRect(leftPosition, 0.0f, measuredWidth - leftPosition, getMeasuredHeight(), mPaint);
                invalidate();
            }
        }
    }

    public void startAnimation() {
        if (mState != RecordState.START) {
            mState = RecordState.START;
            this.startTime = System.currentTimeMillis();
            invalidate();

            mPaint.setColor(LowTimeColor);
            setVisibility(VISIBLE);
        }
    }

    public void stopAnimation() {
        if (mState != RecordState.PAUSE) {
            mState = RecordState.PAUSE;
            setVisibility(INVISIBLE);
        }
    }

    private enum RecordState {
        START, PAUSE;
    }
}
