package com.tactileshow.log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.tactileshow.application.IApplication;

import android.text.TextUtils;

/**
 * 缺陷:在初始化第一次调用的时候,会确立目录以及配置参数
 * 因此,不能再initConig之前,调用一次该日志文件,否则:配置失效
 * 想要区分应用时,可以采用区分目录或修改"x"的方式
 * simple introduction
 * @author YLine 2016-5-21 -> 下午6:50:25
 */
public final class LogFileUtil
{
    /** LogFileUtil 错误日志tag */
    private static final String TAG_ERROR = "LogFileUtil error -> ";
    
    /** 写入文件编号,默认 */
    private static final int START_COUNT = 0;
    
    /** 写入文件最大编号 */
    private static final int MAX_COUNT = 20;
    
    /** 写入文件,每个文件大小512kb */
    private static final int MAX_SIZE_OF_TXT = 4 * 512 * 1024;
    
    /** 写入文件,文件路径 */
    private static final String LOG_FILE_PATH =
        IApplication.getBaseConfig().getFileParentPath() + IApplication.getBaseConfig().getLogFilePath();
    
    /** 写入文件,路径下保存的文件名称 */
    private static final String LOG_FILE_TXT_NAME = "_log.txt";
    
    // 三个开关
    
    /** log 开关 */
    private static final boolean isLog = IApplication.getBaseConfig().isLog();
    
    /** 是否写到文件 */
    private static final boolean isToFile = IApplication.getBaseConfig().isLogToFile();
    
    /** 是否定位 */
    private static final boolean isLogLocation = IApplication.getBaseConfig().isLogLocation();
    
    /** 正常的LogCat失效时，使用sysOut */
    private static final boolean isLogSystem = IApplication.getBaseConfig().isLogSystem();
    
    /** LogFileUtil.m后缀的是否输出 */
    private static final boolean isLogLib = IApplication.getBaseConfig().isLogLib();
    
    // 信息格式
    
    /** 默认自带前缀 */
    private static final String TAG_DEFAULT = "x->";
    
    /** tag 定位  默认格式 */
    private static final String TAG_DEFAULT_LOCATION = TAG_DEFAULT + "%s.%s(L:%d): ";
    
    /** msg 默认格式 */
    private static final String MSG_DEFAULT = "%s -> %s";
    
    /** tag 文件默认格式<日期,级别> */
    private static final String TAG_FILE_DEFAULT = "x->%s: %s/";
    
    /** tag 文件定位默认格式 */
    private static final String TAG_FILE_DEFAULT_LOCATION = "x->%s: %s/%s.%s(L:%d): ";
    
    /** msg 文件定位默认格式 */
    private static final String MSG_FILE_DEFAULT = "%s %s -> %s";
    
    // 安全级别
    private static final String V = "V";
    
    private static final String D = "D";
    
    private static final String I = "I";
    
    private static final String W = "W";
    
    private static final String E = "E";
    
    // 找到位置
    
    /** log trace 抛出的位置,两层,即:使用该工具的当前位置,作为默认 */
    private static final int LOG_LOCATION_NOW = 2;
    
    /** log trace 抛出的位置,两层,即:使用该工具的子类的位置 */
    public static final int LOG_LOCATION_PARENT = 3;
    
    private static final String TAG_LIBSDK = "libsdk";
    
    /**
     * @param content 内容
     */
    public static void m(String content)
    {
        if (isLogLib)
        {
            if (isLog)
            {
                android.util.Log.v(generateTag(LOG_LOCATION_NOW), String.format(MSG_DEFAULT, TAG_LIBSDK, content));
            }
            
            if (isLogSystem)
            {
                System.out.println(generateTag(LOG_LOCATION_NOW) + String.format(MSG_DEFAULT, TAG_LIBSDK, content));
            }
            
            if (isToFile)
            {
                writeLogToFile(
                    String.format(MSG_FILE_DEFAULT, generateFileTag(V, LOG_LOCATION_NOW), TAG_LIBSDK, content));
            }
        }
    }
    
    /**
     * @param content  内容
     * @param location 定位位置
     */
    public static void m(String content, int location)
    {
        if (isLogLib)
        {
            if (isLog)
            {
                android.util.Log.v(generateTag(location), String.format(MSG_DEFAULT, TAG_LIBSDK, content));
            }
            
            if (isLogSystem)
            {
                System.out.println(generateTag(location) + String.format(MSG_DEFAULT, TAG_LIBSDK, content));
            }
            
            if (isToFile)
            {
                writeLogToFile(String.format(MSG_FILE_DEFAULT, generateFileTag(V, location), TAG_LIBSDK, content));
            }
        }
    }
    
