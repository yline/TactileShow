package com.tactileshow.viewhelper;

import java.util.Calendar;
import java.util.Date;

import com.tactileshow.main.R;
import com.tactileshow.util.HistoryDataComputing;
import com.tactileshow.util.StaticValue;
import com.tactileshow.view.DateEditText;
import com.tactileshow.view.DefinedScrollView;
import com.tactileshow.view.DefinedViewPager;
import com.tactileshow.view.TimeEditText;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;

public class BleViewHelper
{
    private static final double BLE_MIN_AXIS = -32768;
    
    private static final double BLE_MAX_AXIS = 32767;
    
    private LineChartBuilder bleMap;
    
    private View contentView;
    
    private LinearLayout layout;
    
    private DefinedScrollView scroll;
    
    private RelativeLayout history_layout;
    
    private TabHost queryHost;
    
    private HistoryDataComputing history;
    
    @SuppressLint("InflateParams")
    public BleViewHelper(Context context, DefinedViewPager pager)
    {
        contentView = LayoutInflater.from(context).inflate(R.layout.view_maintab_ble, null);
        
        scroll = (DefinedScrollView)contentView.findViewById(R.id.scroll);
        layout = (LinearLayout)contentView.findViewById(R.id.visual_chart_layout);
        
        if (layout == null)
        {
            Log.e("wshg", "Null");
            return;
        }
        
        bleMap = new LineChartBuilder(context, layout, "蓝牙数据变化趋势", pager, scroll, StaticValue.BLE);
        bleMap.setYRange(BLE_MIN_AXIS, BLE_MAX_AXIS);
        
        history_layout = (RelativeLayout)contentView.findViewById(R.id.visual_history_layout);
        
        history = new HistoryDataComputing(bleMap);
        initQueryHost();
        
        final Button btn = (Button)contentView.findViewById(R.id.button_history_area);
        btn.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                bleMap.changeMode();
                if (history_layout.getVisibility() == View.INVISIBLE)
                {//当前显示的是实时信息，变成现实历史信息
                    btn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.history_area_visible, 0);
                    history_layout.setVisibility(View.VISIBLE);
                    btn.setText(R.string.label_realtime_area_str);
                }
                else
                {//当前显示的是历史信息，变成显示实时信息
                    history_layout.setVisibility(View.INVISIBLE);
                    btn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.history_area_unvisible, 0);
                    btn.setText(R.string.label_history_area_str);
                    StaticValue.ble_real_time = true;
                    bleMap.changeMode();
                    bleMap.setTitle("蓝牙数据变化趋势");
                }
            }
            
        });
        
        historyListen();
    }
    
    private void historyListen()
    {
        Button query_one_hour = (Button)contentView.findViewById(R.id.button_one_hour);
        query_one_hour.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                bleMap.clearHistory();
                Calendar c = Calendar.getInstance();
                Date to = c.getTime();
                c.add(Calendar.MINUTE, -60);
                Date from = c.getTime();
                //				Time from = new Time(); Time to = new Time();
                //				from.setToNow(); to.setToNow();
                //				from.minute -= 60;
                //				from.normalize(false);
                history.getHoursHistory(from, to, StaticValue.BLE);
                StaticValue.ble_real_time = false;
                bleMap.changeMode();
                //bleMap.setTitle("蓝牙数据历史记录(" + dateFormat(from.hour) + " : " + dateFormat(from.minute) + " - " + dateFormat(to.hour) + " : " + dateFormat(to.minute) + ")");
                bleMap.setTitle("蓝牙数据历史记录(一小时)");
                
                bleMap.setRange(from.getTime(), to.getTime());
            }
            
        });
        
        Button query_one_day = (Button)contentView.findViewById(R.id.button_one_day);
        query_one_day.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                bleMap.clearHistory();
                //				Time from = new Time(); Time to = new Time();
                //				from.setToNow(); to.setToNow();
                //				from.hour = from.minute = from.second = 0;
                //				from.normalize(false);
                Calendar c = Calendar.getInstance();
                Date to = c.getTime();
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                Date from = c.getTime();
                history.getHoursHistory(from, to, StaticValue.BLE);
                StaticValue.ble_real_time = false;
                bleMap.changeMode();
                //bleMap.setTitle("蓝牙数据历史记录(" + dateFormat(from.hour) + " : " + dateFormat(from.minute) + " - " + dateFormat(to.hour) + " : " + dateFormat(to.minute) + ")");
                bleMap.setTitle("蓝牙数据历史记录(一天)");
                bleMap.setRange(from.getTime(), to.getTime());
            }
            
        });
        
        Button query_one_month = (Button)contentView.findViewById(R.id.button_one_month);
        query_one_month.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                bleMap.clearHistory();
                //				Time from = new Time(); Time to = new Time();
                //				from.setToNow(); to.setToNow();
                //				from.monthDay = 1;from.hour = from.minute = from.second = 0;
                //				from.normalize(false);
                Calendar c = Calendar.getInstance();
                Date to = c.getTime();
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                Date from = c.getTime();
                history.getDaysHistory(from, to, StaticValue.BLE);
                StaticValue.ble_real_time = false;
                bleMap.changeMode();
                //bleMap.setTitle("蓝牙数据历史记录(" + dateFormat(from.month+1) + "-" + dateFormat(from.monthDay) + " - " + dateFormat(to.month+1) + "-" + dateFormat(to.monthDay) + ")");
                bleMap.setTitle("蓝牙数据历史记录(一月)");
                bleMap.setRange(from.getTime(), to.getTime());
            }
            
        });
        
        Button query_hour = (Button)contentView.findViewById(R.id.button_query_hour);
        query_hour.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                bleMap.clearHistory();
                TimeEditText fr = (TimeEditText)contentView.findViewById(R.id.edit_from_hour);
                TimeEditText to = (TimeEditText)contentView.findViewById(R.id.edit_to_hour);
                String from_str = fr.getText().toString(), to_str = to.getText().toString();
                Calendar c = Calendar.getInstance();
                //				Time from = new Time(), tot = new Time();from.setToNow(); tot.setToNow();
                String[] pars = from_str.split(" : ");
                c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(pars[0]));
                c.set(Calendar.MINUTE, Integer.parseInt(pars[1]));
                Date from = c.getTime();
                //				from.hour = Integer.parseInt(pars[0]); from.minute = Integer.parseInt(pars[1]);
                pars = to_str.split(" : ");
                c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(pars[0]));
                c.set(Calendar.MINUTE, Integer.parseInt(pars[1]));
                Date tot = c.getTime();
                //				tot.hour = Integer.parseInt(pars[0]); tot.minute = Integer.parseInt(pars[1]);
                history.getHoursHistory(from, tot, StaticValue.BLE);
                StaticValue.ble_real_time = false;
                bleMap.changeMode();
                bleMap.setTitle("蓝牙数据历史记录(" + from_str + " - " + to_str + ")");
                
                bleMap.setRange(from.getTime(), tot.getTime());
            }
            
        });
        
        Button query_day = (Button)contentView.findViewById(R.id.button_query_day);
        query_day.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                bleMap.clearHistory();
                DateEditText fr = (DateEditText)contentView.findViewById(R.id.edit_from_day);
                DateEditText to = (DateEditText)contentView.findViewById(R.id.edit_to_day);
                String from_str = fr.getText().toString(), to_str = to.getText().toString();
                Calendar c = Calendar.getInstance();
                //				Time from = new Time(), tot = new Time();from.setToNow(); tot.setToNow();from.hour = 0; tot.hour = 23;
                String[] pars = from_str.split("-");
                c.set(Calendar.MONTH, Integer.parseInt(pars[1]) - 1);
                c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(pars[2]));
                Date from = c.getTime();
                //				from.month = Integer.parseInt(pars[1]) - 1; from.monthDay = Integer.parseInt(pars[2]);
                pars = to_str.split("-");
                c.set(Calendar.MONTH, Integer.parseInt(pars[1]) - 1);
                c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(pars[2]));
                Date tot = c.getTime();
                //				tot.month = Integer.parseInt(pars[1]) - 1; tot.monthDay = Integer.parseInt(pars[2]);
                history.getDaysHistory(from, tot, StaticValue.BLE);
                StaticValue.ble_real_time = false;
                bleMap.changeMode();
                bleMap.setTitle("蓝牙数据历史记录(" + from_str + " - " + to_str + ")");
                
                bleMap.setRange(from.getTime(), tot.getTime());
                Log.e("wshg", "from: " + from_str + "; to: " + to_str);
            }
            
        });
    }
    
    private void initQueryHost()
    {
        queryHost = (TabHost)contentView.findViewById(R.id.history_query_host);
        queryHost.setup();
        queryHost.addTab(queryHost.newTabSpec("按小时查询").setIndicator("按小时查询").setContent(R.id.one_hour_query_layout));
        queryHost.addTab(queryHost.newTabSpec("按天查询").setIndicator("按天查询").setContent(R.id.one_day_query_layout));
        queryHost.setCurrentTab(0);
    }
    
    public View getView()
    {
        return contentView;
    }
    
    public void repaint()
    {
        bleMap.init();
    }
    
    public void setBle(double t, double data)
    {
        bleMap.addData(t, data);//Log.e("wshg", "set temp visual. data = " + data);
    }
    
    public void setMaxPoints(int maxPoints)
    {
        bleMap.setMaxPoints(maxPoints);
    }
    
    public void onSaveInstanceState(Bundle outState)
    {
        bleMap.onSaveInstanceState(outState);
        //	humMap.onSaveInstanceState(outState);
    }
    
    public void onRestoreInstanceState(Bundle savedState)
    {
        bleMap.onRestoreInstanceState(savedState);
        //	humMap.onRestoreInstanceState(savedState);
    }
    
}
