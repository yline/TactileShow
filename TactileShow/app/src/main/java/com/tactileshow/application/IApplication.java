package com.tactileshow.application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tactileshow.R;
import com.tactileshow.log.CrashHandler;
import com.tactileshow.log.FileUtil;
import com.tactileshow.log.LogFileUtil;

import java.util.ArrayList;
import java.util.List;

public class IApplication extends Application
{
	/** 应用心跳 */
	public static final int HANDLER_PALPITATION = -2;

	/** 应用心跳频率 */
	public static final int PALLITATION_TIME = 30 * 1000;

	/** handler 吐丝 */
	public static final int HANDLER_TOAST = -1;

	/** TAG */
	public static final String TAG = "Tactileshow";

	/** 先选用默认配置 */
	private static SDKConfig mBaseConfig = new SDKConfig();

	/** Activity管理 */
	private static List<Activity> mActivityList = new ArrayList<Activity>();

	/** Fragment记录 */
	private static List<String> mFragmentList = new ArrayList<String>();

	/** View记录 */
	private static List<String> mViewList = new ArrayList<String>();

	/** Service记录 */
	private static List<String> mServiceList = new ArrayList<String>();

	private static Application mApplication;

	// handler相关
	private static Handler handler;

	/** Toast 工具 */
	private Toast mToast;

	private TextView mTvToast;

	/**
	 * @return 当前application, 因为onCreate为应用入口, 因此不用担心为null
	 */
	public static Application getApplication()
	{
		return mApplication;
	}

	private void setApplication(Application application)
	{
		IApplication.mApplication = application;
	}

	public static void addAcitivity(Activity activity)
	{
		LogFileUtil.m("addAcitivity:" + activity.getClass().getSimpleName());
		mActivityList.add(activity);
	}

	public static void removeActivity(Activity activity)
	{
		LogFileUtil.m("removeActivity:" + activity.getClass().getSimpleName());
		mActivityList.remove(activity);
	}

	public static void finishActivity()
	{
		for (Activity activity : mActivityList)
		{
			LogFileUtil.m("finishActivity:" + activity.getClass().getSimpleName());
			activity.finish();
		}
	}

	public static void addFragmentForRecord(Fragment fragment)
	{
		LogFileUtil.m("addFragmentForRecord:" + fragment.getClass().getSimpleName());
		mFragmentList.add(fragment.getClass().getSimpleName());
	}

	public static void removeFragmentForRecord(Fragment fragment)
	{
		LogFileUtil.m("removeFragmentForRecord:" + fragment.getClass().getSimpleName());
		mFragmentList.remove(fragment.getClass().getSimpleName());
	}

	public static void addViewForRecord(View view)
	{
		LogFileUtil.m("addViewForRecord:" + view.getClass().getSimpleName());
		mViewList.add(view.getClass().getSimpleName());
	}

	public static void removeViewForRecord(View view)
	{
		LogFileUtil.m("removeViewForRecord:" + view.getClass().getSimpleName());
		mViewList.remove(view.getClass().getSimpleName());
	}

	public static void addServiceForRecord(Service service)
	{
		LogFileUtil.m("addServiceForRecord:" + service.getClass().getSimpleName());
		mServiceList.add(service.getClass().getSimpleName());
	}

	public static void removeServiceForRecord(Service service)
	{
		LogFileUtil.m("removeServiceForRecord:" + service.getClass().getSimpleName());
		mServiceList.remove(service.getClass().getSimpleName());
	}

	@Override
	public Context getApplicationContext()
	{
		return super.getApplicationContext();
	}

	/**
	 * @return 当前Application的配置信息
	 */
	public static SDKConfig getBaseConfig()
	{
		return mBaseConfig;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * 配置当前Application的配置信息
	 * 返回null,则按默认配置
	 * @param mBaseConfig 配置对象
	 */
	private void setBaseConfig(SDKConfig mBaseConfig)
	{
		if (null != mBaseConfig)
		{
			IApplication.mBaseConfig = mBaseConfig;
		}
	}

	/**
	 * 进行一些基础配置,要求上级必须配置的信息
	 * @return
	 */
	protected SDKConfig initConfig()
	{
		return new SDKConfig();
	}

	/**
	 * 更改资源的时候,才需要做一步操作,引用不需要
	 * 原子操作
	 * @return
	 */
	public static Handler getHandler()
	{
		return handler;
	}

	@SuppressLint("HandlerLeak")
	@Override
	public void onCreate()
	{
		super.onCreate();
		setApplication(this); // 初始化全局变量

		// 配置,定制基础信息
		setBaseConfig(initConfig());

		// 设立一个程序入口的log
		LogFileUtil.v(mBaseConfig.toString());
		LogFileUtil.m("****** application start id = " + Thread.currentThread().getId());

		// 异常崩溃日志
		CrashHandler.getInstance().init(this);

		handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				switch (msg.what)
				{
					case HANDLER_PALPITATION:
						LogFileUtil.v(TAG,
								"this time = " + System.currentTimeMillis() + ",this thread = "
										+ Thread.currentThread().getId());
						handler.sendEmptyMessageDelayed(HANDLER_PALPITATION, PALLITATION_TIME);
						break;
					case HANDLER_TOAST:
						showToast(IApplication.this, (String) msg.obj);
						break;
					default:
						break;
				}
			}
		};
		handler.sendEmptyMessageDelayed(HANDLER_PALPITATION, PALLITATION_TIME);
	}

	/**
	 * 打印toast
	 * @param context
	 * @param msg     内容
	 */
	private void showToast(Context context, String msg)
	{
		if (null == mToast)
		{
			mToast = new Toast(context);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}

		if (null == mTvToast)
		{
			mTvToast = new TextView(context);
			mTvToast.setBackgroundResource(R.drawable.bg_toast);
			mTvToast.setTextColor(0xffffffff);
		}

		mTvToast.setText(msg);
		mToast.setView(mTvToast);
		mToast.show();
	}

	/**
	 * 吐司
	 * @param content
	 */
	public static void toast(String content)
	{
		handler.obtainMessage(HANDLER_TOAST, content).sendToTarget();
	}

	/**
	 * 要求在BaseApplication的super.onCreate()方法执行完成后,调用
	 * 获取本工程文件目录; such as "/sdcard/_yline/LibSdk/"
	 * @return null if failed
	 */
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
	}
}
