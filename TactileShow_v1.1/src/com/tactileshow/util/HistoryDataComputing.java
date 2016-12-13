package com.tactileshow.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.tactileshow.viewhelper.LineChartBuilder;

import android.util.Log;

class Record
{
    public Date t;
    
    public double data;
    
    public Record(String line)
    {
        String[] pars = line.split("%");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ms");
        try
        {
            t = sdf.parse(pars[0]);
        }
        catch (ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        data = Double.parseDouble(pars[1]);
    }
    
    public Record()
    {
        t = new Date();
    }
}

public class HistoryDataComputing
{
    private LineChartBuilder view;
    
    public HistoryDataComputing(LineChartBuilder view)
    {
        this.view = view;
    }
    
    /**
     * 根据数据量不同采取不同大小的分组，保证总共的数据点不大于max_points；
     * @param ls
     */
    private void addData(List<Record> ls)
    {
        int size = ls.size();
        if (size > StaticValue.max_points)
        {
            long team = size / StaticValue.max_points;
            Log.e("wshg", "size = " + size + "; team = " + team);
            double all_data = 0;
            for (int i = 0; i < size;)
            {
                all_data = 0;
                int j = i;
                for (; j - i < team && j < size; j++)
                {
                    all_data += ls.get(j).data;
                }
                view.addHistoryData(i, all_data / (j - i));
                i = j;
            }
        }
        else
        {
            for (int i = 0; i < size; i++)
            {
                view.addHistoryData(i, ls.get(i).data);
            }
        }
    }
    
    public boolean getHoursHistory(Date from, Date to, String sensor)
    {
        if (from.getTime() > StaticValue.record_time.getTime().getTime() || from.getTime() > to.getTime())
            return false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(from);
        
        List<Record> ls = new ArrayList<Record>();
        try
        {
            for (; calendar.getTime().getTime() <= to.getTime(); calendar.add(Calendar.HOUR_OF_DAY, 1))
            {
                BufferedReader in = DataFile.getInstance().getTimeBufferedReader(calendar, sensor, "data");
                if (in != null)
                {
                    String line = in.readLine();
                    while (line != null)
                    {
                        Record one = new Record(line);
                        if (one.t.getTime() > to.getTime())
                            break;
                        else
                        {
                            ls.add(one);
                            line = in.readLine();
                        }
                    }
                    in.close();
                }
            }
            addData(ls);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return true;
    }
    
    public boolean getDaysHistory(Date from, Date to, String sensor)
    {
        if (from.getTime() > StaticValue.record_time.getTime().getTime() || from.getTime() > to.getTime())
            return false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(from);
        
        List<Record> ls = new ArrayList<Record>();
        try
        {
            for (; calendar.getTime().getTime() <= to.getTime(); calendar.add(Calendar.HOUR_OF_DAY, 1))
            {
                BufferedReader in = DataFile.getInstance().getTimeBufferedReader(calendar, sensor, "avg");
                if (in != null)
                {
                    String line = in.readLine();
                    if (line != null)
                    {
                        Record one = new Record();
                        one.t = calendar.getTime();
                        one.data = Double.parseDouble(line);
                        ls.add(one);
                        Log.e("wshg", "add: " + calendar.getTime());
                    }
                    in.close();
                }
                
            }
            addData(ls);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return true;
    }
}