    /**
     * 设置默认的标签
     * @param tag 标签
     */
    public static void v(String content)
    {
        if (isLog)
        {
            android.util.Log.v(generateTag(LOG_LOCATION_NOW), String.format(MSG_DEFAULT, "tag", content));
        }
        
        if (isLogSystem)
        {
            System.out.println(generateTag(LOG_LOCATION_NOW) + String.format(MSG_DEFAULT, "tag", content));
        }
        
        if (isToFile)
        {
            writeLogToFile(String.format(MSG_FILE_DEFAULT, generateFileTag(V, LOG_LOCATION_NOW), "tag", content));
        }
    }
    
    /**
     * @param tag     标签
     * @param content 内容
     */
    public static void v(String tag, String content)
    {
        if (isLog)
        {
            android.util.Log.v(generateTag(LOG_LOCATION_NOW), String.format(MSG_DEFAULT, tag, content));
        }
        
        if (isLogSystem)
        {
            System.out.println(generateTag(LOG_LOCATION_NOW) + String.format(MSG_DEFAULT, tag, content));
        }
        
        if (isToFile)
        {
            writeLogToFile(String.format(MSG_FILE_DEFAULT, generateFileTag(V, LOG_LOCATION_NOW), tag, content));
        }
    }
    
    /**
     * @param tag      标签
     * @param content  内容
     * @param location 定位位置
     */
    public static void v(String tag, String content, int location)
    {
        if (isLog)
        {
            android.util.Log.v(generateTag(location), String.format(MSG_DEFAULT, tag, content));
        }
        
        if (isLogSystem)
        {
            System.out.println(generateTag(location) + String.format(MSG_DEFAULT, tag, content));
        }
        
        if (isToFile)
        {
            writeLogToFile(String.format(MSG_FILE_DEFAULT, generateFileTag(V, location), tag, content));
        }
    }
    
    /**
     * @param tag     标签
     * @param content 内容
     */
    public static void d(String tag, String content)
    {
        if (isLog)
        {
            android.util.Log.d(generateTag(LOG_LOCATION_NOW), String.format(MSG_DEFAULT, tag, content));
        }
        
        if (isLogSystem)
        {
            System.out.println(generateTag(LOG_LOCATION_NOW) + String.format(MSG_DEFAULT, tag, content));
        }
        
        if (isToFile)
        {
            writeLogToFile(String.format(MSG_FILE_DEFAULT, generateFileTag(D, LOG_LOCATION_NOW), tag, content));
        }
    }
    
    /**
     * @param tag      标签
     * @param content  内容
     * @param location 定位位置
     */
    public static void d(String tag, String content, int location)
    {
        if (isLog)
        {
            android.util.Log.d(generateTag(location), String.format(MSG_DEFAULT, tag, content));
        }
        
        if (isLogSystem)
        {
            System.out.println(generateTag(location) + String.format(MSG_DEFAULT, tag, content));
        }
        
        if (isToFile)
        {
            writeLogToFile(String.format(MSG_FILE_DEFAULT, generateFileTag(D, location), tag, content));
        }
    }
    
    /**
     * @param tag     标签
     * @param content 内容
     */
    public static void i(String tag, String content)
    {
        if (isLog)
        {
            android.util.Log.i(generateTag(LOG_LOCATION_NOW), String.format(MSG_DEFAULT, tag, content));
        }
        
        if (isLogSystem)
        {
            System.out.println(generateTag(LOG_LOCATION_NOW) + String.format(MSG_DEFAULT, tag, content));
        }
        
        if (isToFile)
        {
            writeLogToFile(String.format(MSG_FILE_DEFAULT, generateFileTag(I, LOG_LOCATION_NOW), tag, content));
        }
    }
    
    /**
     * @param tag      标签
     * @param content  内容
     * @param location 定位位置
     */
    public static void i(String tag, String content, int location)
    {
        if (isLog)
        {
            android.util.Log.i(generateTag(location), String.format(MSG_DEFAULT, tag, content));
        }
        
        if (isLogSystem)
        {
            System.out.println(generateTag(location) + String.format(MSG_DEFAULT, tag, content));
        }
        
        if (isToFile)
        {
            writeLogToFile(String.format(MSG_FILE_DEFAULT, generateFileTag(I, location), tag, content));
        }
    }
    
