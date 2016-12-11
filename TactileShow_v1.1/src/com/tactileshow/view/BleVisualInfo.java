package com.tactileshow.view;

import java.util.Calendar;
import java.util.Date;

import com.tactileshow.main.R;
import com.tactileshow.util.HistoryDataComputing;
import com.tactileshow.util.StaticValue;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;

public class BleVisualInfo
{
    private LineChartBuilder bleMap;
    
    private Activity context;
    
    private View view;
    
    private LinearLayout layout;
    
    private DefinedScrollView scroll;
    
    private DefinedViewPager pager;
    
    private RelativeLayout history_layout;
    
    private TabHost queryHost;
    
    private HistoryDataComputing history;
    
    private String sensor;
    
    @SuppressLint("InflateParams")
    public BleVisualInfo(Activity activity, DefinedViewPager pager)
    {
        this.context = activity;
        this.pager = pager;
        
        sensor = StaticValue.BLE;
        view = context.getLayoutInflater().inflate(R.layout.activity_visual_info, null);
        scroll = (DefinedScrollView)view.findViewById(R.id.scroll);
        layout = (LinearLayout)view.findViewById(R.id.visual_chart_layout);
        if (layout == null)
        {
            Log.e("wshg", "Null");
            return;
        }
        
        bleMap = new LineChartBuilder(context, layout, "蓝牙数据变化趋势", this.pager, scroll, sensor);
        bleMap.setYRange(StaticValue.ble_min_axis, StaticValue.ble_max_axis);
        
        history_layout = (RelativeLayout)view.findViewById(R.id.visual_history_layout);
        
        history = new HistoryDataComputing(bleMap);
        initQueryHost();
        
        final Button btn = (Button)view.findViewById(R.id.button_history_area);
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
    
    private String dateFormat(int val)
    {
        if (val >= 10)
            return "" + val;
        else
            return "0" + val;
    }
    
    private void historyListen()
    {
        
        Button query_one_hour = (Button)view.findViewById(R.id.button_one_hour);
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
                history.getHoursHistory(from, to, sensor);
                StaticValue.ble_real_time = false;
                bleMap.changeMode();
                //bleMap.setTitle("蓝牙数据历史记录(" + dateFormat(from.hour) + " : " + dateFormat(from.minute) + " - " + dateFormat(to.hour) + " : " + dateFormat(to.minute) + ")");
                bleMap.setTitle("蓝牙数据历史记录(一小时)");
                
                bleMap.setRange(from.getTime(), to.getTime());
            }
            
        });
        
        Button query_one_day = (Button)view.findViewById(R.id.button_one_day);
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
                history.getHoursHistory(from, to, sensor);
                StaticValue.ble_real_time = false;
                bleMap.changeMode();
                //bleMap.setTitle("蓝牙数据历史记录(" + dateFormat(from.hour) + " : " + dateFormat(from.minute) + " - " + dateFormat(to.hour) + " : " + dateFormat(to.minute) + ")");
                bleMap.setTitle("蓝牙数据历史记录(一天)");
                bleMap.setRange(from.getTime(), to.getTime());
            }
            
        });
        
        Button query_one_month = (Button)view.findViewById(R.id.button_one_month);
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
                history.getDaysHistory(from, to, sensor);
                StaticValue.ble_real_time = false;
                bleMap.changeMode();
                //bleMap.setTitle("蓝牙数据历史记录(" + dateFormat(from.month+1) + "-" + dateFormat(from.monthDay) + " - " + dateFormat(to.month+1) + "-" + dateFormat(to.monthDay) + ")");
                bleMap.setTitle("蓝牙数据历史记录(一月)");
                bleMap.setRange(from.getTime(), to.getTime());
            }
            
        });
        
        Button query_hour = (Button)view.findViewById(R.id.button_query_hour);
        query_hour.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                bleMap.clearHistory();
                TimeEditor fr = (TimeEditor)view.findViewById(R.id.edit_from_hour);
                TimeEditor to = (TimeEditor)view.findViewById(R.id.edit_to_hour);
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
                history.getHoursHistory(from, tot, sensor);
                StaticValue.ble_real_time = false;
                bleMap.changeMode();
                bleMap.setTitle("蓝牙数据历史记录(" + from_str + " - " + to_str + ")");
                
                bleMap.setRange(from.getTime(), tot.getTime());
            }
            
        });
        
        Button query_day = (Button)view.findViewById(R.id.button_query_day);
        query_day.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                bleMap.clearHistory();
                DateEditor fr = (DateEditor)view.findViewById(R.id.edit_from_day);
                DateEditor to = (DateEditor)view.findViewById(R.id.edit_to_day);
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
                history.getDaysHistory(from, tot, sensor);
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
        queryHost = (TabHost)view.findViewById(R.id.history_query_host);
        queryHost.setup();
        queryHost.addTab(queryHost.newTabSpec("按小时查询").setIndicator("按小时查询").setContent(R.id.one_hour_query_layout));
        queryHost.addTab(queryHost.newTabSpec("按天查询").setIndicator("按天查询").setContent(R.id.one_day_query_layout));
        queryHost.setCurrentTab(0);
    }
    
    public View getView()
    {
        return view;
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
