package com.yixia.camera;

import java.io.File;

/**
 * 
 * 拍摄SDK
 * 
 * @author yixia.com
 *
 */
public class VCamera {
	/** 视频缓存路径 */
	private static String mVideoCachePath;

	/** 获取视频缓存文件夹 */
	public static String getVideoCachePath() {
		return mVideoCachePath;
	}

	/** 设置视频缓存路径 */
	public static void setVideoCachePath(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}

		mVideoCachePath = path;
	}
}
