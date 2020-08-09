package com.t4m.web.util;

import com.t4m.conf.GlobalProperties;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.serializer.T4MProjectInfoSerializer;
import com.t4m.serializer.T4MSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by Yuxiang Liao on 2020-06-26 04:01.
 */
public class ProjectRecordUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectRecordUtil.class);

	private static List<ProjectInfo> projectInfoList;

	private ProjectRecordUtil() {
	}

	/**
	 * When doing scan, create, and switch operation, update the records cache.
	 */
	public static List<ProjectInfo> updateProjectInfoRecord() {
		T4MSerializer serializer = new T4MProjectInfoSerializer();
		projectInfoList = serializer.deserializeAll();
		return serializer.deserializeAll();
	}

	public static List<ProjectInfo> getProjectInfoList() {
		if (projectInfoList == null) {
			projectInfoList = updateProjectInfoRecord();
		}
		return projectInfoList;
	}

	/**
	 * 检测当前项目指针所指向的路径是否存在，如果不存在则清空该指针
	 */
	public static void checkCurrentProjectIdentifier() {
		File file = new File(
				GlobalProperties.DB_ROOT_PATH + File.separator + GlobalProperties.getCurrentProjectIdentifier());
		if (!file.exists()) {
			GlobalProperties.updateCurrentProjectPointer("");
		}
	}

	/**
	 * 如果index大于记录长度或者为-1，那么将默认读取最后一条记录。
	 * 返回数组：第一个元素为当前记录，第二个元素为上一个记录。 如果只有一条记录，那么返回的2条相同的记录。如果没有记录，则返回空数组。
	 */
	public static ProjectInfo[] getTwoProjectInfoRecordByIndex(int index) {
		List<ProjectInfo> projInfoList = getProjectInfoList();
		if (projInfoList.isEmpty()) {
			return new ProjectInfo[]{null, null};
		}
		if (index > projInfoList.size() - 1 || index < 0) {
			index = projInfoList.size() - 1;
		}
		ProjectInfo current = projInfoList.get(index);
		ProjectInfo previous;
		if (index != 0) {
			previous = projInfoList.get(index - 1);
		} else {
			previous = new ProjectInfo(current.getAbsolutePath(), current.getExcludedPath(),
			                           current.getDependencyPath(), current.getCreateDate());
		}
		return new ProjectInfo[]{current, previous};
	}

	/**
	 * @return Empty list, it no records. Or a list of records.
	 */
	public static List<String> getAllProjectRecordsDirName() {
		List<String> projectDirList = new ArrayList<>();
		String dbPath = GlobalProperties.DB_ROOT_PATH;
		File rootDir = new File(dbPath);
		if (rootDir.exists()) {
			try {
				for (File file : Objects.requireNonNull(rootDir.listFiles())) {
					if (file.isDirectory()) {
						projectDirList.add(file.getName());
					}
				}
				return projectDirList;
			} catch (NullPointerException e) {
				LOGGER.error("There is no project record dir in DB Directory: [{}].", dbPath);
			}
		} else {
			LOGGER.error("The DB Directory: [{}] does not exist.", dbPath);
		}
		return projectDirList;
	}
}
