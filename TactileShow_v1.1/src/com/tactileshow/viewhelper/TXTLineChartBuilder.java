package com.tactileshow.viewhelper;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.tactileshow.main.R;
import com.tactileshow.util.ScreenUtil;
import com.tactileshow.view.DefinedScrollView;
import com.tactileshow.view.DefinedViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TXTLineChartBuilder
{
    private static final String DEFAULT_CHART_TITLE = "文本数据变化趋势";
    
    private static final int MAX_POINTS = 100;
    
    /** 通道个数 */
    private static final int CNT = 4;
    
    /** 通道绘制的颜色 */
    private static final int[] CNT_COLORS = new int[] {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
    
    // 图标绘制相关
    private XYSeries[] series;
    
    private XYMultipleSeriesDataset multipleSeriesDataset = new XYMultipleSeriesDataset();
    
    private XYSeriesRenderer[] renderers;
    
    private XYMultipleSeriesRenderer multipleSeriesRenderer;
    
    private GraphicalView chartView;
    
    private TextView tvTitle;
    
    public TXTLineChartBuilder(Context context, LinearLayout layout, DefinedViewPager pager, DefinedScrollView scroll)
    {
        initRenderer();
        initDataSet();
        
        initView(context, layout, pager, scroll);
        
        updateChartView();
    }
    
    private void initRenderer()
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
        
        renderers = new XYSeriesRenderer[CNT];
        
        for (int i = 0; i < CNT; ++i)
        {
            renderers[i] = new XYSeriesRenderer();
            renderers[i].setColor(CNT_COLORS[i]);
            renderers[i].setPointStyle(PointStyle.CIRCLE);
            renderers[i].setFillPoints(true);
            renderers[i].setLineWidth(4);
            multipleSeriesRenderer.addSeriesRenderer(renderers[i]);
        }
    }
    
    private void initDataSet()
    {
        series = new XYSeries[CNT];
        for (int i = 0; i < CNT; ++i)
        {
            series[i] = new XYSeries("通道" + (i + 1));
            multipleSeriesDataset.addSeries(series[i]);
        }
    }
    
    private void initView(Context context, LinearLayout containerLayout, final DefinedViewPager pager,
        final DefinedScrollView scroll)
    {
        if (chartView == null)
        {
            chartView = ChartFactory.getLineChartView(context, multipleSeriesDataset, multipleSeriesRenderer);
            multipleSeriesRenderer.setClickEnabled(true);
            multipleSeriesRenderer.setSelectableBuffer(10);
            
            // 事件拦截
            chartView.setOnTouchListener(new OnTouchListener()
            {
                
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    if (event.getAction() == MotionEvent.ACTION_UP)
                    {
                        pager.setTouchIntercept(true);
                        scroll.setTouchIntercept(true);
                    }
                    else if (event.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        pager.setTouchIntercept(false);
                        scroll.setTouchIntercept(false);
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
    
    private void updateChartView()
    {
        List<String> ls = new ArrayList<String>();
        
        if (ls != null && ls.size() != 0)
        {
            double YMax = Double.MIN_VALUE, YMin = Double.MAX_VALUE;
            for (int i = 0; i < ls.size(); ++i)
            {
                String[] strs = ls.get(i).split("\t");
                double[] nums = new double[CNT];
                for (int j = 0; j < CNT; ++j)
                {
                    nums[j] = Double.parseDouble(strs[j]);
                    series[j].add(i, nums[j]);
                    YMax = nums[j] > YMax ? nums[j] : YMax;
                    YMin = nums[j] < YMin ? nums[j] : YMin;
                }
            }
            //setYRange(YMax * 1.1, YMin * 1.1);
            int len = ls.size();
            multipleSeriesRenderer.setXAxisMax(len);
            if (len < MAX_POINTS)
            {
                multipleSeriesRenderer.setXAxisMin(0);
            }
            else
            {
                multipleSeriesRenderer.setXAxisMin(len - MAX_POINTS);
            }
            
            chartView.repaint();
        }
    }
    
    public void setRange(long fr, long to)
    {
        multipleSeriesRenderer.setXAxisMax(to);
        multipleSeriesRenderer.setXAxisMin(fr);
    }
    
    public void setYRange(double fr, double to)
    {
        multipleSeriesRenderer.setYAxisMax(to);
        multipleSeriesRenderer.setYAxisMin(fr);
    }
    
    public void repaint()
    {
        chartView.repaint();
    }
    
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putSerializable("dataset", multipleSeriesDataset);
        outState.putSerializable("renderer", multipleSeriesRenderer);
        for (int i = 0; i < CNT; ++i)
        {
            outState.putSerializable("series" + i, series[i]);
            outState.putSerializable("renderers" + i, renderers[i]);
        }
    }
    
    public void onRestoreInstanceState(Bundle savedState)
    {
        multipleSeriesDataset = (XYMultipleSeriesDataset)savedState.getSerializable("dataset");
        multipleSeriesRenderer = (XYMultipleSeriesRenderer)savedState.getSerializable("renderer");
        for (int i = 0; i < CNT; ++i)
        {
            series[i] = (XYSeries)savedState.getSerializable("series" + i);
            renderers[i] = (XYSeriesRenderer)savedState.getSerializable("renderers" + i);
        }
    }
}
