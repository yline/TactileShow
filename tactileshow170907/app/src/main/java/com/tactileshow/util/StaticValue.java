package com.tactileshow.util;

import android.text.format.Time;

import java.util.Date;

public class StaticValue
{
	
	public final static String general_info_tab_name = "一般\n信息";
	
	public final static String visual_info_tab_name = "图像\n信息";
	
	public final static String detail_info_tab_name = "原始\n信息";
	
	public final static String bodymap_info_tab_name = "人体\n地图";  //by yzy
	
	public final static String set_tab_name = "设置";
	
	public static int width, height;
	
	public static String PRESS = "PRESS";
	
	public static String TEMP = "TEMP";
	
	public static boolean temp_real_time = true;
	
	public static boolean press_real_time = true;
	
	public static Time record_time;
	
	public static double temp_max_axis = 60;
	
	public static double temp_min_axis = 10;
	
	public static double press_max_axis = 60;
	
	public static double press_min_axis = 10;
	
	public static int max_points = 100;
	
	public final static String temp_visual_info_name = "温度信息";
	
	public final static String press_visual_info_name = "湿度信息";
	
	//	public static String time_formart = "yyy-mm-dd HH:MM:ss";
	
	public static Date TimetoDate(Time t)
	{
		Date d = new Date(t.toMillis(false));
		return d;
	}
	
	public Time TimeAddOneHour(Time t)
	{
		t.hour += 1;
		t.normalize(false);
		return t;
	}
	
}
