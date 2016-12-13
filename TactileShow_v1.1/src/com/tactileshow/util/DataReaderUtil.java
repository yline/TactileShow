package com.tactileshow.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.tactileshow.application.IApplication;
import com.tactileshow.log.FileUtil;
import com.tactileshow.log.LogFileUtil;

/**
 * 路径："/storage/sdcard0/TactileShow/BLE/mapdata1.txt"
 * @author YLine
 *
 * 2016年12月13日 下午4:52:19
 */
public class DataReaderUtil
{
    public static List<String> getDataList()
    {
        List<String> dataList = new ArrayList<String>();
        
        String path = FileUtil.getPath();
        if (null == path)
        {
            IApplication.toast("储存卡不可用");
            return dataList;
        }
        
        String fileName = path + "TactileShow" + File.separator + "BLE" + File.separator + "mapdata1.txt";
        LogFileUtil.v(IApplication.TAG, "fileName = " + fileName);
        
        Scanner scanner = null;
        try
        {
            scanner = new Scanner(new File(fileName));
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
}
