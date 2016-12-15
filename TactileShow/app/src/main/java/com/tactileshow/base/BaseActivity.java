package com.tactileshow.base;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;

import com.tactileshow.application.IApplication;
import com.tactileshow.log.LogFileUtil;
import com.tactileshow.util.PermissionUtil;

import java.util.List;

/**
 * simple introduction
 * @author YLine 2016-5-25 -> 上午7:32:33
 */
public class BaseActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		IApplication.addAcitivity(this);
		// 兼容到6.0的权限请求
		PermissionUtil.request(this, IApplication.REQUEST_CODE_PERMISSION, Manifest.permission.WRITE_EXTERNAL_STORAGE);
	}

	// 兼容到6.0的权限请求 反馈(仅限写文件)
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		List<String> result =
				PermissionUtil.requestHandle(IApplication.REQUEST_CODE_PERMISSION, requestCode, permissions, grantResults);
		LogFileUtil.v("onRequestPermissionsResult " + result.toString());
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		IApplication.removeActivity(this);
	}
}
