package com.tactileshow.viewhelper;

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

import com.tactileshow.R;
import com.tactileshow.util.HistoryDataComputing;
import com.tactileshow.util.StaticValue;
import com.tactileshow.view.DateEditText;
import com.tactileshow.view.DefinedScrollView;
import com.tactileshow.view.DefinedViewPager;
import com.tactileshow.view.TimeEditText;

import java.util.Calendar;
import java.util.Date;

public class BleViewHelper
{
	private static final double BLE_MIN_AXIS = -32768;

	private static final double BLE_MAX_AXIS = 32767;

	private LineChartBuilder lineChartBuilder;

	private View contentView;

	private HistoryDataComputing history;

	@SuppressLint("InflateParams")
	public BleViewHelper(Context context, DefinedViewPager pager)
	{
		contentView = LayoutInflater.from(context).inflate(R.layout.view_maintab_ble, null);

		initChartView(context, pager, contentView);

		history = new HistoryDataComputing(lineChartBuilder);

		initHistoryView(contentView, lineChartBuilder);
	}

	/**
	 * 初始化与 Chart相关常量
	 * @param context
	 * @param pager
	 * @param view
	 */
	private void initChartView(Context context, DefinedViewPager pager, View view)
	{
		DefinedScrollView scroll = (DefinedScrollView) view.findViewById(R.id.scroll);
		LinearLayout chartLayout = (LinearLayout) view.findViewById(R.id.visual_chart_layout);

		lineChartBuilder = new LineChartBuilder(context, chartLayout, pager, scroll);
		lineChartBuilder.setYRange(BLE_MIN_AXIS, BLE_MAX_AXIS);
	}

