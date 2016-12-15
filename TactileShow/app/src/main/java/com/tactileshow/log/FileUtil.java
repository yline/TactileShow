package com.tactileshow.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;
import android.text.TextUtils;

/**
 * 目前提供给 LogFileUtil准备
 * simple introduction
 *
 * @author YLine 2016-5-25 -> 上午8:06:08
 * @version
 */
public class FileUtil
{
    /**
     * 获取内置sd卡最上层路径
     * @return /storage/emulated/0/ or null
     */
    public static String getPath()
    {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
        {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
        }
        else
        {
            return null;
        }
    }
    
    /**
     * 获取文件大小
     * @param file  文件(such as /yline/log.txt)
     * @return  size or -1(文件为空、获取错误、关闭流失败、文件不存在)
     */
    public static int getFileSize(File file)
    {
        int size = 0;
        if (null == file || !file.exists())
        {
            return -1;
        }
        
        FileInputStream fileInputStream = null;
        try
        {
            fileInputStream = new FileInputStream(file);
            size = fileInputStream.available();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return -1;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return -1;
        }
        finally
        {
            if (null != fileInputStream)
            {
                try
                {
                    fileInputStream.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    return -1;
                }
            }
        }
        
        return size;
    }
    
    /**
     * android.permission.WRITE_EXTERNAL_STORAGE
     * 创建一个文件夹
     * @param path  such as /storage/emulated/0/Yline/Log/
     * @return  file or null
     */
    public static File createFileDir(String path)
    {
        File pathFile = new File(path);
        
        if (!pathFile.exists())
        {
            if (!pathFile.mkdirs())
            {
                return null;
            }
        }
        return pathFile;
    }
    
    /**
     * android.permission.WRITE_EXTERNAL_STORAGE
     * 构建一个文件,真实的创建
     * @param dir   文件的目录
     * @param name  文件名     such as log.txt
     * @return  file or null
     */
    public static File createFile(File dir, String name)
    {
        if (null == dir || TextUtils.isEmpty(name))
        {
            return null;
        }
        
        File file = new File(dir, name);
        if (!file.exists())
        {
            try
            {
                if (file.createNewFile())
                {
                    return file;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        else
        {
            return file;
        }
        
        return null;
    }
    
    /**
     * 是否存在该文件
     * @param dir   文件目录
     * @param name  文件名称
     * @return false(参数错误、文件不存在)
     */
    public static boolean isExist(File dir, String name)
    {
        if (null == dir || TextUtils.isEmpty(name))
        {
            return false;
        }
        
        return new File(dir, name).exists();
    }
    
    /**
     * android.permission.WRITE_EXTERNAL_STORAGE
     * 删除一个文件
     * @param dir   文件的目录
     * @param name  文件名  such as log.txt
     * @return  false(参数错误、不存在该文件、删除失败)
     */
    public static boolean deleteFile(File dir, String name)
    {
        if (null == dir || TextUtils.isEmpty(name))
        {
            return false;
        }
        
        File file = new File(dir, name);
        if (file.exists())
        {
            return file.delete();
        }
        
        return false;
    }
    
    /**
     * 重命名一个文件
     * @param dir   文件的目录
     * @param oldName   文件名  such as log0.txt
     * @param newName   文件名  such as log1.txt
     * @return  false(参数错误、不存在该文件、重命名失败)
     */
    public static boolean renameFile(File dir, String oldName, String newName)
    {
        if (null == dir || TextUtils.isEmpty(oldName))
        {
            return false;
        }
        
        File oldFile = new File(dir, oldName);
        // 不存在该文件,即算作命名成功
        if (oldFile.exists())
        {
            if (TextUtils.isEmpty(newName))
            {
                return false;
            }
            File newFile = new File(dir, newName);
            return oldFile.renameTo(newFile);
        }
        
        return false;
    }
    
    /**
     * 之后统计乱码的情况(理论是不乱码的)
     * 写内容到文件中,尾随后面写
     * @param file  文件
     * @param content   内容
     * @return  false(写入失败,写入工具关闭失败)
     */
    public static boolean writeToFile(File file, String content)
    {
        FileWriter fileWriter = null;
        try
        {
            fileWriter = new FileWriter(file, true);
            fileWriter.append(content + "\n");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        finally
        {
            if (null != fileWriter)
            {
                try
                {
                    fileWriter.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        
        return true;
    }
}
