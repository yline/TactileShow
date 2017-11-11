package com.yixia.camera.util;

public class Log {
	private static boolean gIsLog = true;
	private static final String TAG = "VCamera";

	/**
	 *
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 */
	public static void e(String tag, String msg) {
		if (gIsLog) {
			android.util.Log.e(tag, msg);
		}
	}

	public static void e(String msg) {
		if (gIsLog) {
			android.util.Log.e(TAG, msg);
		}
	}

	/**
	 *
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 * @param tr
	 *            An exception to log
	 */
	public static void e(String tag, String msg, Throwable tr) {
		if (gIsLog) {
			android.util.Log.e(tag, msg, tr);
		}
	}

	public static void e(String msg, Throwable tr) {
		if (gIsLog) {
			android.util.Log.e(TAG, msg, tr);
		}
	}
}
