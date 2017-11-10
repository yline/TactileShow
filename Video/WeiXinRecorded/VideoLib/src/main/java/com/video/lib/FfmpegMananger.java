package com.video.lib;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.yixia.videoeditor.adapter.UtilityAdapter;

import java.io.File;
import java.util.Locale;

/**
 * Ffmpeg 管理类
 * @author yline 2017/11/10 -- 20:44
 * @version 1.0.0
 */
public class FfmpegMananger {
    private static boolean mIsLog;
    private static String cachePath;
    private static final String VersionOfSDK = "1.2.0";

    public static void v(String method, String msg) {
        Log.i("xxx-", "v: method: " + method + ", msg: " + msg);
    }

    /**
     * 初始化拍摄SDK，必须
     * @param context 上下文
     */
    public static void init(Context context, String dirName) {
        long startTime = System.currentTimeMillis();

        // 初始化FFmpeg
        String packageName = context.getPackageName(); // 应用包名

        String versionMane = ""; // 应用版本名称
        int versionCode = -1; // 应用版本号
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            if (null != packageInfo) {
                versionMane = packageInfo.versionName;
                versionCode = packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String deviceVersion = Build.VERSION.RELEASE; // 获得设备的固件版本号
        String deviceModel = Build.MODEL; // 获得设备型号

        String formatStr = "versionName=%s&versionCode=%d&sdkVersion=%s&android=%s&device=%s";
        String settingStr = String.format(Locale.CHINA, formatStr, versionMane, versionCode, VersionOfSDK, deviceVersion, deviceModel);

        v("FfmpegManager-init", "settingStr = " + settingStr);

        UtilityAdapter.FFmpegInit(context, settingStr);

        // 初始化其它操作
        mIsLog = true;
        File dcimFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        cachePath = dcimFile.getPath() + File.separator + dirName + File.separator;  // 设置拍摄视频缓存路径

        v("FfmpegManager-init", "diffTime = " + (System.currentTimeMillis() - startTime));
    }

    public static String getCachePath() {
        return cachePath;
    }
}
