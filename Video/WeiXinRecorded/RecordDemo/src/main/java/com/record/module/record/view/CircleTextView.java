package com.record.module.record.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;

/**
 * 按下圆圈
 *
 * @author yline 2017/11/8 -- 20:29
 * @version 1.0.0
 */
public class CircleTextView extends android.support.v7.widget.AppCompatTextView {
    private static final int PaintWidth = 2;
    private static final int CircleWidth = 1;

    private Paint mPaint;
    private RectF mRectF;

    public CircleTextView(Context context) {
        this(context, null, 0);
    }

    public CircleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void startRecordAnimator() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(this, "scaleX", new float[]{1.0F, 1.2F, 1.5F}),
                ObjectAnimator.ofFloat(this, "scaleY", new float[]{1.0F, 1.2F, 1.5F}),
                ObjectAnimator.ofFloat(this, "alpha", new float[]{1.0F, 0.25F, 0.0F})
        );

        animatorSet.setDuration(300L).start();
    }

    public void stopRecordAnimator() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(this, "scaleX", new float[]{1.5F, 1.2F, 1.0F}),
                ObjectAnimator.ofFloat(this, "scaleY", new float[]{1.5F, 1.2F, 1.0F}),
                ObjectAnimator.ofFloat(this, "alpha", new float[]{0.0F, 0.25F, 1.0F})
        );

        animatorSet.setDuration(300L).start();
    }

    private void initView() {
        setGravity(Gravity.CENTER);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(PaintWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.GREEN);

        mRectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mRectF.set(CircleWidth, CircleWidth, canvas.getWidth() - CircleWidth, canvas.getHeight() - CircleWidth);
        canvas.drawOval(mRectF, mPaint);
    }
}
