package com.tactileshow.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import com.tactileshow.application.IApplication;
import com.tactileshow.log.FileUtil;
import com.tactileshow.log.LogFileUtil;

public abstract class BaseFileHelper
{
    public static final String TAG = "FileHelper";
    
    private static final String TACTILE = "TactileShow";
    
    public static final String BLE = "BLE";
    
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
    
    /** 读取本地文件 */
    protected List<List<String>> readMapDataFile(File file)
    {
        List<List<String>> result = new ArrayList<List<String>>();
        
        if (null == file)
        {
            return result;
        }
        
        // 读取每一行的信息,并保存
        List<String> lineList = new ArrayList<String>();
        Scanner scanner = null;
        try
        {
            scanner = new Scanner(file);
            while (scanner.hasNextLine())
            {
                lineList.add(scanner.nextLine());
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
        
        // 解析每一行信息
        for (int i = 0; i < lineList.size(); i++)
        {
            if (null == lineList.get(i) || "".equals(lineList.get(i)))
            {
                break;
            }
            result.add(parseLine(lineList.get(i)));
        }
        
        return result;
    }
    
    /** 解析每一行信息 */
    private List<String> parseLine(String content)
    {
        String[] strs = content.split("\t");
        return Arrays.asList(strs);
    }
    
    /** 写入本地文件 avg 中 */
    protected void writeAvgFile(File file, String content)
    {
        if (null == file)
        {
            return;
        }
        
        RandomAccessFile randomAccessFile = null;
        try
        {
            File parentFile = new File(file.getParent());
            parentFile.mkdirs();
            if (!file.isFile())
            {
                file.createNewFile();
            }
            
            randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(0);
            randomAccessFile.writeUTF(content);
        }
        catch (FileNotFoundException e)
        {
            LogFileUtil.e(TAG, "RandomAccessFile FileNotFoundException", e);
        }
        catch (IOException e)
        {
            LogFileUtil.e(TAG, "file IOException", e);
        }
        finally
        {
            if (null != randomAccessFile)
            {
                try
                {
                    randomAccessFile.close();
                }
                catch (IOException e)
                {
                    LogFileUtil.e(TAG, "randomAccessFile close IOException", e);
                }
            }
        }
    }
    
    /** 写入本地文件 data 中 */
    protected void writeDataFile(File file, String content)
    {
        if (null == file)
        {
            return;
        }
        
        PrintWriter printWriter = null;
        try
        {
            File parentFile = new File(file.getParent());
            parentFile.mkdirs();
            if (!file.isFile())
            {
                file.createNewFile();
            }
            
            printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            printWriter.print(content);
            printWriter.flush();
        }
        catch (FileNotFoundException e)
        {
            LogFileUtil.e(TAG, "RandomAccessFile FileNotFoundException", e);
        }
        catch (IOException e)
        {
            LogFileUtil.e(TAG, "file IOException", e);
        }
        finally
        {
            if (null != printWriter)
            {
                printWriter.close();
            }
        }
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
