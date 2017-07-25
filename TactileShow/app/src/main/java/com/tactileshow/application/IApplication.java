package com.tactileshow.application;

import com.yline.application.BaseApplication;

public class IApplication extends BaseApplication
{
	@Override
	public void onCreate()
	{
		super.onCreate();
	}

	/*
	*//**
	 * 要求在BaseApplication的super.onCreate()方法执行完成后,调用
	 * 获取本工程文件目录; such as "/sdcard/_yline/LibSdk/"
	 * @return null if failed
	 *//*
	public static String getProjectFilePath()
	{
		String path = FileUtil.getPath();
		if (TextUtils.isEmpty(path))
		{
			LogFileUtil.e(TAG, "SDCard not support, getProjectFilePath failed");
			return null;
		}

		path += (mBaseConfig.getFileParentPath() + mBaseConfig.getLogFilePath());
		return path;
	}*/
}
