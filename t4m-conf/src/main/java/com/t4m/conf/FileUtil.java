package com.t4m.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by Yuxiang Liao on 2020-06-17 06:01.
 */
public class FileUtil {

	public static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

	/**
	 * 判断目录是否存在，如果不存在则创建目录
	 */
	public static boolean checkAndMakeDirectory(String dirPath) {
		File dir = new File(dirPath);
		if (!dir.exists()) {
			return dir.mkdirs();
		} else {
			return true;
		}
	}

	public static boolean checkAndMakeDirectory(File dir) {
		if (!dir.exists()) {
			return dir.mkdirs();
		} else {
			return true;
		}
	}
}
