package com.tactileshow.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ViewPagerAdapter extends PagerAdapter
{
    private List<View> viewList;
    
    public ViewPagerAdapter()
    {
        viewList = new ArrayList<View>();
    }
    
    @Override
    public int getCount()
    {
        return viewList.size();
    }
    
    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return (view == object);
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, final int position)
    {
        View view = viewList.get(position);
        container.addView(view);
        return view;
    }
    
    /**
     * 这里不能重写原来的方法,因为一定环境下不支持
     *
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((View)object);
    }
    
    public void addView(View view)
    {
        this.viewList.add(view);
        notifyDataSetChanged();
    }
    
    /**
     * 添加一个View
     *
     * @param postition the index at which to insert.
     * @param view
     */
    public void addView(int postition, View view)
    {
        this.viewList.add(postition, view);
        notifyDataSetChanged();
    }
    
    /**
     * 添加viewList
     * @param viewList
     * @return
     */
    public boolean addViewAll(List<View> viewList)
    {
        boolean result = this.viewList.addAll(viewList);
        notifyDataSetChanged();
        return result;
    }
    
    /**
     * 移除一个View
     *
     * @param position the index at which to insert.
     */
    public void removeView(int position)
    {
        this.viewList.remove(position);
        notifyDataSetChanged();
    }
    
    public void removeView(View view)
    {
        this.viewList.remove(view);
        notifyDataSetChanged();
    }
    
    /**
     * 替换所有的view
     *
     * @param viewList
     */
    public void setViews(List<View> viewList)
    {
        this.viewList = viewList;
        notifyDataSetChanged();
    }
}
