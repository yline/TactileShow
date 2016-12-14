package com.tactileshow.viewhelper;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.tactileshow.main.R;
import com.tactileshow.util.ScreenUtil;
import com.tactileshow.util.StaticValue;
import com.tactileshow.view.DefinedScrollView;
import com.tactileshow.view.DefinedViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LineChartBuilder
{
    private static final String DEFAULT_CHART_TITLE = "蓝牙数据变化趋势";
    
    private static final int MAX_POINTS = 100;
    
    // 图标绘制相关
    private XYSeriesRenderer seriesRenderer;
    
    private XYMultipleSeriesDataset multipleSeriesDataset;
    
    private XYMultipleSeriesRenderer multipleSeriesRenderer;
    
    private XYSeries currentSeries, historySeries;
    
    private GraphicalView chartView;
    
    private TextView tvTitle;
    
    // 其他
    private int is_touch = 0;
    
    private int rec_after_touch = 0;
    
    public LineChartBuilder(Context context, LinearLayout layout, DefinedViewPager pager, DefinedScrollView scroll)
    {
        initSeriesRenderer();
        
        initMultipleSeriesRenderer(seriesRenderer);
        
        currentSeries = new XYSeries("实时信息");
        historySeries = new XYSeries("历史记录");
        multipleSeriesDataset = new XYMultipleSeriesDataset();
        multipleSeriesDataset.addSeries(currentSeries);
        
        initView(context, layout, pager, scroll); // , pager, scroll
    }
    
    private void initSeriesRenderer()
    {
        seriesRenderer = new XYSeriesRenderer();
        seriesRenderer.setColor(Color.RED);
        // seriesRenderer.setPointStyle(PointStyle.POINT);
        seriesRenderer.setPointStyle(PointStyle.CIRCLE);
        seriesRenderer.setFillPoints(true);
        seriesRenderer.setLineWidth(4);
    }
    
    private void initMultipleSeriesRenderer(XYSeriesRenderer xySeriesRenderer)
    {
        multipleSeriesRenderer = new XYMultipleSeriesRenderer();
        multipleSeriesRenderer.setAxisTitleTextSize(30);
        multipleSeriesRenderer.setChartTitleTextSize(30);
        multipleSeriesRenderer.setLabelsTextSize(30);
        multipleSeriesRenderer.setLegendTextSize(30);
        multipleSeriesRenderer.setMargins(new int[] {5, 5, 0, 5});
        multipleSeriesRenderer.setZoomButtonsVisible(true);
        multipleSeriesRenderer.setPointSize(5);
        multipleSeriesRenderer.setApplyBackgroundColor(true);
        multipleSeriesRenderer.setBackgroundColor(Color.argb(0, 50, 50, 50));
        multipleSeriesRenderer.setShowGridX(true);
        multipleSeriesRenderer.setGridColor(Color.BLACK);
        multipleSeriesRenderer.setYLabels(10);
        multipleSeriesRenderer.setYLabelsPadding(-50);
        multipleSeriesRenderer.setYLabelsColor(0, Color.BLACK);
        multipleSeriesRenderer.addSeriesRenderer(xySeriesRenderer);
    }
    
    private void initView(Context context, LinearLayout containerLayout, final DefinedViewPager pager,
        final DefinedScrollView scroll)
    {
        if (null == chartView)
        {
            // 添加 ChartView
            chartView = ChartFactory.getLineChartView(context, multipleSeriesDataset, multipleSeriesRenderer);
            multipleSeriesRenderer.setClickEnabled(true);
            multipleSeriesRenderer.setSelectableBuffer(10);
            
            // 事件拦截
            chartView.setOnClickListener(new View.OnClickListener()
            {
                
                @Override
                public void onClick(View v)
                {
                    
                }
            });
            chartView.setOnTouchListener(new OnTouchListener()
            {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    switch (event.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            pager.setTouchIntercept(false);
                            scroll.setTouchIntercept(false);
                            is_touch = 1;
                            break;
                        case MotionEvent.ACTION_UP:
                            pager.setTouchIntercept(true);
                            scroll.setTouchIntercept(true);
                            is_touch = 2;
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
            
            // 添加默认标题
            tvTitle = new TextView(context);
            tvTitle.setText(DEFAULT_CHART_TITLE);
            tvTitle.setTextSize(context.getResources().getDimension(R.dimen.dimen_visual_text_size) / 3);
            
            containerLayout.addView(tvTitle);
            containerLayout.addView(chartView,
                ScreenUtil.getScreenWidth(context) - 5,
                ScreenUtil.getScreenHeight(context) / 2);
        }
        else
        {
            chartView.repaint();
        }
    }
    
    public void changeMode()
    {
        if (StaticValue.ble_real_time)
        {
            multipleSeriesDataset.clear();
            multipleSeriesDataset.addSeries(currentSeries);
        }
        else
        {
            multipleSeriesDataset.clear();
            multipleSeriesDataset.addSeries(historySeries);
        }
    }
    
    /*
     * For history information method.
     */
    public void addHistoryData(double x, double y)
    {
        historySeries.add(x, y);
    }
    
    public void clearHistory()
    {
        historySeries.clear();
    }
    
    public void setXRange(long fr, long to)
    {
        multipleSeriesRenderer.setXAxisMax(to);
        multipleSeriesRenderer.setXAxisMin(fr);
    }
    
    public void setYRange(double fr, double to)
    {
        multipleSeriesRenderer.setYAxisMax(to);
        multipleSeriesRenderer.setYAxisMin(fr);
    }
    
    public void setTitle(String title)
    {
        tvTitle.setText(title);
    }
    
    public void addData(double x, double y)
    {
        currentSeries.add(x, y);
        if (StaticValue.ble_real_time)
        {
            if (is_touch == 2)
            {//刚按过
                rec_after_touch++;
                if (rec_after_touch > 3)
                {
                    multipleSeriesRenderer.setXAxisMax(x);
                    if (currentSeries.getItemCount() > MAX_POINTS)
                    {
                        Log.e("wshg", "" + currentSeries.getX(currentSeries.getItemCount() - MAX_POINTS));
                        multipleSeriesRenderer
                            .setXAxisMin(currentSeries.getX(currentSeries.getItemCount() - MAX_POINTS));
                    }
                    else
                    {
                        Log.e("wshg", "" + currentSeries.getX(0));
                        multipleSeriesRenderer.setXAxisMin(currentSeries.getX(0));
                    }
                    rec_after_touch = 0;
                    is_touch = 0;
                }
            }
            else if (is_touch == 0)
            {
                multipleSeriesRenderer.setXAxisMax(x);
                if (currentSeries.getItemCount() > MAX_POINTS)
                {
                    Log.e("wshg", "" + currentSeries.getX(currentSeries.getItemCount() - MAX_POINTS));
                    multipleSeriesRenderer.setXAxisMin(currentSeries.getX(currentSeries.getItemCount() - MAX_POINTS));
                }
                else
                {
                    Log.e("wshg", "" + currentSeries.getX(0));
                    multipleSeriesRenderer.setXAxisMin(currentSeries.getX(0));
                }
            }
        }
        chartView.repaint();
    }
    
    public void onSaveInstanceState(Bundle outState)
    {
        // save the current data, for instance when changing screen orientation
        outState.putSerializable("dataset", multipleSeriesDataset);
        outState.putSerializable("renderer", multipleSeriesRenderer);
        outState.putSerializable("current_renderer", seriesRenderer);
        outState.putSerializable("current_series", currentSeries);
        outState.putSerializable("history_series", historySeries);
    }
    
    public void onRestoreInstanceState(Bundle savedState)
    {
        // restore the current data, for instance when changing the screen orientation
        multipleSeriesDataset = (XYMultipleSeriesDataset)savedState.getSerializable("dataset");
        multipleSeriesRenderer = (XYMultipleSeriesRenderer)savedState.getSerializable("renderer");
        seriesRenderer = (XYSeriesRenderer)savedState.getSerializable("current_renderer");
        currentSeries = (XYSeries)savedState.getSerializable("current_series");
        historySeries = (XYSeries)savedState.getSerializable("history_series");
    }
    
}
