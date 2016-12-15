package com.tactileshow.base;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tactileshow.application.IApplication;
import com.tactileshow.log.LogFileUtil;
import com.tactileshow.util.PermissionUtil;

import java.util.List;

/**
 * @author yline 2016/9/4 --> 17:57
 * @version 1.0.0
 */
public class BaseAppCompatActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		IApplication.addAcitivity(this);
		PermissionUtil.request(this, IApplication.REQUEST_CODE_PERMISSION, Manifest.permission.WRITE_EXTERNAL_STORAGE);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		List<String> result = PermissionUtil.requestHandle(IApplication.REQUEST_CODE_PERMISSION, requestCode, permissions, grantResults);
		LogFileUtil.v("onRequestPermissionsResult " + result.toString());
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		IApplication.removeActivity(this);
	}
}
