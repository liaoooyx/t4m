package com.t4m.web.util;

import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.PropertyUtil;
import com.t4m.serializer.T4MProjectInfoSerializer;
import com.t4m.serializer.T4MSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Yuxiang Liao on 2020-06-26 04:01.
 */
public class ProjectRecord {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectInfo.class);

	static List<ProjectInfo> projectInfoList;

	/**
	 * 当扫描，切换，新增时，需要主动更新记录。并刷新前端页面
	 */
	public static List<ProjectInfo> updateProjectInfoRecord() {
		T4MSerializer serializer = new T4MProjectInfoSerializer();
		projectInfoList = serializer.deserializeAll();
		return projectInfoList;
	}


	public static List<ProjectInfo> getProjectInfoList() {
		if (projectInfoList == null) {
			projectInfoList = updateProjectInfoRecord();
		}
		return projectInfoList;
	}

	/**
	 * 如果index大于记录长度或者为-1，那么将默认读取最后一条记录。返回数组：第一个元素为当前记录，第二个元素为上一个记录。
	 * 如果只有一条记录，那么返回的2条相同的记录。如果没有记录，则返回null。
	 */
	public static ProjectInfo[] getProjectInfoRecordByIndex(int index) {

		List<ProjectInfo> projectInfoList = ProjectRecord.getProjectInfoList();
		if (projectInfoList.size() == 0){
			return null;
		}
		if (index > projectInfoList.size() - 1 || index < 0) {
			index = projectInfoList.size() - 1;
			LOGGER.warn(
					"The index ({}) of projectInfo list is larger than the size of list ({}), using the index of last element as default.",
					index, projectInfoList.size());
		}
		ProjectInfo current = projectInfoList.get(index);
		ProjectInfo previous = null;
		if (index != 0) {
			previous = projectInfoList.get(index - 1);
		} else {
			previous = current;
		}
		return new ProjectInfo[]{current, previous};
	}

}
