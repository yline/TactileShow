package com.yline.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;

import com.yline.utils.UIScreenUtil;
import com.yline.view.recycler.decoration.CommonLinearDecoration;
import com.yline.view.recycler.holder.Callback;

public class LetterLinearItemDecoration extends CommonLinearDecoration {
    private static final int TextSize = 13; // dp
    private static final int LetterHeight = 25; // dp

    private SparseArray<String> keys;
    private Context sContext;

    private Paint mTextPaint;
    private Paint mBackgroundPaint;

    public LetterLinearItemDecoration(Context context) {
        super(context);

        this.sContext = context;
        this.keys = new SparseArray<>();

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        initTextPaint(mTextPaint);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        initBackgroundPaint(mBackgroundPaint);
    }

    private void initTextPaint(Paint textPaint) {
        textPaint.setTextSize(UIScreenUtil.dp2px(sContext, TextSize));
        textPaint.setColor(0xff999999);
    }

    private void initBackgroundPaint(Paint backgroundPaint) {
        backgroundPaint.setColor(0xFFF7F7F7);
    }

    @Override
    protected void setVerticalOffsets(Rect outRect, RecyclerView parent, int currentPosition) {
        int headCount = 0;
        if (parent.getAdapter() instanceof Callback.IHeadFootCallback) {
            headCount = ((Callback.IHeadFootCallback) parent.getAdapter()).getHeadersCount();
        }
        currentPosition = currentPosition - headCount;

        if (null != keys.get(currentPosition)) {
            outRect.set(0, getTitleHeight(), 0, 0);
        }
    }

    @Override
    protected void setHorizontalOffsets(Rect outRect, RecyclerView parent, int currentPosition) {
        int headCount = 0;
        if (parent.getAdapter() instanceof Callback.IHeadFootCallback) {
            headCount = ((Callback.IHeadFootCallback) parent.getAdapter()).getHeadersCount();
        }
        currentPosition = currentPosition - headCount;

        if (null != keys.get(currentPosition)) {
            outRect.set(getTitleHeight(), 0, 0, 0);
        }
    }

    @Override
    protected void drawVerticalDivider(Canvas c, RecyclerView parent, int currentPosition, int childLeft, int childTop, int childRight, int childBottom) {
        int headCount = 0;
        if (parent.getAdapter() instanceof Callback.IHeadFootCallback) {
            headCount = ((Callback.IHeadFootCallback) parent.getAdapter()).getHeadersCount();
        }
        currentPosition = currentPosition - headCount;

        if (null != keys.get(currentPosition)) {
            c.drawRect(childLeft, childTop - getTitleHeight(), childRight, childTop, mBackgroundPaint);

            Rect bgRect = new Rect(childLeft, childTop - getTitleHeight(), childRight, childTop);
            drawVerticalText(c, keys.get(currentPosition), bgRect, mTextPaint);
        }
    }

    @Override
    protected void drawHorizontalDivider(Canvas c, RecyclerView parent, int currentPosition, int childLeft, int childTop, int childRight, int childBottom) {
        if (null != keys.get(currentPosition)) {
            c.drawRect(childLeft, childTop, childLeft - sDivider.getIntrinsicWidth(), childBottom, mBackgroundPaint);
            c.drawText(keys.get(currentPosition), childLeft, childTop, mTextPaint);
        }
    }

    public void setKeys(SparseArray<String> keys) {
        this.keys.clear();
        this.keys = keys;
    }

    private void drawVerticalText(Canvas c, String content, Rect bgRect, Paint textPaint) {
        int textLeft = bgRect.left + UIScreenUtil.dp2px(sContext, TextSize);
        int textBottom = bgRect.centerY() + ((int) textPaint.getTextSize() >> 1);

        c.drawText(content, textLeft, textBottom, textPaint);
    }

    private int getTitleHeight() {
        return UIScreenUtil.dp2px(sContext, LetterHeight);
    }
}
