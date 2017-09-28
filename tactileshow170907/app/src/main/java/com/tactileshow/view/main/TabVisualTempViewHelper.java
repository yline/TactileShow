package com.tactileshow.view.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.tactileshow.main.R;
import com.tactileshow.util.StaticValue;
import com.tactileshow.view.custom.DefineChartView;
import com.tactileshow.view.custom.DefinedScrollView;
import com.tactileshow.view.custom.DefinedViewPager;

/**
 * 温度
 *
 * @author yline 2017/9/28 -- 16:46
 * @version 1.0.0
 */
public class TabVisualTempViewHelper {

    private View parentView;
    private DefinedScrollView definedScrollView;
    private TabVisualQueryView queryView;
    private DefineChartView defineChartView;

    public TabVisualTempViewHelper(Context context, DefinedViewPager viewPager) {
        initView(context, viewPager);
    }

    private void initView(Context context, final DefinedViewPager viewPager) {
        parentView = LayoutInflater.from(context).inflate(R.layout.view_tab_visual_temp, null);
        definedScrollView = parentView.findViewById(R.id.visual_temp_scroll);
        queryView = parentView.findViewById(R.id.visual_temp_query);

        defineChartView = parentView.findViewById(R.id.visual_temp_chart);
        defineChartView.setYRange(StaticValue.temp_min_axis, StaticValue.temp_max_axis);
        defineChartView.setOnTouchChartCallback(new DefineChartView.OnTouchChartCallback() {
            @Override
            public void onActionUp() {
                //viewPager.setTouchIntercept(true);
                definedScrollView.setTouchIntercept(true);
            }

            @Override
            public void onActionDown() {
                //viewPager.setTouchIntercept(false);
                definedScrollView.setTouchIntercept(false);
            }
        });
    }

    public void addData(long stamp, double tempNum) {
        defineChartView.addNowData(stamp, tempNum);
        defineChartView.notifyDataChanged();
    }

    public View getView(){
        return parentView;
    }
}
