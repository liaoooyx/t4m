package com.t4m.extractor.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Yuxiang Liao on 2020-06-18 00:58.
 */
public class PropertyUtil {
	private static Properties properties;

	static {
		InputStream in = PropertyUtil.class.getClassLoader().getResourceAsStream("extractor.properties");
		try {
			properties = new Properties();
			properties.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getProperty(String key){
		return properties.getProperty(key);
	}

	public static void main(String[] args) {
	}

}
