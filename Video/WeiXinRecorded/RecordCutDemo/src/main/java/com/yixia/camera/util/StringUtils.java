package com.yixia.camera.util;

/**
 * 字符串工具类
 * 
 * @author tangjun
 * 
 */
public class StringUtils {
	public static final String EMPTY = "";

	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0 || str.equalsIgnoreCase("null");
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String trim(String str) {
		return str == null ? EMPTY : str.trim();
	}
}
