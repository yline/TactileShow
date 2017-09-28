package com.tactileshow.view.main;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;

import com.tactileshow.main.R;
import com.tactileshow.util.StaticValue;
import com.tactileshow.view.custom.DefinedViewPager;

import java.util.ArrayList;
import java.util.List;

public class TabVisualViewHelper {
    private View view;

    private DefinedViewPager viewPager;
    private TabVisualTempViewHelper tempViewHelper;
    private TabVisualHumViewHelper humViewHelper;

    // private TempVisualInfo tempVisual;
    // private PressVisualInfo pressVisual;

    public TabVisualViewHelper(Context context, DefinedViewPager pager) {
        this.view = LayoutInflater.from(context).inflate(R.layout.view_tab_visual, null);
        this.viewPager = (DefinedViewPager) view.findViewById(R.id.visual_view_pager);
        viewPager.setOffscreenPageLimit(2);

        tempViewHelper = new TabVisualTempViewHelper(context,pager);
        humViewHelper = new TabVisualHumViewHelper(context, pager);
        // tempVisual = new TempVisualInfo(context, pager);
        // pressVisual = new PressVisualInfo(context, pager);

        initView(view);
    }

    private void initView(View parentView) {
        final List<View> viewList = new ArrayList<>();
        final List<String> titleList = new ArrayList<>();

        viewList.add(tempViewHelper.getView());
        titleList.add(StaticValue.temp_visual_info_name);

        viewList.add(humViewHelper.getView());
        titleList.add(StaticValue.press_visual_info_name);

        TabLayout tabLayout = parentView.findViewById(R.id.visual_tab_layout);

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter();
        pagerAdapter.setViews(viewList, titleList);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setTouchIntercept(false);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void setTemp(long stamp, double tempNum) {
        tempViewHelper.addData(stamp, tempNum);
        // tempVisual.setTemp(t, data);
        // tempViewHelper.addData();
    }

    public void setHum(long stamp, double humNum) {
        //pressVisual.setTemp(t, data);
        humViewHelper.addData(stamp, humNum);
    }

    public View getView() {
        return this.view;
    }
/*
    public void onSaveInstanceState(Bundle outState) {
        tempVisual.onSaveInstanceState(outState);
    }

    public void onRestoreInstanceState(Bundle savedState) {
        pressVisual.onRestoreInstanceState(savedState);
    }*/
}
