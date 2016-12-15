package com.tactileshow.util;

import java.util.Calendar;

public class StaticValue
{
    public static final String BLE = "BLE";
    
    /** 当前显示的是否是 蓝牙 实时信息 */
    public static boolean ble_real_time = true;
    
    public static Calendar record_time = Calendar.getInstance();
    
    public static double xcnt = 0;
}
