package com.tactileshow.maintab.viewhelper.visual;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;

import com.tactileshow.main.R;
import com.tactileshow.maintab.view.DefinedViewPager;
import com.tactileshow.maintab.viewhelper.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class VisualViewHelper {
    private View view;

    private DefinedViewPager viewPager;
    private VisualTempViewHelper tempViewHelper;
    private VisualHumViewHelper humViewHelper;
    private VisualHeaderViewHelper headerViewHelper;

    public VisualViewHelper(Context context, DefinedViewPager pager) {
        this.view = LayoutInflater.from(context).inflate(R.layout.view_tab_visual, null);
        this.viewPager = view.findViewById(R.id.visual_view_pager);
        viewPager.setOffscreenPageLimit(3);

        tempViewHelper = new VisualTempViewHelper(context, pager);
        humViewHelper = new VisualHumViewHelper(context, pager);
        headerViewHelper = new VisualHeaderViewHelper(context, pager);

        initView(view);
    }

    private void initView(View parentView) {
        final List<View> viewList = new ArrayList<>();
        final List<String> titleList = new ArrayList<>();

        viewList.add(tempViewHelper.getView());
        titleList.add("Red LED");

        viewList.add(humViewHelper.getView());
        titleList.add("IR LED");

        viewList.add(headerViewHelper.getView());
        titleList.add("Green LED");

        TabLayout tabLayout = parentView.findViewById(R.id.visual_tab_layout);
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter();
        pagerAdapter.setViews(viewList, titleList);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setTouchIntercept(false);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void setTemp(long stamp, float tempNum) {
        tempViewHelper.addData(stamp, tempNum);
    }

    public void setHum(long stamp, float humNum) {
        humViewHelper.addData(stamp, humNum);
    }

    /**
     * 第三渠道，内容
     */
    public void setHeader(long stamp, float headerNum) {
        headerViewHelper.addData(stamp, headerNum);
    }

    public View getView() {
        return this.view;
    }
}
