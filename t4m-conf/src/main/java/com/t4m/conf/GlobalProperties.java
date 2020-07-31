package com.t4m.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yuxiang Liao on 2020-07-29 13:43.
 */
public class GlobalProperties {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalProperties.class);
	public static final String DB_ROOT_PATH_KEY = "DB_ROOT_PATH";
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
	public static Map<String, ProjectIndependentProperties> projectPropertiesMap = new HashMap<>();

	static {
		T4mPropertiesUtil t4mProperties = new T4mPropertiesUtil(
				CONF_ROOT_PATH + File.separator + MAIN_PROPERTIES_FILE_NAME);
		DB_ROOT_PATH = t4mProperties.getProperty(DB_ROOT_PATH_KEY);
		DEFAULT_EXCLUDED_PATH = t4mProperties.getProperty(EXCLUDED_PATH_KEY);
		DEFAULT_DEPENDENCY_PATH = t4mProperties.getProperty(DEPENDENCY_PATH_KEY);
		T4mPropertiesUtil webProperties = new T4mPropertiesUtil(CONF_ROOT_PATH + File.separator +
				                                                        WEB_PROPERTIES_FILE_NAME);
		CURRENT_PROJECT_IDENTIFIER = webProperties.getProperty(CURRENT_PROJECT_IDENTIFIER_KEY);
	}

	public static void updateCurrentProjectPointer(String currentProjectName) {
		T4mPropertiesUtil webProperties = new T4mPropertiesUtil(CONF_ROOT_PATH + File.separator + WEB_PROPERTIES_FILE_NAME);
		webProperties.setProperty(CURRENT_PROJECT_IDENTIFIER_KEY, currentProjectName);
		CURRENT_PROJECT_IDENTIFIER = currentProjectName;
	}

	/**
	 * 返回此项目的排除扫描的路径，如果没有则返回默认值
	 */
	public static String getExcludedPath(String projectName) {
		if (!projectPropertiesMap.containsKey(projectName)) {
			return DEFAULT_EXCLUDED_PATH;
		} else {
			String excludedPath = projectPropertiesMap.get(projectName).excludedPath;
			return excludedPath == null ? DEFAULT_EXCLUDED_PATH : excludedPath;
		}
	}

	/**
	 * 返回此项目的外部jar包依赖的路径，如果没有则返回默认值
	 */
	public static String getDependencyPath(String projectName) {
		if (!projectPropertiesMap.containsKey(projectName)) {
			return DEFAULT_DEPENDENCY_PATH;
		} else {
			String dependencyPath = projectPropertiesMap.get(projectName).dependencyPath;
			return dependencyPath == null ? DEFAULT_DEPENDENCY_PATH : dependencyPath;
		}
	}

	/**
	 * 扫描的目标项目的配置信息
	 */
	static class ProjectIndependentProperties {
		public static final String PROJECT_NAME_KEY = "PROJECT_NAME";
		public static final String PROJECT_EXCLUDED_PATH_KEY = "EXCLUDED_PATH";
		public static final String PROJECT_DEPENDENCY_PATH_KEY = "DEPENDENCY_PATH";

		public final String projectName;
		public final String excludedPath;
		public final String dependencyPath;

		public ProjectIndependentProperties(String projectName, String excludedPath, String dependencyPath) {
			this.projectName = projectName;
			this.excludedPath = excludedPath;
			this.dependencyPath = dependencyPath;
		}

		public ProjectIndependentProperties(T4mPropertiesUtil projectProperties) {
			this.projectName = projectProperties.getProperty(ProjectIndependentProperties.PROJECT_NAME_KEY);
			this.excludedPath = projectProperties.getProperty(ProjectIndependentProperties.PROJECT_EXCLUDED_PATH_KEY);
			this.dependencyPath = projectProperties.getProperty(
					ProjectIndependentProperties.PROJECT_DEPENDENCY_PATH_KEY);
		}

		// void storeToFile() {
		// 	String projectPropertiesFilePath =
		// 			PROJECT_INDEPENDENT_ROOT_PATH + File.separator + projectName + PROPERTIES_FILE_SUFFIX;
		// 	T4mPropertiesUtil propertiesUtil = new T4mPropertiesUtil(projectPropertiesFilePath);
		// 	if (projectName != null) {
		// 		propertiesUtil.setProperty(PROJECT_NAME_KEY, projectName);
		// 	}
		// 	if (excludedPath != null) {
		// 		propertiesUtil.setProperty(PROJECT_EXCLUDED_PATH_KEY, excludedPath);
		// 	}
		// 	if (dependencyPath != null) {
		// 		propertiesUtil.setProperty(PROJECT_DEPENDENCY_PATH_KEY, dependencyPath);
		// 	}
		// }
	}

	public static void main(String[] args) {
		T4mPropertiesUtil t4mProperties = new T4mPropertiesUtil(
				CONF_ROOT_PATH + File.separator + MAIN_PROPERTIES_FILE_NAME);
		t4mProperties.setProperty("asd", "asdd2");
	}

}
