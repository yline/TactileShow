package com.tactileshow.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import com.tactileshow.application.IApplication;
import com.tactileshow.log.FileUtil;

public abstract class BaseFileHelper
{
    private static final String TACTILE = "TactileShow";
    
    private static final String BLE = "BLE";
    
    private static final String MAP_DATA_NAME = "mapdata1.txt";
    
    /** /storage/sdcard0/TactileShow/BLE/ */
    private String parentFileName;
    
    /** /storage/sdcard0/TactileShow/BLE/mapdata1.txt */
    private String mapDataFileName;
    
    protected BaseFileHelper()
    {
        String path = FileUtil.getPath();
        if (null == path)
        {
            IApplication.toast("储存卡不可用");
            return;
        }
        
        parentFileName = path + TACTILE + File.separator + BLE + File.separator;
        mapDataFileName = parentFileName + MAP_DATA_NAME;
    }
    
    public List<String> readFile(File file)
    {
        List<String> dataList = new ArrayList<String>();
        
        Scanner scanner = null;
        try
        {
            scanner = new Scanner(file);
            while (scanner.hasNextLine())
            {
                dataList.add(scanner.nextLine());
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (null != scanner)
            {
                scanner.close();
            }
        }
        return dataList;
    }
    
    public String getParentFileName()
    {
        return parentFileName;
    }
    
    public String getMapDataFileName()
    {
        return mapDataFileName;
    }
    
    /**
     * /storage/sdcard0/TactileShow/BLE/12/14/9/
     * @return
     */
    public String getTimeDirectory()
    {
        Calendar calendar = Calendar.getInstance();
        String timeName =
            parentFileName + (calendar.get(Calendar.MONTH) + 1) + File.separator + calendar.get(Calendar.DAY_OF_MONTH)
                + File.separator + calendar.get(Calendar.HOUR_OF_DAY) + File.separator;
        return timeName;
    }
}
