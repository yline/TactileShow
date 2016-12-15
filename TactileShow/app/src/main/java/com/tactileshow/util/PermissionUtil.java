package com.tactileshow.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yline 2016/11/19 --> 15:07
 * @version 1.0.0
 */
public class PermissionUtil
{
	/**
	 * 检查某一项权限,是否需要动态申请
	 * @param context
	 * @param permission
	 * @return
	 */
	public static boolean check(Context context, String permission)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
			{
				return true;
			}
		}
		else
		{
			return true;
		}
		return false;
	}

	/**
	 * 动态请求权限
	 * @param activity
	 * @param permissions
	 */
	public static void request(Activity activity, int requestCode, String... permissions)
	{
		List<String> list = new ArrayList<String>();
		for (String permission : permissions)
		{
			if (isRequest(activity, permission))
			{
				list.add(permission);
			}
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && list.size() > 0)
		{
			activity.requestPermissions(list.toArray(new String[list.size()]), requestCode);
		}
	}

	/**
	 * 是否发送用户请求
	 * @return
	 */
	private static boolean isRequest(Context context, String permission)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
			{
				return true;
			}
			// 这里还可以添加,如果上次用户已经拒绝,就不再申请
		}
		else
		{
			return true;
		}
		return false;
	}

	/**
	 * 处理响应请求,返回被拒绝的权限
	 * @param requestCode
	 * @param requestBackCode
	 * @param permissions
	 * @param grantResults
	 * @return 被拒绝的权限
	 */
	public static List<String> requestHandle(int requestCode, int requestBackCode, String[] permissions, int[] grantResults)
	{
		List<String> result = new ArrayList<String>();
		if (requestCode == requestBackCode)
		{
			if (null != grantResults)
			{
				// 获取被拒绝的权限
				for (int i = 0; i < grantResults.length; i++)
				{
					if (grantResults[i] == PackageManager.PERMISSION_DENIED)
					{
						result.add(permissions[i]);
					}
				}
			}
		}
		return result;
	}
}
