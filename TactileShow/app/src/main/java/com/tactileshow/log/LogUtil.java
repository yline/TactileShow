package com.tactileshow.log;

import java.util.Locale;

/**
 * 该工具,默认给_LibSDK使用,因此tag,全免
 * simple introduction
 * 格式:x->{[类名.方法名(L:行数)]: }功能tag -> 信息content
 * 
 * tag 功能 目的: 某一个功能模块的tag
 * content 具体信息 目的: "start"、"end"、"number" = number 等类似信息
 *
 * 级别:
 * v 主流程信息
 * d 调试信息
 * i 主流程信息注意级别
 * w 警告级别
 * e 错误级别
 * 
 * {}这里统一加一个开关,设置为信息安全
 * 
 * @author YLine 2016-5-1
 * @version
 */
public final class LogUtil
{
    /** log 开关 */
    private static final boolean isLog = true;
    
    /** log 是否定位 */
    private static final boolean isLogLocation = true;
    
    /** 默认 tag, 为_libsdk准备 */
    private static final String TAG_USER_FOR_LIBSDK = "libsdk";
    
    /** tag 默认格式 */
    private static final String TAG_DEFAULT = "x->";
    
    /** tag 定位  默认格式 */
    private static final String TAG_DEFAULT_LOCATION = "x->%s.%s(L:%d): ";
    
    /** msg 默认格式 */
    private static final String MSG_DEFAULT = TAG_USER_FOR_LIBSDK + " -> %s";
    
    /** log trace 抛出的位置,两层,即:使用该工具的当前位置,作为默认 */
    private static final int LOG_LOCATION_NOW = 2;
    
    /** log trace 抛出的位置,两层,即:使用该工具的子类的位置 */
    public static final int LOG_LOCATION_PARENT = 3;
    
    private static String generateTag(int location)
    {
        if (isLogLocation)
        {
            StackTraceElement caller = new Throwable().getStackTrace()[location];
            String clazzName = caller.getClassName();
            clazzName = clazzName.substring(clazzName.lastIndexOf(".") + 1);
            
            return String.format(Locale.CHINA,
                TAG_DEFAULT_LOCATION,
                clazzName,
                caller.getMethodName(),
                caller.getLineNumber());
        }
        else
        {
            return TAG_DEFAULT;
        }
    }
    
    /**
     * @param content   内容
     */
    public static void v(String content)
    {
        if (isLog)
        {
            android.util.Log.v(generateTag(LOG_LOCATION_NOW), String.format(MSG_DEFAULT, content));
        }
    }
    
    /** 
     * @param content   内容
     * @param location  定位位置
     */
    public static void v(String content, int location)
    {
        if (isLog)
        {
            android.util.Log.v(generateTag(location), String.format(MSG_DEFAULT, content));
        }
    }
    
    /**
     * @param content   内容
     */
    public static void d(String content)
    {
        if (isLog)
        {
            android.util.Log.d(generateTag(LOG_LOCATION_NOW), String.format(MSG_DEFAULT, content));
        }
    }
    
    /** 
     * @param content   内容
     * @param location  定位位置
     */
    public static void d(String content, int location)
    {
        if (isLog)
        {
            android.util.Log.d(generateTag(location), String.format(MSG_DEFAULT, content));
        }
    }
    
    /**
     * @param content   内容
     */
    public static void i(String content)
    {
        if (isLog)
        {
            android.util.Log.i(generateTag(LOG_LOCATION_NOW), String.format(MSG_DEFAULT, content));
        }
    }
    
    /** 
     * @param content   内容
     * @param location  定位位置
     */
    public static void i(String content, int location)
    {
        if (isLog)
        {
            android.util.Log.i(generateTag(location), String.format(MSG_DEFAULT, content));
        }
    }
    
    /**
     * @param content   内容
     * @param tr        错误信息
     */
    public static void i(String content, Throwable tr)
    {
        if (isLog)
        {
            android.util.Log.i(generateTag(LOG_LOCATION_NOW), String.format(MSG_DEFAULT, content), tr);
        }
    }
    
    /**
     * @param content   内容
     * @param location  定位位置
     * @param tr        错误信息
     */
    public static void i(String content, int location, Throwable tr)
    {
        if (isLog)
        {
            android.util.Log.i(generateTag(location), String.format(MSG_DEFAULT, content), tr);
        }
    }
    
    /**
     * @param content   内容
     */
    public static void w(String content)
    {
        if (isLog)
        {
            android.util.Log.w(generateTag(LOG_LOCATION_NOW), String.format(MSG_DEFAULT, content));
        }
    }
    
    /** 
     * @param content   内容
     * @param location  定位位置
     */
    public static void w(String content, int location)
    {
        if (isLog)
        {
            android.util.Log.w(generateTag(location), String.format(MSG_DEFAULT, content));
        }
    }
    
    /**
     * @param content   内容
     */
    public static void e(String content)
    {
        if (isLog)
        {
            android.util.Log.e(generateTag(LOG_LOCATION_NOW), String.format(MSG_DEFAULT, content));
        }
    }
    
    /** 
     * @param content   内容
     * @param location  定位位置
     */
    public static void e(String content, int location)
    {
        if (isLog)
        {
            android.util.Log.e(generateTag(location), String.format(MSG_DEFAULT, content));
        }
    }
    
    /**
     * @param content   内容
     * @param tr        错误信息
     */
    public static void e(String content, Throwable tr)
    {
        if (isLog)
        {
            android.util.Log.e(generateTag(LOG_LOCATION_NOW), String.format(MSG_DEFAULT, content), tr);
        }
    }
    
    /**
     * @param content   内容
     * @param location  定位位置
     * @param tr        错误信息
     */
    public static void e(String content, int location, Throwable tr)
    {
        if (isLog)
        {
            android.util.Log.e(generateTag(location), String.format(MSG_DEFAULT, content), tr);
        }
    }
    
}
