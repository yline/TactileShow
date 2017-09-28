package com.tactileshow.view.custom;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tactileshow.main.R;
import com.yline.utils.LogUtil;
import com.yline.utils.UIScreenUtil;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.Date;

/**
 * 重构绘图
 *
 * @author yline 2017/9/28 -- 15:10
 * @version 1.0.0
 */
public class DefineChartView extends LinearLayout {
    private static final int OffsetOfXRange = 1000;

    private XYMultipleSeriesRenderer seriesRenderer;

    private TimeSeries nowSeries, historySeries;
    private XYMultipleSeriesDataset seriesDataset;

    private TextView tvTitle;
    private GraphicalView graphicalView;

    private OnTouchChartCallback onTouchChartCallback;
    private int maxPoints;

    public DefineChartView(Context context) {
        super(context);
        initView(context);
    }

    public DefineChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DefineChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        initRenderer();
        initDataSet();

        initGraphicalView(context);

        maxPoints = 50;
    }

    private void initGraphicalView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_define_chart, this, true);
        tvTitle = view.findViewById(R.id.define_chart_tv);

        if (null == graphicalView) {
            graphicalView = ChartFactory.getTimeChartView(context, seriesDataset, seriesRenderer, null);
            // graphicalView = ChartFactory.getCubeLineChartView(context, seriesDataset, seriesRenderer, 0f); // 0不弯曲，1完全弯曲；0.33f 够用

            seriesRenderer.setClickEnabled(true);
            seriesRenderer.setSelectableBuffer(10);

            graphicalView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.i("onClick: ");
                }
            });
            graphicalView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                            if (null != onTouchChartCallback) {
                                onTouchChartCallback.onActionUp();
                            }
                            break;
                        case MotionEvent.ACTION_DOWN:
                            if (null != onTouchChartCallback) {
                                onTouchChartCallback.onActionDown();
                            }
                            break;
                    }
                    return false;
                }
            });

            FrameLayout frameLayout = view.findViewById(R.id.define_chart_graphic);
            ViewGroup.LayoutParams layoutParams = frameLayout.getLayoutParams();
            layoutParams.width = UIScreenUtil.getScreenWidth(context) - 5;
            layoutParams.height = UIScreenUtil.getScreenHeight(context) / 2;
            frameLayout.addView(graphicalView);
        } else {
            graphicalView.repaint();
        }
    }

    private void initRenderer() {
        seriesRenderer = new XYMultipleSeriesRenderer();
        seriesRenderer.setAxisTitleTextSize(30);
        seriesRenderer.setChartTitleTextSize(30);
        seriesRenderer.setLabelsTextSize(30);
        seriesRenderer.setLegendTextSize(22);
        seriesRenderer.setMargins(new int[]{5, 5, 0, 5});
        seriesRenderer.setZoomButtonsVisible(true);
        seriesRenderer.setPointSize(5);
        seriesRenderer.setApplyBackgroundColor(true);
        seriesRenderer.setBackgroundColor(Color.argb(0, 50, 50, 50));
        // seriesRenderer.setShowGridX(true);
        // seriesRenderer.setShowGridY(true);
        seriesRenderer.setShowGrid(true);

        seriesRenderer.setShowAxes(true);
        seriesRenderer.setAxesColor(Color.GREEN);

        seriesRenderer.setGridColor(Color.BLACK);

        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setColor(Color.RED);
        renderer.setPointStyle(PointStyle.POINT);

        seriesRenderer.addSeriesRenderer(renderer);

        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setFillPoints(true);
        renderer.setLineWidth(4);

        seriesRenderer.setXLabels(4);
        seriesRenderer.setYLabels(10);
        seriesRenderer.setYLabelsPadding(-40);

        seriesRenderer.setXLabelsColor(Color.RED);
        seriesRenderer.setYLabelsColor(0,Color.BLACK);
    }

    private void initDataSet() {
        seriesDataset = new XYMultipleSeriesDataset();
        nowSeries = new TimeSeries("实时信息");
        historySeries = new TimeSeries("历史记录");

        seriesDataset.addSeries(nowSeries);
    }

    public void addHistoryData(long stamp, double y) {
        if (null != historySeries) {
            historySeries.add(new Date(stamp), y);
        }
    }

    public void clearHistoryData() {
        if (null != historySeries) {
            historySeries.clear();
        }
    }

    public void setXRange(long from, long to) {
        if (null != seriesRenderer) {
            seriesRenderer.setXAxisMin(from);
            seriesRenderer.setXAxisMax(to);
        }
    }

    public void setYRange(double from, double to) {
        if (null != seriesRenderer) {
            seriesRenderer.setYAxisMin(from);
            seriesRenderer.setYAxisMax(to);
        }
    }

    public void setTitle(String title) {
        if (null != tvTitle) {
            tvTitle.setText(title);
        }
    }

    public void setMaxPoint(int maxPoint) {
        this.maxPoints = maxPoint;
    }

    public void addNowData(long stamp, double y) {
        if (null != nowSeries) {
            nowSeries.add(new Date(stamp), y);
        }
    }

    public void updateXRange(long maxStamp) {
        maxStamp += OffsetOfXRange;

        if (null != nowSeries && null != seriesRenderer) {
            seriesRenderer.setXAxisMax(maxStamp);
            if (nowSeries.getItemCount() > maxPoints) {
                seriesRenderer.setXAxisMin(nowSeries.getX(nowSeries.getItemCount() - maxPoints));
            } else {
                Log.i("xxxx-", "updateXRange: nowSeries.getX(0) = " + nowSeries.getX(0));
                seriesRenderer.setXAxisMin(nowSeries.getX(0));
            }
        }
    }

    public void notifyDataChanged() {
        if (null != graphicalView) {
            graphicalView.repaint();
        }
    }

    public void setOnTouchChartCallback(OnTouchChartCallback onTouchChartCallback) {
        this.onTouchChartCallback = onTouchChartCallback;
    }

    public interface OnTouchChartCallback {
        void onActionUp();

        void onActionDown();
    }
}
