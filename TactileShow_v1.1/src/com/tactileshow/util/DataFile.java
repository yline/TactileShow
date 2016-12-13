package com.tactileshow.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class DataFile
{
    private PrintWriter out_ble;
    
    private RandomAccessFile out_avg_ble;
    
    private double avg_ble = -1;
    
    private int records_ble;
    
    public static DataFile getInstance()
    {
        return DataFileHolder.sInstance;
    }
    
    private static class DataFileHolder
    {
        private static DataFile sInstance = new DataFile();
    }
    
    private DataFile()
    {
        //time 改成 date
        StaticValue.record_time = Calendar.getInstance();
        Log.e("toy", "Record Hour: " + StaticValue.record_time.get(Calendar.HOUR_OF_DAY));
        
        out_ble = getTimePrintWriter(StaticValue.record_time, StaticValue.BLE, "data", true);
        out_avg_ble = getRandomAccessFile(StaticValue.record_time, StaticValue.BLE, "avg");
        
        records_ble = computeAvgData(StaticValue.BLE);
    }
    
    private String getTimeDirectory(Calendar t, String sensor)
    {
        return "/storage/sdcard0/TactileShow/" + sensor + "/" + (t.get(Calendar.MONTH) + 1) + "/"
            + t.get(Calendar.DAY_OF_MONTH) + "/" + t.get(Calendar.HOUR_OF_DAY) + "/";
    }
    
    private RandomAccessFile getRandomAccessFile(Calendar t, String sensor, String fileName)
    {
        File f = new File(getTimeDirectory(t, sensor) + fileName);
        try
        {
            File parent = new File(f.getParent());
            parent.mkdirs();
            if (!f.isFile())
            {
                f.createNewFile();
            }
            return new RandomAccessFile(f, "rw");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    private PrintWriter getTimePrintWriter(Calendar t, String sensor, String fileName, boolean append)
    {
        File f = new File(getTimeDirectory(t, sensor) + fileName);
        try
        {
            File parent = new File(f.getParent());
            parent.mkdirs();
            if (!f.isFile())
                f.createNewFile();
            return new PrintWriter(new OutputStreamWriter(new FileOutputStream(f, append)));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public void writeAvgData(Calendar t, String sensor, String data)
    {
        PrintWriter print = getTimePrintWriter(t, sensor, "avg", false);
        print.println(data);
        print.flush();
        print.close();
    }
    
    public String readAvgData(Calendar t, String sensor)
    {
        BufferedReader read = getTimeBufferedReader(t, sensor, "avg");
        if (read == null)
            return null;
        String line = null;
        try
        {
            line = read.readLine();
            read.close();
            return line;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return line;
        }
    }
    
    /*
     * Return: the number of lines in data file.
     *         -1: error in compute average.
     */
    private int computeAvgData(String sensor)
    {
        BufferedReader in = getTimeBufferedReader(StaticValue.record_time, sensor, "data");
        if (in == null)
        {
            return -1;//12.5新加的代码
        }
        try
        {
            double all = 0;
            int lines = 0;
            String line = in.readLine();
            while (line != null)
            {
                String[] pars = line.split(" ");
                all += Double.parseDouble(pars[1]);
                lines++;
                line = in.readLine();
            }
            if (lines != 0)
                all /= lines;
            if (sensor.equals(StaticValue.BLE))
            {
                avg_ble = all;
                out_avg_ble.seek(0);
                out_avg_ble.writeUTF(Double.toString(all));
            }
            return lines;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return -1;
        }
        catch (NumberFormatException e2)
        {
            e2.printStackTrace();
            Log.e("toy", "Compute average data error, cannot translate to double.");
            return -1;
        }
    }
    
    private boolean compareTime(Calendar t)
    {
        if (t.get(Calendar.DAY_OF_MONTH) != StaticValue.record_time.get(Calendar.DAY_OF_MONTH)
            || t.get(Calendar.HOUR_OF_DAY) != StaticValue.record_time.get(Calendar.HOUR_OF_DAY)
            || t.get(Calendar.MONTH) != StaticValue.record_time.get(Calendar.MONTH))
            return false;
        return true;
    }
    
    private void reloadEnvironment(Calendar t)
    {
        out_ble.close();//12.5这段代码有问题，调试下
        
        out_ble = getTimePrintWriter(t, StaticValue.BLE, "data", true);
        
        out_avg_ble = getRandomAccessFile(t, StaticValue.BLE, "avg");
        
        StaticValue.record_time = t;
        
        avg_ble = -1;
        
        records_ble = computeAvgData(StaticValue.BLE);
    }
    
    public BufferedReader getTimeBufferedReader(Calendar t, String sensor, String fileName)
    {
        File f = new File(getTimeDirectory(t, sensor) + fileName);
        try
        {
            if (!f.isFile())
            {
                return null;
            }
            return new BufferedReader(new InputStreamReader(new FileInputStream(f)));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public void writeData(Date t, String sensor, String data)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(t);
        if (!compareTime(calendar))
        {
            reloadEnvironment(calendar);
        }
        try
        {
            if (sensor.equals(StaticValue.BLE))
            {
                //if(out_avg_ble == null) //12.5新加的代码
                //return;   //12.5新加的代码
                out_avg_ble.seek(0);
                out_avg_ble
                    .writeUTF(Double.toString(((avg_ble * records_ble++) + Double.parseDouble(data)) / records_ble));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ms");
                String s = sdf.format(t);
                out_ble.println(s + "%" + data);
                out_ble.flush();
            }
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            Log.e("toy", "Format data error, the data received may not be double.");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e("toy", "Write average data to file error.");
        }
        
    }
    
}
