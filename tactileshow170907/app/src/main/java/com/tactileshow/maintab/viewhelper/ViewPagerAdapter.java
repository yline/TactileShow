package com.tactileshow.maintab.viewhelper;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 配合TabLayout使用的 ViewPager 的 Adapter
 *
 * @author yline 2017/9/24 -- 20:39
 * @version 1.0.0
 */
public class ViewPagerAdapter extends PagerAdapter {
    private List<View> viewList;

    private List<String> titleList;

    public ViewPagerAdapter() {
        viewList = new ArrayList<>();
        titleList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = viewList.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void addView(View view) {
        this.viewList.add(view);
        notifyDataSetChanged();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    /**
     * 替换所有的view
     *
     * @param viewList
     */
    public void setViews(List<View> viewList, List<String> titleList) {
        if (null == viewList || null == titleList || viewList.size() != titleList.size()) {
            return;
        }

        this.viewList = viewList;
        this.titleList = titleList;
        notifyDataSetChanged();
    }
}
