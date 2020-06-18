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
public class JavaFileUtil {

	public static final Logger LOGGER = LoggerFactory.getLogger(JavaFileUtil.class);

	/**
	 * 读取Java源文件内容，以字符串返回。默认文件编码为UTF-8
	 */
	public static String readSourceCodeFromJavaFile(String absolutePath) {
		//TODO 考虑文件编码的影响
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

	public static void main(String[] args) {
		String path =
				"/Users/liao/myProjects/IdeaProjects/t4m/t4m-extractor/src/main/java/com/t4m/extractor/util/JavaFileUtil.java";
		String javaSource = JavaFileUtil.readSourceCodeFromJavaFile(path);
		System.out.println(javaSource);
	}
}