	/**
	 * 初始化 与 History Query相关的量,并设置好与Chart的关联
	 * @param view
	 * @param chartBuilder
	 */
	private void initHistoryView(final View view, final LineChartBuilder chartBuilder)
	{
		// 打开用的 Button
		final RelativeLayout historyRelativeLayout = (RelativeLayout) view.findViewById(R.id.visual_history_layout);
		final Button btnSwitch = (Button) view.findViewById(R.id.button_history_area);
		btnSwitch.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				chartBuilder.changeMode();
				if (historyRelativeLayout.getVisibility() == View.INVISIBLE) // 当前显示的是实时信息，变成现实历史信息
				{
					historyRelativeLayout.setVisibility(View.VISIBLE);

					btnSwitch.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.history_area_visible, 0);
					btnSwitch.setText(R.string.label_realtime_area_str);
				}
				else // 当前显示的是历史信息，变成显示实时信息
				{
					historyRelativeLayout.setVisibility(View.INVISIBLE);

					btnSwitch.setCompoundDrawablesRelativeWithIntrinsicBounds(0,
							0,
							R.drawable.history_area_unvisible,
							0);
					btnSwitch.setText(R.string.label_history_area_str);

					StaticValue.ble_real_time = true;
					chartBuilder.changeMode();
					chartBuilder.setTitle("蓝牙数据变化趋势");
				}
			}
		});

		// 选择日期 选择方式的 TabHost
		TabHost queryHost = (TabHost) view.findViewById(R.id.history_query_host);
		queryHost.setup();
		queryHost.addTab(queryHost.newTabSpec("按小时查询").setIndicator("按小时查询").setContent(R.id.one_hour_query_layout));
		queryHost.addTab(queryHost.newTabSpec("按天查询").setIndicator("按天查询").setContent(R.id.one_day_query_layout));
		queryHost.setCurrentTab(0);

		// 每个Button对应的点击事件
		view.findViewById(R.id.button_one_hour).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				chartBuilder.clearHistory();
				Calendar c = Calendar.getInstance();
				Date to = c.getTime();
				c.add(Calendar.MINUTE, -60);
				Date from = c.getTime();
				//              Time from = new Time(); Time to = new Time();
				//              from.setToNow(); to.setToNow();
				//              from.minute -= 60;
				//              from.normalize(false);
				history.getHoursHistory(from, to, StaticValue.BLE);
				StaticValue.ble_real_time = false;
				chartBuilder.changeMode();
				//chartBuilder.setTitle("蓝牙数据历史记录(" + dateFormat(from.hour) + " : " + dateFormat(from.minute) + " - " + dateFormat(to.hour) + " : " + dateFormat(to.minute) + ")");
				chartBuilder.setTitle("蓝牙数据历史记录(一小时)");

				chartBuilder.setXRange(from.getTime(), to.getTime());
			}
		});

		view.findViewById(R.id.button_one_day).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				chartBuilder.clearHistory();
				//              Time from = new Time(); Time to = new Time();
				//              from.setToNow(); to.setToNow();
				//              from.hour = from.minute = from.second = 0;
				//              from.normalize(false);
				Calendar c = Calendar.getInstance();
				Date to = c.getTime();
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				Date from = c.getTime();
				history.getHoursHistory(from, to, StaticValue.BLE);
				StaticValue.ble_real_time = false;
				chartBuilder.changeMode();
				//chartBuilder.setTitle("蓝牙数据历史记录(" + dateFormat(from.hour) + " : " + dateFormat(from.minute) + " - " + dateFormat(to.hour) + " : " + dateFormat(to.minute) + ")");
				chartBuilder.setTitle("蓝牙数据历史记录(一天)");
				chartBuilder.setXRange(from.getTime(), to.getTime());
			}
		});

		view.findViewById(R.id.button_one_month).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				chartBuilder.clearHistory();
				//              Time from = new Time(); Time to = new Time();
				//              from.setToNow(); to.setToNow();
				//              from.monthDay = 1;from.hour = from.minute = from.second = 0;
				//              from.normalize(false);
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
				chartBuilder.changeMode();
				//chartBuilder.setTitle("蓝牙数据历史记录(" + dateFormat(from.month+1) + "-" + dateFormat(from.monthDay) + " - " + dateFormat(to.month+1) + "-" + dateFormat(to.monthDay) + ")");
				chartBuilder.setTitle("蓝牙数据历史记录(一月)");
				chartBuilder.setXRange(from.getTime(), to.getTime());
			}
		});

		view.findViewById(R.id.button_query_hour).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				chartBuilder.clearHistory();
				TimeEditText fr = (TimeEditText) view.findViewById(R.id.edit_from_hour);
				TimeEditText to = (TimeEditText) view.findViewById(R.id.edit_to_hour);
				String from_str = fr.getText().toString(), to_str = to.getText().toString();
				Calendar c = Calendar.getInstance();
				//              Time from = new Time(), tot = new Time();from.setToNow(); tot.setToNow();
				String[] pars = from_str.split(" : ");
				c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(pars[0]));
				c.set(Calendar.MINUTE, Integer.parseInt(pars[1]));
				Date from = c.getTime();
				//              from.hour = Integer.parseInt(pars[0]); from.minute = Integer.parseInt(pars[1]);
				pars = to_str.split(" : ");
				c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(pars[0]));
				c.set(Calendar.MINUTE, Integer.parseInt(pars[1]));
				Date tot = c.getTime();
				//              tot.hour = Integer.parseInt(pars[0]); tot.minute = Integer.parseInt(pars[1]);
				history.getHoursHistory(from, tot, StaticValue.BLE);
				StaticValue.ble_real_time = false;
				chartBuilder.changeMode();
				chartBuilder.setTitle("蓝牙数据历史记录(" + from_str + " - " + to_str + ")");

				chartBuilder.setXRange(from.getTime(), tot.getTime());
			}
		});

		view.findViewById(R.id.button_query_day).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				chartBuilder.clearHistory();
				DateEditText fr = (DateEditText) view.findViewById(R.id.edit_from_day);
				DateEditText to = (DateEditText) view.findViewById(R.id.edit_to_day);
				String from_str = fr.getText().toString(), to_str = to.getText().toString();
				Calendar c = Calendar.getInstance();
				//              Time from = new Time(), tot = new Time();from.setToNow(); tot.setToNow();from.hour = 0; tot.hour = 23;
				String[] pars = from_str.split("-");
				c.set(Calendar.MONTH, Integer.parseInt(pars[1]) - 1);
				c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(pars[2]));
				Date from = c.getTime();
				//              from.month = Integer.parseInt(pars[1]) - 1; from.monthDay = Integer.parseInt(pars[2]);
				pars = to_str.split("-");
				c.set(Calendar.MONTH, Integer.parseInt(pars[1]) - 1);
				c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(pars[2]));
				Date tot = c.getTime();
				//              tot.month = Integer.parseInt(pars[1]) - 1; tot.monthDay = Integer.parseInt(pars[2]);
				history.getDaysHistory(from, tot, StaticValue.BLE);
				StaticValue.ble_real_time = false;
				chartBuilder.changeMode();
				chartBuilder.setTitle("蓝牙数据历史记录(" + from_str + " - " + to_str + ")");

				chartBuilder.setXRange(from.getTime(), tot.getTime());
				Log.e("wshg", "from: " + from_str + "; to: " + to_str);
			}
		});
	}

	public View getView()
	{
		return contentView;
	}

	public void setBle(double t, double data)
	{
		lineChartBuilder.addData(t, data);
		//Log.e("wshg", "set temp visual. data = " + data);
	}

	public void onSaveInstanceState(Bundle outState)
	{
		lineChartBuilder.onSaveInstanceState(outState);
	}

	public void onRestoreInstanceState(Bundle savedState)
	{
		lineChartBuilder.onRestoreInstanceState(savedState);
	}

}
