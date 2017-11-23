package com.tactileshow.manager;

import android.content.Context;

import com.yline.utils.FileUtil;
import com.yline.utils.LogUtil;

import java.io.File;
import java.util.Calendar;

/**
 * 写入数据到文件中
 *
 * @author yline 2017/11/23 -- 9:36
 * @version 1.0.0
 */
public class CacheFileManager {
    private static final String TAG_ERROR = "CacheFile error -> ";
    public static final String TYPE_HUM = "Red LED";
    public static final String TYPE_TEMP = "IR LED";
    public static final String TYPE_HEADER = "Green LED";

    private static String logDirPath;

    public static void init(Context context) {
        File file = context.getExternalFilesDir("Bluetooth");
        if (null != file) {
            logDirPath = file.getAbsolutePath();
        }
    }

    public static void writeData(TactileModel model) {
        long time = model.getTime();
        if (TactileModel.Empty != model.getHum()){
            writeDataToFile(time, TYPE_HUM, time + ":  " + model.getHum());
        }

        if (TactileModel.Empty != model.getTemp()){
            writeDataToFile(time, TYPE_TEMP, time + ":  " + model.getTemp());
        }

        if (TactileModel.Empty != model.getHeader()){
            writeDataToFile(time, TYPE_HEADER, time + ":  " + model.getHeader());
        }
    }

    private synchronized static void writeDataToFile(long time, String fileName, String content) {
        String path = logDirPath;
        if (null == path) {
            LogUtil.e(TAG_ERROR + "sdcard path is null");
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        File dirFile = FileUtil.createDir(path + File.separator + year + File.separator + month + File.separator + day + File.separator + hour + File.separator);
        if (null == dirFile) {
            LogUtil.e(TAG_ERROR + "sdcard dirFile create failed path = " + path + logDirPath);
            return;
        }

        File file = FileUtil.create(dirFile, fileName);
        if (null == file) {
            LogUtil.e(TAG_ERROR + "sdcard file create failed");
            return;
        }

        if (!FileUtil.write(file, content)) {
            LogUtil.e(TAG_ERROR + "FileUtil write failed");
        }
    }
}
