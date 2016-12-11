package com.tactileshow.base;

import com.tactileshow.application.IApplication;

import android.app.Activity;
import android.os.Bundle;

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
        // PermissionUtil.request(this, SDKConstant.REQUEST_CODE_PERMISSION, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    
    /*
     * 兼容到6.0的权限请求 反馈(仅限写文件)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<String> result =
            PermissionUtil.requestHandle(SDKConstant.REQUEST_CODE_PERMISSION, requestCode, permissions, grantResults);
        LogFileUtil.v(SDKConstant.TAG_HANDLE_PERMISSION, result.toString());
    }
    */
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        IApplication.removeActivity(this);
    }
}
