package com.t4m.extractor.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yuxiang Liao on 2020-06-25 01:00.
 */
public class TimeUtil {
	public static String formatToLogFileName(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		return format.format(date);
	}

	public static String formatToStandardDatetime(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}
}
