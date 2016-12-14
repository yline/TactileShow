package com.tactileshow.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tactileshow.adapter.ViewPagerAdapter;
import com.tactileshow.application.IApplication;
import com.tactileshow.base.BaseActivity;
import com.tactileshow.bean.BleReceiverBean;
import com.tactileshow.main.R;
import com.tactileshow.util.DataFile;
import com.tactileshow.util.StaticValue;
import com.tactileshow.view.DefinedViewPager;
import com.tactileshow.viewhelper.BleViewHelper;
import com.tactileshow.viewhelper.TXTViewHelper;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

public class MainTabActivity extends BaseActivity
{
    private static final String BLE_TAB_NAME = "蓝牙图像信息";
    
    private static final String TXT_TAB_NAME = "文本图像信息";
    
    private TabHost tabHost;
    
    private DefinedViewPager viewPager;
    
    private ViewPagerAdapter pagerAdapter;
    
    private BleViewHelper bleViewHelper;
    
    private TXTViewHelper txtViewHelper;
    
    private AlertDialog.Builder exitBuilder;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintab);
        
        initView();
        
        initExitDialog();
        
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.BROADCAST_ADDRESS);
        registerReceiver(mGattUpdateReceiver, filter);
    }
    
    private void initView()
    {
        tabHost = (TabHost)findViewById(R.id.tabhost);
        viewPager = (DefinedViewPager)findViewById(R.id.view_pager);
        
        pagerAdapter = new ViewPagerAdapter();
        pagerAdapter.addViewAll(getViewList());
        viewPager.setAdapter(pagerAdapter);
        
        tabHost.setup();
        
        tabHost.addTab(tabHost.newTabSpec(BLE_TAB_NAME).setIndicator(BLE_TAB_NAME).setContent(R.id.view1));
        tabHost.addTab(tabHost.newTabSpec(TXT_TAB_NAME).setIndicator(TXT_TAB_NAME).setContent(R.id.view1));
        
        viewPager.setOnPageChangeListener(new OnPageChangeListener()
        {
            @Override
            public void onPageSelected(int position)
            {
                tabHost.setCurrentTab(position);
            }
            
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2)
            {
            }
            
            @Override
            public void onPageScrollStateChanged(int arg0)
            {
            }
        });
        
        tabHost.setOnTabChangedListener(new OnTabChangeListener()
        {
            @Override
            public void onTabChanged(String tabId)
            {
                if (BLE_TAB_NAME.equals(tabId))
                {
                    viewPager.setCurrentItem(0);
                }
                else if (TXT_TAB_NAME.equals(tabId))
                {
                    viewPager.setCurrentItem(1);
                }
            }
        });
    }
    
    private List<View> getViewList()
    {
        List<View> viewList = new ArrayList<View>();
        
        // 12.5调试
        bleViewHelper = new BleViewHelper(this, viewPager);
        txtViewHelper = new TXTViewHelper(this, viewPager);
        
        //12.5 调试
        viewList.add(bleViewHelper.getView());
        viewList.add(txtViewHelper.getView());
        
        return viewList;
    }
    
    private void initExitDialog()
    {
        exitBuilder = new AlertDialog.Builder(this);
        exitBuilder.setTitle("是否退出");
        
        exitBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        
        exitBuilder.setNeutralButton("ֱ退出应用", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                IApplication.finishActivity();
            }
        });
        
        exitBuilder.setPositiveButton("结束测试", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                finish();
            }
        });
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            exitBuilder.show();
            return true;
        }
        else
        {
            return super.onKeyDown(keyCode, event);
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        bleViewHelper.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedState)
    {
        super.onRestoreInstanceState(savedState);
        bleViewHelper.onRestoreInstanceState(savedState);
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
    }
    
    /**
     * 用于显示 
     * @param date
     * @param str
     */
    private void setBle(Date date, String str)
    {
        try
        {
            double data = Double.parseDouble(str);
            bleViewHelper.setBle(StaticValue.xcnt++, data);
        }
        catch (NumberFormatException e)
        {
            Log.e("toy", "Received an error format data!");
        }
    }
    
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver()
    {
        
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getStringExtra("msg");
            if (action == null)
            {
                return;
            }
            
            BleReceiverBean bm = new BleReceiverBean(action);
            if (bm.getSensor() == null)
            {
                Log.e("toy", "Receive error msg format or msg is null");
            }
            else if (bm.getSensor().equals(StaticValue.BLE))
            {
                setBle(bm.getTime(), bm.getData());
                DataFile.getInstance().writeData(bm.getTime(), StaticValue.BLE, bm.getData());
            }
        }
    };
    
    public static void actionStart(Context context)
    {
        context.startActivity(new Intent(context, MainTabActivity.class));
    }
}
