package com.tactileshow.helper;

import android.content.Context;

import com.yline.log.LogFileUtil;
import com.yline.utils.FileUtil;
import com.yline.utils.LogUtil;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

/**
 * 将 数据写入文件；按照现有规则
 * 日期为文件，
 *
 * @author yline 2017/9/24 -- 16:51
 * @version 1.0.0
 */
public class DataFileUtil {
    private static String logDirPath;

    private static final String TAG_ERROR = "DataFileUtil error -> ";

    public static void init(Context context) {
        File dataDir = FileUtil.getFileExternalDir(context, "Bluetooth");
        if (null != dataDir) {
            logDirPath = dataDir.getAbsolutePath();
        }
    }

    public static void append(BroadcastModel model) {
        if (null != model && !model.isDataEmpty()) {
            String content = BroadcastModel.toJson(model);
            writeDataToFile(content);
        } else {
            LogFileUtil.e("DataFileUtil", "model is null or dataEmpty");
        }
    }

    /**
     * 规则如下：
     * 指定文件夹 / 年+月+日 / 时.txt
     *
     * @param content 单条数据
     */
    private synchronized static void writeDataToFile(String content) {
        String path = logDirPath;
        if (null == path) {
            LogUtil.e(TAG_ERROR + "sdcard path is null");
            return;
        }

        long stampTime = System.currentTimeMillis();
        String dirName = getTimeDirName(stampTime);
        File dirFile = FileUtil.createDir(path + File.separator + dirName + File.separator);
        if (null == dirFile) {
            LogUtil.e(TAG_ERROR + "sdcard dirFile create failed path = " + path + File.separator + dirName);
            return;
        }

        String fileName = getTimeFileName(stampTime);
        File file = FileUtil.create(dirFile, fileName);
        if (null == file) {
            LogUtil.e(TAG_ERROR + "sdcard file create failed");
            return;
        }

        if (!FileUtil.write(file, content)) {
            LogUtil.e(TAG_ERROR + "FileUtil write failed");
        }
    }

    /**
     * @param stampTime unit ms
     * @return 年_月_日
     */
    private static String getTimeDirName(long stampTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(stampTime);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format(Locale.CHINA, "%d_%d_%d", year, month, day);
    }

    /**
     * @param stampTime unit ms
     * @return 时_bt_txt
     */
    private static String getTimeFileName(long stampTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(stampTime);
        return calendar.get(Calendar.HOUR_OF_DAY) + "_bt.txt";
    }
}
