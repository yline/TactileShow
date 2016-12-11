package com.tactileshow.base;

import com.tactileshow.application.IApplication;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * simple introduction
 * @author YLine 2016-5-25 -> 上午7:32:58
 */
public class BaseFragmentActivity extends FragmentActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        IApplication.addAcitivity(this);
        super.onCreate(savedInstanceState);
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
    }*/
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        IApplication.removeActivity(this);
    }
}
