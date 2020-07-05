package com.t4m.web.util;

import com.t4m.extractor.util.PropertyUtil;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by Yuxiang Liao on 2020-07-03 03:49.
 */
public class GlobalVariable {

	public static String CURRENT_PROJECT_NAME = PropertyUtil.getProperty("CURRENT_PROJECT_NAME");

	/**
	 * 将同步更新至t4m.properties中
	 */
	public static void updateCurrentProjectName(String currentProjectName) {
		CURRENT_PROJECT_NAME = currentProjectName;
		PropertyUtil.setProperty("CURRENT_PROJECT_NAME", currentProjectName);
	}
}
