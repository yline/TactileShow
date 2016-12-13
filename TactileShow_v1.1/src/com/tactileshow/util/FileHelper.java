package com.tactileshow.util;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import com.tactileshow.log.LogFileUtil;

/**
 * 1,读取本地文件内容
 * @author YLine
 *
 * 2016年12月14日 上午1:32:51
 */
public class FileHelper extends BaseFileHelper
{
    private static Calendar recordCalendar;
    
    public static FileHelper getInstance()
    {
        return FileHelperHolder.sInstance;
    }
    
    private static class FileHelperHolder
    {
        private static FileHelper sInstance = new FileHelper();
    }
    
    private FileHelper()
    {
        super();
        
        recordCalendar = Calendar.getInstance();
        LogFileUtil.v("parentFileName = " + getParentFileName() + ",mapData = " + getMapDataFileName());
    }
    
    public List<String> readMapData()
    {
        String fileName = getMapDataFileName();
        LogFileUtil.v("MapDataFileName = " + fileName);
        
        File file = new File(fileName);
        List<String> dataList = readFile(file);
        
        return dataList;
    }
    
    public void writeData()
    {
        if (!compareTime(recordCalendar))
        {
            recordCalendar = Calendar.getInstance();
        }
        else
        {
            LogFileUtil.v("writeData time is same");
        }
    }
    
    /**
     * 相比较两者时间是否相同，精确到 小时 为单位
     * @param oldCalendar
     * @return
     */
    private boolean compareTime(Calendar oldCalendar)
    {
        Calendar newCalendar = Calendar.getInstance();
        if (oldCalendar.get(Calendar.YEAR) == newCalendar.get(Calendar.YEAR))
        {
            if (oldCalendar.get(Calendar.MONTH) == newCalendar.get(Calendar.MONTH))
            {
                if (oldCalendar.get(Calendar.DAY_OF_MONTH) == newCalendar.get(Calendar.DAY_OF_MONTH))
                {
                    if (oldCalendar.get(Calendar.HOUR_OF_DAY) == newCalendar.get(Calendar.HOUR_OF_DAY))
                    {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
}
