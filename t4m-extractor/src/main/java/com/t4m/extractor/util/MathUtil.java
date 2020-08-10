package com.t4m.extractor.util;

import java.text.DecimalFormat;

/**
 * Created by Yuxiang Liao on 2020-07-17 17:23.
 */
public class MathUtil {

	private MathUtil() {
	}

	public static String divide(float a, float b) {
		return b == 0 ? "0.00" : new DecimalFormat("0.00").format(a / b);
	}

	public static String percentage(float a, float b) {
		return b == 0 ? "0.00" : new DecimalFormat("0.00").format((a / b) * 100);
	}

	public static String abs(float a) {
		return new DecimalFormat("0.00").format(Math.abs(a));
	}

}
