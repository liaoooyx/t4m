package com.t4m.extractor.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Properties;

/**
 * Created by Yuxiang Liao on 2020-06-18 00:58.
 */
public class PropertyUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertyUtil.class);
	private static Properties properties;

	static {
		InputStream in = PropertyUtil.class.getClassLoader().getResourceAsStream("t4m.properties");
		try {
			properties = new Properties();
			properties.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

	public static void setProperty(String key, String val) {
		//多添加几个值。
		properties.setProperty(key, val);
		//将添加的值，连同以前的值一起写入 新的属性文件里面。
		try {
			URI uri = Objects.requireNonNull(PropertyUtil.class.getClassLoader().getResource("t4m.properties"))
			                 .toURI();
			try (OutputStream out = new FileOutputStream(new File(uri))) {
				properties.store(out, String.format("Add property %s=%s", key, val));
				LOGGER.debug("Write property [{}={}] to [{}]", key, val, uri);
			}
		} catch (Exception e) {
			LOGGER.error("Error happen when storing properties", e);
		}

		//TODO 发布时删除以下部分
		try {
			try (OutputStream out = new FileOutputStream(new File("/Users/liao/myProjects/IdeaProjects/t4m/t4m-web/src/main/resources/t4m.properties"))) {
				properties.store(out, String.format("Add property %s=%s", key, val));
				LOGGER.debug("Write property [{}={}] to [{}]", key, val);
			}
		} catch (Exception e) {
			LOGGER.error("Error happen when storing properties", e);
		}
		// 至此

	}

	public static void main(String[] args) throws URISyntaxException {
		PropertyUtil.setProperty("H","H");
		// System.out.println(new File("src/main/resources/t4m.properties").getAbsolutePath());
	}

}
