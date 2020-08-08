package com.t4m.extractor.util;

import java.io.File;
import java.util.Arrays;

/**
 * Created by Yuxiang Liao on 2020-08-08 07:21.
 */
public class RegularExprUtil {

	private static final String FILE_SEPARATOR = File.separator;

	private RegularExprUtil() {
	}
	
	public static String compatibleWithWindows(String regExpr) {
		if ("/".equals(FILE_SEPARATOR)) {
			return regExpr;
		} else {
			return regExpr.replace("/", "\\\\");
		}
	}
}
