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
 * 湿度
 *
 * @author yline 2017/9/28 -- 16:45
 * @version 1.0.0
 */
public class TabVisualHumViewHelper {

    private View parentView;
    private DefinedScrollView definedScrollView;
    private TabVisualQueryView queryView;
    private DefineChartView defineChartView;

    public TabVisualHumViewHelper(Context context, DefinedViewPager viewPager) {
        initView(context, viewPager);
    }

    private void initView(Context context, final DefinedViewPager viewPager) {
        parentView = LayoutInflater.from(context).inflate(R.layout.view_tab_visual_hum, null);
        definedScrollView = parentView.findViewById(R.id.visual_hum_scroll);
        queryView = parentView.findViewById(R.id.visual_hum_query);

        defineChartView = parentView.findViewById(R.id.visual_hum_chart);
        defineChartView.setYRange(StaticValue.press_min_axis, StaticValue.press_max_axis);
        defineChartView.setOnTouchChartCallback(new DefineChartView.OnTouchChartCallback() {
            @Override
            public void onActionUp() {
                definedScrollView.setTouchIntercept(true);
                viewPager.setTouchIntercept(true);
            }

            @Override
            public void onActionDown() {
                definedScrollView.setTouchIntercept(false);
                viewPager.setTouchIntercept(false);
            }
        });
    }

    public void addData(long stamp, double humData) {
        defineChartView.addNowData(stamp, humData);
        defineChartView.updateXRange(stamp);
        defineChartView.notifyDataChanged();
    }

    public View getView() {
        return parentView;
    }
}
