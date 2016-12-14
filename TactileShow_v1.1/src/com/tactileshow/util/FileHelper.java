package com.tactileshow.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.tactileshow.bean.BleReceiverBean;
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
    
    public List<List<String>> readMapData()
    {
        String fileName = getMapDataFileName();
        LogFileUtil.v("MapDataFileName = " + fileName);
        
        File file = new File(fileName);
        List<List<String>> dataList = readMapDataFile(file);
        LogFileUtil.v("dataList size = " + dataList.size());
        
        return dataList;
    }
    
    /**
     * 该方法未经过测试,但基本内容Ok,日志OK
     * @param bean
     */
    public void writeData(BleReceiverBean bean)
    {
        if (null == bean)
        {
            LogFileUtil.e(TAG, "BleReceiverBean is null");
            return;
        }
        
        if (!BLE.equals(bean.getSensor()))
        {
            LogFileUtil.e(TAG, "BleReceiverBean sensor is not right");
            return;
        }
        
        if (!compareTime(recordCalendar))
        {
            recordCalendar = Calendar.getInstance();
            
            String timeDirectory = getTimeDirectory();
            LogFileUtil.v("timeDirectory = " + timeDirectory);
            
            // 写入 Avg
            String AvgDirStr = timeDirectory + "avg";
            if (null != AvgDirStr)
            {
                File file = new File(AvgDirStr);
                writeAvgFile(file, "content");
            }
            
            // 写入 Data
            String dataDirStr = timeDirectory + "data";
            if (null != dataDirStr)
            {
                File file = new File(dataDirStr);
                
                String time =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ms", Locale.getDefault()).format(bean.getTime());
                String dataDirResult = time + "%" + bean.getData();
                LogFileUtil.v("dataDirResult = " + dataDirResult);
                
                writeDataFile(file, dataDirResult);
            }
        }
        else
        {
            LogFileUtil.v("writeAvgData time is same");
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