    /**
     * @param tag     标签
     * @param content 内容
     * @param tr      错误信息
     */
    public static void i(String tag, String content, Throwable tr)
    {
        if (isLog)
        {
            android.util.Log.i(generateTag(LOG_LOCATION_NOW), String.format(MSG_DEFAULT, tag, content), tr);
        }
        
        if (isLogSystem)
        {
            System.out.println(generateTag(LOG_LOCATION_NOW) + String.format(MSG_DEFAULT, tag, content)
                + android.util.Log.getStackTraceString(tr));
        }
        
        if (isToFile)
        {
            writeLogToFile(String.format(MSG_FILE_DEFAULT, generateFileTag(I, LOG_LOCATION_NOW), tag, content) + '\n'
                + android.util.Log.getStackTraceString(tr));
        }
    }
    
    /**
     * @param tag      标签
     * @param content  内容
     * @param location 定位位置
     * @param tr       错误信息
     */
    public static void i(String tag, String content, int location, Throwable tr)
    {
        if (isLog)
        {
            android.util.Log.i(generateTag(location), String.format(MSG_DEFAULT, tag, content), tr);
        }
        
        if (isLogSystem)
        {
            System.out.println(generateTag(location) + String.format(MSG_DEFAULT, tag, content)
                + android.util.Log.getStackTraceString(tr));
        }
        
        if (isToFile)
        {
            writeLogToFile(String.format(MSG_FILE_DEFAULT, generateFileTag(I, location), tag, content) + '\n'
                + android.util.Log.getStackTraceString(tr));
        }
    }
    
    /**
     * @param tag     标签
     * @param content 内容
     */
    public static void w(String tag, String content)
    {
        if (isLog)
        {
            android.util.Log.w(generateTag(LOG_LOCATION_NOW), String.format(MSG_DEFAULT, tag, content));
        }
        
        if (isLogSystem)
        {
            System.out.println(generateTag(LOG_LOCATION_NOW) + String.format(MSG_DEFAULT, tag, content));
        }
        
        if (isToFile)
        {
            writeLogToFile(String.format(MSG_FILE_DEFAULT, generateFileTag(W, LOG_LOCATION_NOW), tag, content));
        }
    }
    
    /**
     * @param tag      标签
     * @param content  内容
     * @param location 定位位置
     */
    public static void w(String tag, String content, int location)
    {
        if (isLog)
        {
            android.util.Log.w(generateTag(location), String.format(MSG_DEFAULT, tag, content));
        }
        
        if (isLogSystem)
        {
            System.out.println(generateTag(location) + String.format(MSG_DEFAULT, tag, content));
        }
        
        if (isToFile)
        {
            writeLogToFile(String.format(MSG_FILE_DEFAULT, generateFileTag(W, location), tag, content));
        }
    }
    
    /**
     * @param tag     标签
     * @param content 内容
     */
    public static void e(String tag, String content)
    {
        if (isLog)
        {
            android.util.Log.e(generateTag(LOG_LOCATION_NOW), String.format(MSG_DEFAULT, tag, content));
        }
        
        if (isLogSystem)
        {
            System.out.println(generateTag(LOG_LOCATION_NOW) + String.format(MSG_DEFAULT, tag, content));
        }
        
        if (isToFile)
        {
            writeLogToFile(String.format(MSG_FILE_DEFAULT, generateFileTag(E, LOG_LOCATION_NOW), tag, content));
        }
    }
    
    /**
     * @param tag      标签
     * @param content  内容
     * @param location 定位位置
     */
    public static void e(String tag, String content, int location)
    {
        if (isLog)
        {
            android.util.Log.e(generateTag(location), String.format(MSG_DEFAULT, tag, content));
        }
        
        if (isLogSystem)
        {
            System.out.println(generateTag(location) + String.format(MSG_DEFAULT, tag, content));
        }
        
        if (isToFile)
        {
            writeLogToFile(String.format(MSG_FILE_DEFAULT, generateFileTag(E, location), tag, content));
        }
    }
    
