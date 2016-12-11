package com.tactileshow.view;
/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.tactileshow.main.R;
import com.tactileshow.util.StaticValue;

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
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    
    private XYSeries series, history_series;
    
    private GraphicalView mChartView;
    
    private XYSeriesRenderer renderer;
    
    private Context context;
    
    private LinearLayout layout;
    
    private int maxPoints = 100;
    
    private String title;
    
    private DefinedViewPager pager;
    
    private DefinedScrollView scroll;
    
    private int is_touch = 0;
    
    private TextView tv;
    
    private String sensor;
    
    public LineChartBuilder(Context context, LinearLayout layout, String title, DefinedViewPager pager,
        DefinedScrollView scroll, String sensor)
    {
        
        this.context = context;
        this.layout = layout;
        this.title = title;
        this.pager = pager;
        this.scroll = scroll;
        this.sensor = sensor;
        
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
        
        //    String seriesTitle = "Series " + (mDataset.getSeriesCount() + 1);
        series = new XYSeries("实时信息");
        history_series = new XYSeries("历史记录");
        mDataset.addSeries(series);
        renderer = new XYSeriesRenderer();
        
        renderer.setColor(Color.RED);
        renderer.setPointStyle(PointStyle.POINT);
        
        mRenderer.addSeriesRenderer(renderer);
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setFillPoints(true);
        renderer.setLineWidth(4);
        mRenderer.setYLabels(10);
        mRenderer.setYLabelsPadding(-50);
        mRenderer.setYLabelsColor(0, Color.BLACK);
        init();
    }
    
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
            layout.addView(mChartView, StaticValue.width - 5, StaticValue.height / 2);
        }
        else
        {
            mChartView.repaint();
        }
    }
    
    public void changeMode()
    {
        if (sensor.equals(StaticValue.BLE))
        {
            if (StaticValue.ble_real_time)
            {
                mDataset.clear();
                mDataset.addSeries(series);
            }
            else
            {
                mDataset.clear();
                mDataset.addSeries(history_series);
            }
        }
    }
    
    /*
     * For history information method.
     */
    public void addHistoryData(double x, double y)
    {
        history_series.add(x, y);
    }
    
    public void clearHistory()
    {
        history_series.clear();
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
    
    private int rec_after_touch = 0;
    
    public void addData(double x, double y)
    {
        series.add(x, y);
        if (StaticValue.ble_real_time && sensor.equals(StaticValue.BLE))
        {
            if (is_touch == 2)
            {//刚按过
                rec_after_touch++;
                if (rec_after_touch > 3)
                {
                    mRenderer.setXAxisMax(x);
                    if (series.getItemCount() > maxPoints)
                    {
                        Log.e("wshg", "" + series.getX(series.getItemCount() - maxPoints));
                        mRenderer.setXAxisMin(series.getX(series.getItemCount() - maxPoints));
                    }
                    else
                    {
                        Log.e("wshg", "" + series.getX(0));
                        mRenderer.setXAxisMin(series.getX(0));
                    }
                    rec_after_touch = 0;
                    is_touch = 0;
                }
            }
            else if (is_touch == 0)
            {
                mRenderer.setXAxisMax(x);
                if (series.getItemCount() > maxPoints)
                {
                    Log.e("wshg", "" + series.getX(series.getItemCount() - maxPoints));
                    mRenderer.setXAxisMin(series.getX(series.getItemCount() - maxPoints));
                }
                else
                {
                    Log.e("wshg", "" + series.getX(0));
                    mRenderer.setXAxisMin(series.getX(0));
                }
            }
        }
        mChartView.repaint();
    }
    
    public void setMaxPoints(int maxPoints)
    {
        this.maxPoints = maxPoints;
    }
    
    public void onSaveInstanceState(Bundle outState)
    {
        // save the current data, for instance when changing screen orientation
        outState.putSerializable("dataset", mDataset);
        outState.putSerializable("renderer", mRenderer);
        outState.putSerializable("current_series", series);
        outState.putSerializable("current_renderer", renderer);
        outState.putSerializable("history_series", history_series);
    }
    
    public void onRestoreInstanceState(Bundle savedState)
    {
        // restore the current data, for instance when changing the screen
        // orientation
        mDataset = (XYMultipleSeriesDataset)savedState.getSerializable("dataset");
        mRenderer = (XYMultipleSeriesRenderer)savedState.getSerializable("renderer");
        series = (XYSeries)savedState.getSerializable("current_series");
        renderer = (XYSeriesRenderer)savedState.getSerializable("current_renderer");
        history_series = (XYSeries)savedState.getSerializable("history_series");
    }
    
}
