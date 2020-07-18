package com.t4m.extractor.util;

import java.text.DecimalFormat;

/**
 * Created by Yuxiang Liao on 2020-07-17 17:23.
 */
public class MathUtil {

	public static float divide(float a, float b) {

		return b == 0 ? 0 : Float.parseFloat(new DecimalFormat("0.00").format(a / b));
	}
}
