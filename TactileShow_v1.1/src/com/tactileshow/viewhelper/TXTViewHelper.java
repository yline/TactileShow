package com.tactileshow.viewhelper;

import com.tactileshow.main.R;
import com.tactileshow.util.StaticValue;
import com.tactileshow.view.DefinedScrollView;
import com.tactileshow.view.DefinedViewPager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;

public class TXTViewHelper
{
    private TXTLineChartBuilder txtMap;
    
    private Activity context;
    
    private View view;
    
    private LinearLayout layout;
    
    private DefinedScrollView scroll;
    
    private DefinedViewPager pager;
    
    private TabHost queryHost;
    
    public TXTViewHelper(Activity activity, DefinedViewPager pager)
    {
        this.context = activity;
        this.pager = pager;
        
        view = context.getLayoutInflater().inflate(R.layout.activity_visual_txt_info, null);
        scroll = (DefinedScrollView)view.findViewById(R.id.scroll_txt);
        layout = (LinearLayout)view.findViewById(R.id.visual_txt_chart_layout);
        if (layout == null)
        {
            Log.e("wshg", "Null");
            return;
        }
        
        txtMap = new TXTLineChartBuilder(context, layout, "文本数据变化趋势", this.pager, scroll);
        txtMap.setYRange(StaticValue.txt_min_axis, StaticValue.txt_max_axis);
    }
    
    public View getView()
    {
        return view;
    }
    
    public void onSaveInstanceState(Bundle outState)
    {
        txtMap.onSaveInstanceState(outState);
    }
    
    public void onRestoreInstanceState(Bundle savedState)
    {
        txtMap.onRestoreInstanceState(savedState);
    }
}