    /**
     * @param tag     标签
     * @param content 内容
     * @param tr      错误信息
     */
    public static void e(String tag, String content, Throwable tr)
    {
        if (isLog)
        {
            android.util.Log.e(generateTag(LOG_LOCATION_NOW), String.format(MSG_DEFAULT, tag, content), tr);
        }
        
        if (isLogSystem)
        {
            System.out.println(generateTag(LOG_LOCATION_NOW) + String.format(MSG_DEFAULT, tag, content)
                + android.util.Log.getStackTraceString(tr));
        }
        
        if (isToFile)
        {
            writeLogToFile(String.format(MSG_FILE_DEFAULT, generateFileTag(E, LOG_LOCATION_NOW), tag, content) + '\n'
                + android.util.Log.getStackTraceString(tr));
        }
    }
    
    /**
     * @param tag      标签
     * @param content  内容
     * @param location 定位位置
     * @param tr       错误信息
     */
    public static void e(String tag, String content, int location, Throwable tr)
    {
        if (isLog)
        {
            android.util.Log.e(generateTag(location), String.format(MSG_DEFAULT, tag, content), tr);
        }
        
        if (isLogSystem)
        {
            System.out.println(generateTag(location) + String.format(MSG_DEFAULT, tag, content)
                + android.util.Log.getStackTraceString(tr));
        }
        
        if (isToFile)
        {
            writeLogToFile(String.format(MSG_FILE_DEFAULT, generateFileTag(E, location), tag, content) + '\n'
                + android.util.Log.getStackTraceString(tr));
        }
    }
    
    /**
     * 拼接日志tag,该tag专为打在eclipse,DDms上准备
     * @return
     */
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
     * 拼接 日志tag,该tag专为写入file中准备
     * @param type
     * @return
     */
    private static String generateFileTag(String type, int location)
    {
        // 日期 时间: 级别
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA)
            .format(Long.valueOf(System.currentTimeMillis()));
        if (TextUtils.isEmpty(type))
        {
            type = E;
        }
        
        if (isLogLocation)
        {
            StackTraceElement caller = new Throwable().getStackTrace()[location];
            String clazzName = caller.getClassName();
            clazzName = clazzName.substring(clazzName.lastIndexOf(".") + 1);
            
            return String.format(Locale.CHINA,
                TAG_FILE_DEFAULT_LOCATION,
                time,
                type,
                clazzName,
                caller.getMethodName(),
                caller.getLineNumber());
        }
        else
        {
            return String.format(TAG_FILE_DEFAULT, time, type);
        }
    }
    
    /**
     * 写日志入文件
     * @param content 日志内容
     */
    private synchronized static void writeLogToFile(String content)
    {
        String path = FileUtil.getPath();
        if (null == path)
        {
            LogUtil.e(TAG_ERROR + "sdcard path is null");
            return;
        }
        
        File dirFile = FileUtil.createFileDir(path + LOG_FILE_PATH);
        if (null == dirFile)
        {
            LogUtil.e(TAG_ERROR + "sdcard dirFile create failed");
            return;
        }
        
        File file = FileUtil.createFile(dirFile, START_COUNT + LOG_FILE_TXT_NAME);
        if (null == file)
        {
            LogUtil.e(TAG_ERROR + "sdcard file create failed");
            return;
        }
        
        if (!FileUtil.writeToFile(file, content))
        {
            LogUtil.e(TAG_ERROR + "FileUtil write failed");
            return;
        }
        
        int size = FileUtil.getFileSize(file);
        if (-1 == size)
        {
            LogUtil.e(TAG_ERROR + "sdcard getFileSize failed");
            return;
        }
        
        // 分文件、限制文件个数
        if (size > MAX_SIZE_OF_TXT)
        {
            for (int count = MAX_COUNT; count >= START_COUNT; count--)
            {
                if (count == MAX_COUNT)
                {
                    if (FileUtil.isExist(dirFile, count + LOG_FILE_TXT_NAME)
                        && !FileUtil.deleteFile(dirFile, MAX_COUNT + LOG_FILE_TXT_NAME))
                    {
                        LogUtil.e(TAG_ERROR + "FileUtil deleteFile failed");
                        return;
                    }
                }
                else
                {
                    if (FileUtil.isExist(dirFile, count + LOG_FILE_TXT_NAME)
                        && !FileUtil.renameFile(dirFile, count + LOG_FILE_TXT_NAME, (count + 1) + LOG_FILE_TXT_NAME))
                    {
                        LogUtil.e(TAG_ERROR + "FileUtil renameFile failed");
                        return;
                    }
                }
            }
        }
    }
}
