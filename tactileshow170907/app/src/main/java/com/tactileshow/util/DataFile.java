package com.tactileshow.util;

import android.text.TextUtils;
import android.text.format.Time;

import com.yline.log.LogFileUtil;
import com.yline.utils.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

public class DataFile
{
	private static final String TAG = "DataFile";
	
	// 这两个可能为空
	private PrintWriter out_press, out_temp;
	
	// 这两个可能为空
	private RandomAccessFile out_avg_press, out_avg_temp;
	
	private double avg_press = -1, avg_temp = -1;
	
	private int records_press, records_temp;
	
	public DataFile()
	{
		Time nowTime = new Time();
		nowTime.setToNow();
		
		initWriterEnvironment(nowTime);
	}
	
	public BufferedReader getTimeBufferedReader(Time t, String sensor, String fileName)
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
	
	/*
	 * Return: the number of lines in data file.
	 *         -1: error in compute average.
	 */
	public synchronized int computeAvgData(String sensor)
	{
		BufferedReader in = getTimeBufferedReader(StaticValue.record_time, sensor, "data");
		if (in == null)
		{
			return -1;
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
			{
				all /= lines;
			}
			
			if (sensor.equals(StaticValue.TEMP))
			{
				avg_temp = all;
				out_avg_temp.seek(0);
				out_avg_temp.writeUTF(Double.toString(all));
			}
			else if (sensor.equals(StaticValue.PRESS))
			{
				avg_press = all;
				out_avg_press.seek(0);
				out_avg_press.writeUTF(Double.toString(all));
			}
			return lines;
		}
		catch (IOException e)
		{
			LogFileUtil.e(TAG, "computeAvgData IOException", e);
			return -1;
		}
		catch (NumberFormatException e2)
		{
			LogFileUtil.e(TAG, "computeAvgData NumberFormatException", e2);
			return -1;
		}
	}
	
	public synchronized void writeData(Time t, String sensor, String data)
	{
		if (!compareTime(t))
		{
			reloadEnvironment(t);
		}
		
		try
		{
			if (sensor.equals(StaticValue.TEMP))
			{
				if (null != out_avg_temp)
				{
					out_avg_temp.seek(0);
					out_avg_temp.writeUTF(Double.toString(((avg_temp * records_temp++) + Double.parseDouble(data)) / records_temp));
				}
				
				if (null != out_temp)
				{
					out_temp.println(t.format2445() + " " + data);
					out_temp.flush();
				}
			}
			else if (sensor.equals(StaticValue.PRESS))
			{
				if (null != out_avg_press)
				{
					out_avg_press.seek(0);
					out_avg_press.writeUTF(Double.toString(((avg_press * records_press++) + Double.parseDouble(data)) / records_press));
				}
				
				if (null != out_press)
				{
					out_press.println(t.format2445() + " " + data);
					out_press.flush();
				}
			}
		}
		catch (NumberFormatException e)
		{
			LogFileUtil.e(TAG, "writeData NumberFormatException", e);
		}
		catch (IOException e)
		{
			LogFileUtil.e(TAG, "writeData IOException", e);
		}
	}
	
	private void reloadEnvironment(Time nowTime)
	{
		if (null != out_press)
		{
			out_press.close();
		}
		
		if (null != out_temp)
		{
			out_temp.close();
		}
		
		initWriterEnvironment(nowTime);
	}
	
	private void initWriterEnvironment(Time nowTime)
	{
		StaticValue.record_time = nowTime;
		
		out_press = getTimePrintWriter(nowTime, StaticValue.PRESS, "data", true);
		out_temp = getTimePrintWriter(nowTime, StaticValue.TEMP, "data", true);
		out_avg_press = getRandomAccessFile(nowTime, StaticValue.PRESS, "avg");
		out_avg_temp = getRandomAccessFile(nowTime, StaticValue.TEMP, "avg");
		
		avg_press = -1;
		avg_temp = -1;
		records_press = computeAvgData(StaticValue.PRESS);
		records_temp = computeAvgData(StaticValue.TEMP);
	}
	
	/**
	 * 线程不安全，写入文件工具
	 *
	 * @param nowTime  当前时间
	 * @param sensor   目录结构中一部分
	 * @param fileName 文件名
	 * @return
	 */
	private RandomAccessFile getRandomAccessFile(Time nowTime, String sensor, String fileName)
	{
		String dirPath = getTimeDirectory(nowTime, sensor);
		if (!TextUtils.isEmpty(dirPath))
		{
			File file = new File(getTimeDirectory(nowTime, sensor) + fileName);
			try
			{
				File parentFile = new File(file.getParent());
				parentFile.mkdirs();
				if (!file.isFile())
				{
					file.createNewFile();
				}
				return new RandomAccessFile(file, "rw");
			}
			catch (IOException e)
			{
				LogFileUtil.e(TAG, "getRandomAccessFile IOException", e);
				return null;
			}
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * 写入文件工具
	 * 线程不安全
	 *
	 * @param nowTime  当前时间
	 * @param sensor   目录结构中字符
	 * @param fileName 文件名
	 * @param append   是否在后面追加
	 * @return
	 */
	private PrintWriter getTimePrintWriter(Time nowTime, String sensor, String fileName, boolean append)
	{
		String dirPath = getTimeDirectory(nowTime, sensor);
		if (!TextUtils.isEmpty(dirPath))
		{
			File file = new File(getTimeDirectory(nowTime, sensor) + fileName);
			try
			{
				File parentFile = new File(file.getParent());
				parentFile.mkdirs();
				if (!file.isFile())
				{
					file.createNewFile();
				}
				return new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, append)));
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	
	/**
	 * 比较时间是否相同
	 *
	 * @param nowTime 当前时间
	 * @return
	 */
	private boolean compareTime(Time nowTime)
	{
		if (null == StaticValue.record_time)
		{
			StaticValue.record_time = new Time();
			StaticValue.record_time.setToNow();
		}
		Time oldTime = StaticValue.record_time;
		
		if (nowTime.hour == oldTime.hour && nowTime.monthDay == oldTime.monthDay && nowTime.month == oldTime.month)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 获取文件目录
	 *
	 * @param time   当前时间
	 * @param sensor 目录结构中名称
	 * @return 目录路径
	 */
	private String getTimeDirectory(Time time, String sensor)
	{
		String topPath = FileUtil.getPathTop();
		if (!TextUtils.isEmpty(topPath))
		{
			return String.format("%sTactileShow/%s/%d/%d/%d/", topPath, sensor, (time.month + 1), time.monthDay, time.hour);
		}
		return null;
	}
}