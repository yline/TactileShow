package com.tactileshow.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tactileshow.main.R;
import com.tactileshow.util.BroadcastMsg;
import com.tactileshow.util.Macro;
import com.tactileshow.util.StaticValue;
import com.tactileshow.view.BleVisualInfo;
import com.tactileshow.view.DefinedPagerAdapter;
import com.tactileshow.view.DefinedViewPager;
import com.tactileshow.view.TXTVisualInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

public class MainTabActivity extends Activity
{
    private Context context;
    
    private TabHost tabHost;
    
    private List<View> listViews;
    
    private DefinedViewPager viewPager;
    
    private DefinedPagerAdapter pagerAdapter;
    
    private BleVisualInfo bleVisual;
    
    private TXTVisualInfo txtVisual;
    
    AlertDialog.Builder builder_dl_exit;
    
    AlertDialog dl_exit;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        getScreenMetrics();
        context = this;
        
        tabHost = (TabHost)findViewById(R.id.tabhost);
        viewPager = (DefinedViewPager)findViewById(R.id.view_pager);
        listViews = new ArrayList<View>();
        bleVisual = new BleVisualInfo(this, viewPager);
        txtVisual = new TXTVisualInfo(this, viewPager);//12.5调试
        
        listViews.add(bleVisual.getView());
        listViews.add(txtVisual.getView());//12.5 调试
        
        pagerAdapter = new DefinedPagerAdapter(listViews);
        viewPager.setAdapter(pagerAdapter);
        
        tabHost.setup();
        
        tabHost.addTab(tabHost.newTabSpec(StaticValue.visual_info_tab_name)
            .setIndicator(StaticValue.visual_info_tab_name)
            .setContent(R.id.view1));
        tabHost.addTab(tabHost.newTabSpec(StaticValue.visual_info_tab_txt_name)
            .setIndicator(StaticValue.visual_info_tab_txt_name)
            .setContent(R.id.view1)); //12.5调试
        
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
                
                if (StaticValue.visual_info_tab_name.equals(tabId))
                {
                    viewPager.setCurrentItem(0);
                }
                else if (StaticValue.visual_info_tab_txt_name.equals(tabId))
                {
                    viewPager.setCurrentItem(1);
                }
            }
        });
        
        ExDialog_Init();
        
        IntentFilter filter = new IntentFilter();
        filter.addAction(Macro.BROADCAST_ADDRESS);
        registerReceiver(mGattUpdateReceiver, filter);
    }
    
    private void ExDialog_Init()
    {
        builder_dl_exit = new AlertDialog.Builder(context);
        builder_dl_exit.setTitle("是否退出");
        
        builder_dl_exit.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        
        builder_dl_exit.setPositiveButton("立即退出", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                ((Activity)context).finish();
            }
        });
        
        builder_dl_exit.setNeutralButton("ֱ保存退出", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                Macro.SETTING_EXIT_DIRECTLY = true;
                ((Activity)context).finish();
            }
        });
    }
    
    public void ExDialog_Show()
    {
        dl_exit = builder_dl_exit.show();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            ExDialog_Show();
            return true;
        }
        else
            return super.onKeyDown(keyCode, event);
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        bleVisual.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedState)
    {
        super.onRestoreInstanceState(savedState);
        bleVisual.onRestoreInstanceState(savedState);
    }
    
    private void getScreenMetrics()
    {
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        StaticValue.width = dm.widthPixels;
        StaticValue.height = dm.heightPixels;
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
    }
    
    public void setBle(Date t, String str)
    {
        try
        {
            double data = Double.parseDouble(str);
            bleVisual.setBle(StaticValue.xcnt++, data);
        }
        catch (NumberFormatException e)
        {
            Log.e("toy", "Received an error format data!");
        }
    }
    
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver()
    {
        
        @Override
        public void onReceive(Context arg0, Intent arg1)
        {
            final String action = arg1.getStringExtra("msg");
            if (action == null)
                return;
            BroadcastMsg bm = new BroadcastMsg(action);
            if (bm.getSensor() == null)
            {
                Log.e("toy", "Receive error msg format or msg is null");
            }
            else if (bm.getSensor().equals(StaticValue.BLE))
            {
                setBle(bm.getTime(), bm.getData());
                StaticValue.data_file.writeData(bm.getTime(), StaticValue.BLE, bm.getData());
            }
        }
    };
}
