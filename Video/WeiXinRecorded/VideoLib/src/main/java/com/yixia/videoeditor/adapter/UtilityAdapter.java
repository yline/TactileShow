package com.yixia.videoeditor.adapter;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class UtilityAdapter {
	static {
		System.loadLibrary("utility");
	}

	/** 初始化底层库 */
	public static native void FFmpegInit(Object context, String settings);

	/** 获取当前转码时间 */
	public static native int FFmpegVideoGetTransTime(int flag);

	/** 开始播放器数据的录制 */
	public static native boolean VitamioStartRecord(String yuv, String pcm);

	/** 停止播放器数据的录制 */
	public static native int VitamioStopRecord(int flag);

	/** 获取视频回调指针 */
	public static native int GetVitamioVideoCallbackPointer(int flag);

	/** 获取音频回调指针 */
	public static native int GetVitamioAudioCallbackPointer(int flag);

	/** 获取视频旋转信息 */
	public static native int VideoGetMetadataRotate(String filename);

	/**
	 * 执行ffmpeg命令 tag 任务的唯一标识，如果标识为""，以阻塞方式运行，否则以异步方式运行 FFmpegRun("",
	 * "ffmpeg -i \"生成的mp4\" -y -f image2 -ss 1 -t 0.001 -s 480x480 \"输出.jpg\" "
	 * )
	 * 
	 * @param strtag
	 *            任务的唯一标识，如果标识为""，以阻塞方式运行，否则以异步方式运行
	 * @param strcmd
	 *            命令行
	 * @return 返回执行结果
	 */
	public static native int FFmpegRun(String tag, String cmd);

	/** 结束异步执行的ffmpeg */
	public static native void FFmpegKill(String tag);

	/** 检测ffmpeg实例是否正在运行 */
	public static native boolean FFmpegIsRunning(String tag);

	/** 获取视频信息，相当于调用ffprobe */
	public static native String FFmpegVideoGetInfo(String filename);

	/**
	 * 传入参数width，height为surfaceview创建时给出的，后面的outwidth，outheight为输出的视频高宽，初始化时传入0，0，返回纹理id
	 * @param width surfaceview创建时给出的
	 * @param height surfaceview创建时给出的
	 * @return
	 */
	public static native int RenderViewInit(int width, int height);

	/**
	 * 设置输入参数
	 * 
	 * @param inw 视频输入宽
	 * @param inh 视频输入高
	 * @param org 后置摄像头0，前置摄像头180
	 * @param flip 后置摄像头FLIPTYPE_NORMAL，前摄像头FLIPTYPE_HORIZONTAL
	 */
	public static native void RenderInputSettings(int inw, int inh, int org, int flip);

	/**
	 * 设置视频输出参数
	 * 
	 * @param outw 视频输出宽
	 * @param outh 视频输出高
	 * @param outfps  视频输出帧率
	 * @param format  视频输出格式，参考OUTPUTFORMAT_*
	 */
	public static native void RenderOutputSettings(int outw, int outh, int outfps, int format);

	public static native void RenderSetFilter(int type, String filter);

	//进行显示
	public static native void RenderStep();

	//提供摄像头数据
	public static native void RenderDataYuv(byte[] yuv);

	/** 提供录音数据，必须是44100Hz，1channel，16bit unsigned */
	public static native void RenderDataPcm(byte[] pcm);

	/** 获取最后一帧数据，如果失败会返回一副全透明的图，如果内存失败，会返回空，alpha的值为0-1，0为全透明 */
	public static native int[] RenderGetDataArgb(float alpha);

	/** 设置输出数据文件，设置完就开始录制 */
	public static native boolean RenderOpenOutputFile(String video, String audio);

	/** 关闭输出数据文件，关闭后就停止录制 */
	public static native void RenderCloseOutputFile();

	/** 关闭输出数据文件，关闭后就停止录制 */
	public static native boolean RenderIsOutputJobFinish();

	/** 暂停录制 */
	public static native void RenderPause(boolean pause);

	/**
	 * 特效处理器
	 * 
	 * @param settings 特效设置: inv=/sdcard/v.rgb; ina=/sdcard/p.pcm; out=/sdcard/o.mp4; text=/sdcard/txt.png
	 * @param surface Surface
	 * @param holder SurfaceHolder
	 */
	public static native boolean FilterParserInit(String strings, Object surface);

	public static native int FilterParserInfo(int mode);

	/** 停止特效处理 */
	public static native void FilterParserFree();

	/**
	 * 特效处理
	 * 
	 * @param settings
	 * @param actiontype
	 * @return
	 */
	public static native int FilterParserAction(String settings, int actiontype);

	public static native boolean SaveData(String filename, int[] data, int flag);

	/**
	 * 变声
	 * 
	 * @param inPath wav音频输入
	 * @param outPath wav音频输出
	 * @param tempoChange 变速(语速增加%xx)
	 * @param pitch  // 音幅变调
	 * @param pitchSemitone //音程变调
	 */
	public static native int SoundEffect(String inPath, String outPath, float tempoChange, float pitch, int pitchSemitone);
}
