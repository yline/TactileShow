package com.tactileshow.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class DefinedViewPager extends ViewPager{
	
	private Context context;
    private boolean willIntercept = true;
    
    public DefinedViewPager(Context context) {
            super(context);
            this.context = context;
    }
    
    public DefinedViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.context = context;
    }

    
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
            if(willIntercept){
                    //����ط�ֱ�ӷ���true��ܿ�
                    return super.onInterceptTouchEvent(arg0);
            }else{
                    return false;
            }
            
    }

    /**
     * ����ViewPager�Ƿ����ص���¼�
     * @param value if true, ViewPager���ص���¼�
     *                                 if false, ViewPager�����ܻ�����ViewPager����View���Ի�õ���¼�
     *                                 ��Ҫ��Ӱ��ĵ���¼�Ϊ���򻬶�
     *
     */
    public void setTouchIntercept(boolean value){
            willIntercept = value;
    }
    

}
