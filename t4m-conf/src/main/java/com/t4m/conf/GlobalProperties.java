package com.t4m.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by Yuxiang Liao on 2020-07-29 13:43.
 */
public class GlobalProperties {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalProperties.class);
	public static final String DB_PATH_KEY = "DB_PATH";
	public static final String EXCLUDED_PATH_KEY = "EXCLUDED_PATH";
	public static final String DEPENDENCY_PATH_KEY = "DEPENDENCY_PATH";
	public static final String CURRENT_PROJECT_IDENTIFIER_KEY = "CURRENT_PROJECT_IDENTIFIER";

	public static final String T4M_ROOT_PATH = System.getenv("T4M_HOME");
	public static final String CONF_ROOT_PATH = T4M_ROOT_PATH + File.separator + "conf";

	public static final String MAIN_PROPERTIES_FILE_NAME = "t4m.properties";
	public static final String WEB_PROPERTIES_FILE_NAME = "web.properties";

	public static final String DB_ROOT_PATH;
	public static final String DEFAULT_EXCLUDED_PATH;
	public static final String DEFAULT_DEPENDENCY_PATH;
	public static String CURRENT_PROJECT_IDENTIFIER;

	static {
		T4mPropertiesUtil t4mProperties = new T4mPropertiesUtil(
				CONF_ROOT_PATH + File.separator + MAIN_PROPERTIES_FILE_NAME);
		DB_ROOT_PATH = T4M_ROOT_PATH + File.separator + t4mProperties.getProperty(DB_PATH_KEY);
		DEFAULT_EXCLUDED_PATH = t4mProperties.getProperty(EXCLUDED_PATH_KEY);
		DEFAULT_DEPENDENCY_PATH = t4mProperties.getProperty(DEPENDENCY_PATH_KEY);
		T4mPropertiesUtil webProperties = new T4mPropertiesUtil(
				CONF_ROOT_PATH + File.separator + WEB_PROPERTIES_FILE_NAME);
		CURRENT_PROJECT_IDENTIFIER = webProperties.getProperty(CURRENT_PROJECT_IDENTIFIER_KEY);
	}

	public static void updateCurrentProjectPointer(String currentProjectName) {
		T4mPropertiesUtil webProperties = new T4mPropertiesUtil(
				CONF_ROOT_PATH + File.separator + WEB_PROPERTIES_FILE_NAME);
		webProperties.setProperty(CURRENT_PROJECT_IDENTIFIER_KEY, currentProjectName);
		CURRENT_PROJECT_IDENTIFIER = currentProjectName;
	}


	public static void main(String[] args) {
		T4mPropertiesUtil t4mProperties = new T4mPropertiesUtil(
				CONF_ROOT_PATH + File.separator + MAIN_PROPERTIES_FILE_NAME);
		t4mProperties.setProperty("asd", "asdd2");
	}

}
