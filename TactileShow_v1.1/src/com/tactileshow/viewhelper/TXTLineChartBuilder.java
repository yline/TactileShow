package com.tactileshow.viewhelper;

import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.tactileshow.main.R;
import com.tactileshow.util.FileHelper;
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
    
    /** 管理数据的 */
    private XYMultipleSeriesDataset multipleSeriesDataset;
    
    private XYSeriesRenderer[] renderers;
    
    /** 管理 坐标系的 */
    private XYMultipleSeriesRenderer multipleSeriesRenderer;
    
    private GraphicalView chartView;
    
    private TextView tvTitle;
    
    public TXTLineChartBuilder(Context context, LinearLayout layout, DefinedViewPager pager, DefinedScrollView scroll)
    {
        initRenderer();
        initDataSet();
        
        initView(context, layout, pager, scroll); // , pager, scroll
        
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
        multipleSeriesDataset = new XYMultipleSeriesDataset();
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
                            break;
                        case MotionEvent.ACTION_UP:
                            pager.setTouchIntercept(true);
                            scroll.setTouchIntercept(true);
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
    
    private void updateChartView()
    {
        List<List<String>> result = FileHelper.getInstance().readMapData();
        
        // 确定X轴范围
        int length = result.size() + 2;
        multipleSeriesRenderer.setXAxisMax(length);
        if (length < MAX_POINTS)
        {
            multipleSeriesRenderer.setXAxisMin(0);
        }
        else
        {
            multipleSeriesRenderer.setXAxisMin(length - MAX_POINTS);
        }
        
        // 添加数据,并计算出最大值和最小值
        float max = getMapDataFloat(result, 0, 0);
        float min = max;
        
        for (int i = 0; i < CNT; i++)
        {
            // 一次绘制一支
            
            for (int j = 0; j < result.size(); j++)
            {
                float number = getMapDataFloat(result, j, i);
                series[i].add(j, j, number);
                
                max = max > number ? max : number;
                min = min < number ? max : number;
            }
        }
        
        setYRange(min, max);
        chartView.repaint();
    }
    
    /**
     * @param result
     * @param i 行(List<String>)
     * @param j 列(String)
     */
    private float getMapDataFloat(List<List<String>> listStr, int i, int j)
    {
        float result = Float.parseFloat(listStr.get(i).get(j));
        return result;
    }
    
    public void setXRange(int min, int max)
    {
        multipleSeriesRenderer.setXAxisMax(max + 2);
        multipleSeriesRenderer.setXAxisMin(min - 2);
    }
    
    public void setYRange(float min, float max)
    {
        float realMax = max > 0 ? max * 1.1f : max * 0.9f;
        float realMin = min > 0 ? min * 0.9f : min * 1.1f;
        
        multipleSeriesRenderer.setYAxisMax(realMax);
        multipleSeriesRenderer.setYAxisMin(realMin);
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
