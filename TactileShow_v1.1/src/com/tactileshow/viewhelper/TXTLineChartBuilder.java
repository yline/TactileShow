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
    private static final int cnt = 1;
    
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    
    private XYSeries[] series;
    
    private GraphicalView mChartView;
    
    private XYSeriesRenderer[] renderers;
    
    private int[] colors;
    
    private Context context;
    
    private LinearLayout layout;
    
    private int maxPoints = 100;
    
    private String title;
    
    private DefinedViewPager pager;
    
    private DefinedScrollView scroll;
    
    private TextView tv;
    
    private int is_touch = 0;
    
    public TXTLineChartBuilder(Context context, LinearLayout layout, String title, DefinedViewPager pager,
        DefinedScrollView scroll)
    {
        this.context = context;
        this.layout = layout;
        this.title = title;
        this.pager = pager;
        this.scroll = scroll;
        mRenderer.setAxisTitleTextSize(30);
        mRenderer.setChartTitleTextSize(30);
        mRenderer.setLabelsTextSize(30);
        mRenderer.setLegendTextSize(30);
        mRenderer.setMargins(new int[] {5, 5, 0, 5});
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setPointSize(5);
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.argb(0, 50, 50, 50));
        mRenderer.setShowGridX(true);
        mRenderer.setGridColor(Color.BLACK);
        mRenderer.setYLabels(10);
        mRenderer.setYLabelsPadding(-50);
        mRenderer.setYLabelsColor(0, Color.BLACK);
        
        colors = new int[] {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
        
        series = new XYSeries[cnt];
        for (int i = 0; i < cnt; ++i)
        {
            series[i] = new XYSeries("通道" + (i + 1));
            mDataset.addSeries(series[i]);
        }
        
        renderers = new XYSeriesRenderer[cnt];
        
        for (int i = 0; i < cnt; ++i)
        {
            renderers[i] = new XYSeriesRenderer();
            renderers[i].setColor(colors[i]);
            renderers[i].setPointStyle(PointStyle.CIRCLE);
            renderers[i].setFillPoints(true);
            renderers[i].setLineWidth(4);
            mRenderer.addSeriesRenderer(renderers[i]);
        }
        init();
    }
    
    //有问题
    public void init()
    {
        if (mChartView == null)
        {
            //mChartView = ChartFactory.getCubeLineChartView(context, mDataset, mRenderer, 0.33f);
            mChartView = ChartFactory.getLineChartView(context, mDataset, mRenderer);
            mRenderer.setClickEnabled(true);
            mRenderer.setSelectableBuffer(10);
            mChartView.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    
                }
            });
            mChartView.setOnTouchListener(new OnTouchListener()
            {
                
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    if (event.getAction() == MotionEvent.ACTION_UP)
                    {
                        pager.setTouchIntercept(true);
                        scroll.setTouchIntercept(true);
                        is_touch = 2;
                    }
                    else if (event.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        pager.setTouchIntercept(false);
                        scroll.setTouchIntercept(false);
                        is_touch = 1;
                    }
                    return false;
                }
            });
            tv = new TextView(context);
            tv.setText(title);
            tv.setTextSize(context.getResources().getDimension(R.dimen.dimen_visual_text_size) / 3);
            layout.addView(tv);
            layout.addView(mChartView, ScreenUtil.getScreenWidth(context) - 5, ScreenUtil.getScreenHeight(context) / 2);
        }
        else
        {
            mChartView.repaint();
        }
        
        List<String> ls = FileHelper.getInstance().readMapData();
        
        if (ls != null && ls.size() != 0)
        {
            double YMax = Double.MIN_VALUE, YMin = Double.MAX_VALUE;
            for (int i = 0; i < ls.size(); ++i)
            {
                String[] strs = ls.get(i).split("\t");
                double[] nums = new double[cnt];
                for (int j = 0; j < cnt; ++j)
                {
                    nums[j] = Double.parseDouble(strs[j]);
                    series[j].add(i, nums[j]);
                    YMax = nums[j] > YMax ? nums[j] : YMax;
                    YMin = nums[j] < YMin ? nums[j] : YMin;
                }
            }
            //setYRange(YMax * 1.1, YMin * 1.1);
            int len = ls.size();
            mRenderer.setXAxisMax(len);
            if (len < maxPoints)
            {
                mRenderer.setXAxisMin(0);
            }
            else
            {
                mRenderer.setXAxisMin(len - maxPoints);
            }
            
            mChartView.repaint();
        }
    }
    
    public void setRange(long fr, long to)
    {
        mRenderer.setXAxisMax(to);
        mRenderer.setXAxisMin(fr);
    }
    
    public void setYRange(double fr, double to)
    {
        mRenderer.setYAxisMax(to);
        mRenderer.setYAxisMin(fr);
    }
    
    public void repaint()
    {
        mChartView.repaint();
    }
    
    public void setTitle(String title)
    {
        tv.setText(title);
    }
    
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putSerializable("dataset", mDataset);
        outState.putSerializable("renderer", mRenderer);
        for (int i = 0; i < cnt; ++i)
        {
            outState.putSerializable("series" + i, series[i]);
            outState.putSerializable("renderers" + i, renderers[i]);
        }
    }
    
    public void onRestoreInstanceState(Bundle savedState)
    {
        mDataset = (XYMultipleSeriesDataset)savedState.getSerializable("dataset");
        mRenderer = (XYMultipleSeriesRenderer)savedState.getSerializable("renderer");
        for (int i = 0; i < cnt; ++i)
        {
            series[i] = (XYSeries)savedState.getSerializable("series" + i);
            renderers[i] = (XYSeriesRenderer)savedState.getSerializable("renderers" + i);
        }
    }
}
