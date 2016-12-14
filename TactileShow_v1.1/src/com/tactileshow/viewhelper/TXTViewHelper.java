package com.tactileshow.viewhelper;

import com.tactileshow.main.R;
import com.tactileshow.view.DefinedScrollView;
import com.tactileshow.view.DefinedViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class TXTViewHelper
{
    private final static double TXT_MIN_AXIS = -500;
    
    private final static double TXT_MAX_AXIS = 500;
    
    private TXTLineChartBuilder lineChartBuilder;
    
    private View contentView;
    
    @SuppressLint("InflateParams")
    public TXTViewHelper(Context context, DefinedViewPager pager)
    {
        contentView = LayoutInflater.from(context).inflate(R.layout.view_maintab_txt, null);
        
        initChartView(context, pager, contentView);
    }
    
    private void initChartView(Context context, DefinedViewPager pager, View view)
    {
        DefinedScrollView scroll = (DefinedScrollView)view.findViewById(R.id.scroll_txt);
        LinearLayout chartLayout = (LinearLayout)view.findViewById(R.id.visual_txt_chart_layout);
        
        lineChartBuilder = new TXTLineChartBuilder(context, chartLayout, pager, scroll);
        lineChartBuilder.setYRange(TXT_MIN_AXIS, TXT_MAX_AXIS);
    }
    
    public View getView()
    {
        return contentView;
    }
    
    public void onSaveInstanceState(Bundle outState)
    {
        lineChartBuilder.onSaveInstanceState(outState);
    }
    
    public void onRestoreInstanceState(Bundle savedState)
    {
        lineChartBuilder.onRestoreInstanceState(savedState);
    }
}
