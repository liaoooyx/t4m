package com.t4m.extractor.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Yuxiang Liao on 2020-06-17 06:01.
 */
public class FileUtil {

	public static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

	private FileUtil() {
	}

	/**
	 * Read the content of .java file. Use UTF-8 encoding as default.
	 * @param absolutePath the absolute path of the file
	 */
	public static String readStringFromJavaSourceFile(String absolutePath) {
		String encoding = "UTF-8";
		File file = new File(absolutePath);
		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		try (FileInputStream in = new FileInputStream(file)) {
			in.read(filecontent);
			return new String(filecontent, encoding);
		} catch (FileNotFoundException e) {
			LOGGER.error("Cannot find {}. [{}]", absolutePath, e.toString(), e);
		} catch (IOException e) {
			LOGGER.error("Error happened when retrieving file content. [{}]", e.toString(), e);
		}
		return null;
	}
}
