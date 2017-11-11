package com.record.lib.temp.ffmpeg;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.yixia.videoeditor.adapter.UtilityAdapter;

import java.io.File;
import java.util.Locale;

/**
 * Ffmpeg 管理类
 *
 * @author yline 2017/11/9 -- 15:33
 * @version 1.0.0
 */
public class FfmpegManager {
    private static boolean mIsLog;
    private static String cachePath;
    private static final String VersionOfSDK = "1.2.0";

    public static void v(String method, String msg) {
        Log.i("xxx-", "v: method: " + method + ", msg: " + msg);
    }

    public static void init(Context context) {
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
        cachePath = dcimFile.getPath() + "/WeChatJuns/";  // 设置拍摄视频缓存路径

        v("FfmpegManager-init", "diffTime = " + (System.currentTimeMillis() - startTime));
    }

    /**
     * 获取log输出的文件路径
     *
     * @return
     */
    public static String getLogPathCommand() {
        if (mIsLog) {
            return " -d stdout -loglevel verbose";
        } else {
            String filePath = cachePath + "temp_ffmpeg.log";
            return " -d " + filePath + " -loglevel verbose";
        }
    }

    public static void setCachePath(String cachePath) {
        FfmpegManager.cachePath = cachePath;
    }

    public static String getCachePath() {
        return cachePath;
    }

    // 后置摄像头 flip
    private static final int FLIP_TYPE_BACK = 0x0;

    // 前置摄像头 flip
    private static final int FLIP_TYPE_FRONT = 0x1;

    /**
     * 设置视频输入参数
     *
     * @param width       宽度
     * @param height      高度
     * @param orientation 方向，后置{0}；前置{1}
     */
    public static void setInputSetting(int width, int height, int orientation) {
        if (orientation == android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK) {
            UtilityAdapter.RenderInputSettings(width, height, 0, FLIP_TYPE_BACK);
        } else {
            UtilityAdapter.RenderInputSettings(width, height, 180, FLIP_TYPE_FRONT);
        }
    }

    // 视频格式
    public static final int OUTPUT_FORMAT_YUV = 0x1;
    public static final int OUTPUT_FORMAT_RGBA = 0x2;
    public static final int OUTPUT_FORMAT_MASK_ZIP = 0x4;
    public static final int OUTPUT_FORMAT_MASK_NEED_LASTSNAP = 0x8;
    public static final int OUTPUT_FORMAT_MASK_HARDWARE_ACC = 0x10;
    public static final int OUTPUT_FORMAT_MASK_MP4 = 0x20;

    /**
     * 设置视频输出参数
     *
     * @param width  宽度
     * @param height 高度
     * @param fps    帧率
     * @param format 视频输出格式; 已定义成了常量
     */
    public static void setOutputSetting(int width, int height, int fps, int format) {
        UtilityAdapter.RenderOutputSettings(width, height, fps, format);
    }

    // 解析 操作，的状态
    public static final int PARSER_ACTION_INIT = 0; //<设置全局的属性，在一开始进入预览界面时调用
    public static final int PARSER_ACTION_UPDATE = 1; //<设置摄像头相关属性，在摄像头打开时调用
    public static final int PARSER_ACTION_START = 2; //<设置开始捕捉，并指定保存的文件
    public static final int PARSER_ACTION_STOP = 3; //<设置停止捕捉
    public static final int PARSER_ACTION_FREE = 4; //<释放占用，这时没完成的进度也会被取消
    public static final int PARSER_ACTION_PROGRESS = 5; //<查询处理的进度

    /**
     * 设置 操作的 状态
     *
     * @param setting    设置参数，无{""}，保存文件{"filename = %s"}
     * @param actionType 解析操作时，状态
     * @return 依据actionType不同，返回结果不同；例如PARSER_ACTION_PROGRESS，返回当前进度
     */
    public static int setParserActionState(String setting, int actionType) {
        setting = TextUtils.isEmpty(setting) ? "" : setting;
        return UtilityAdapter.FilterParserAction(setting, actionType);
    }

    // 修改后缀名；{log命令、原始路径文件名、修改后路径文件名}
    public static final String COMMAND_MODIFY_SUFFIX = "ffmpeg %s -i %s -r 25 -vcodec copy -acodec copy -vbsf h264_mp4toannexb %s";
    // 合并视频流；{log命令、流关联、处理翻转信息、视频编码参数、临时输出文件}
    public static final String COMMAND_MERGE_VIDEO = "ffmpeg %s -i %s -vf %s %s -acodec copy -absf aac_adtstoasc -f mp4 -movflags faststart %s";
    // 合并视频流；{Log命令、流关联、临时输出文件}
    public static final String COMMAND_MERGE_VIDEO_SIMPLE = "ffmpeg %s -i %s -vcodec copy -acodec copy -absf aac_adtstoasc -f mp4 -movflags faststart %s";
    // 压缩视频流；{输出的临时路径、输出的视频路径}
    public static final String COMMAND_COMPRESS_VIDEO = "ffmpeg -y -i %s -strict -2 -vcodec libx264 -preset ultrafast -crf 25 -acodec aac -ar 44100 -ac 2 -b:a 96k -s 360x640 -aspect 9:16 %s";

    // 命令行执行成功
    public static final int COMMAND_RESULT_SUCCESS = 0;
    // FFMPEG视频编码参数
    public static final String COMMAND_PARAM_VIDEO_CODE = " -pix_fmt yuv420p -vcodec libx264 -profile:v baseline -preset ultrafast";

    /**
     * 命令行格式：{"ffmpeg -i \"生成的mp4\" -y -f image2 -ss 1 -t 0.001 -s 480x480 \"输出.jpg\" "}
     *
     * @param tag     任务的唯一标识，如果标识为""，以阻塞方式运行，否则以异步方式运行
     * @param command 命令行
     * @return 返回执行结果，成功{0}
     */
    public static int executeCommand(String tag, String command) {
        v("executeCommand", "tag = " + tag + ",command = " + command);
        return UtilityAdapter.FFmpegRun(tag, command);
    }

    /**
     * 底层实时处理视频，将视频旋转好，并剪切成480x480(原先设置的输出参数)
     *
     * @param data 数据流
     */
    public static void executeRenderVideoData(byte[] data) {
        if (null != data) {
            UtilityAdapter.RenderDataYuv(data);
        }
    }

    /**
     * 提供录音数据，必须是44100Hz，1channel，16bit unsigned
     * @param data 数据流
     */
    public static void executeRenderAudioData(byte[] data) {
        if (null != data) {
            UtilityAdapter.RenderDataPcm(data);
        }
    }
}
