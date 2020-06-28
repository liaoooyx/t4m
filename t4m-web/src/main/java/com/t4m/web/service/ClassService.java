package com.t4m.web.service;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.EntityUtil;
import com.t4m.web.util.ProjectRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yuxiang Liao on 2020-06-26 15:56.
 */
@Service("ClassService")
public class ClassService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClassService.class);

	/**
	 * 构造绑定到前端页面的数据。List的每条数据都是一个包，每个模块的信息以键值对方式存储在Map中。
	 */
	public List<Map<String, Object>> getClassMapList(int index) {
		ProjectInfo[] projectInfos = ProjectRecord.getProjectInfoRecordByIndex(index);
		ProjectInfo current = projectInfos[0];
		ProjectInfo previous = projectInfos[1];

		List<Map<String, Object>> classMapList = new ArrayList<>();
		if (previous == null){
			for (ClassInfo classInfo : current.getClassList()) {
				Map<String, Object> m1 = initMapList(classInfo);
				m1.put("type", "old");
				classMapList.add(m1);
				for (ClassInfo innerClass : classInfo.getInnerClassList()) {
					Map<String, Object> m2 = initMapList(innerClass);
					m2.put("type", "old");
					classMapList.add(m2);
				}
			}
		}else{
			//添加新、旧记录
			for (ClassInfo classInfo : current.getClassList()) {
				Map<String, Object> m1 = initNewAndOldRecord(classInfo, previous.getClassList());
				classMapList.add(m1);
				for (ClassInfo innerClass : classInfo.getInnerClassList()) {
					Map<String, Object> m2 = initNewAndOldRecord(innerClass, previous.getInnerClassList());
					classMapList.add(m2);
				}
			}
			// 添加已删除记录
			for (ClassInfo classInfo : previous.getClassList()) {
				Map<String, Object> m1 = initDeletedRecord(classInfo, current.getClassList());
				if (m1 != null) {
					classMapList.add(m1);
				}
				for (ClassInfo innerClass : classInfo.getInnerClassList()) {
					Map<String, Object> m2 = initDeletedRecord(innerClass, current.getInnerClassList());
					if (m1 != null) {
						classMapList.add(m2);
					}
				}
			}
		}

		return classMapList;
	}

	/**
	 * 添加新、旧记录
	 */
	private Map<String, Object> initNewAndOldRecord(ClassInfo classInfo, List<ClassInfo> previousRecordClassList) {
		Map<String, Object> recordMap = initMapList(classInfo);
		ClassInfo classOfPreviousRecord = EntityUtil.getClassByQualifiedName(previousRecordClassList,
		                                                                     classInfo.getFullyQualifiedName());
		if (classOfPreviousRecord == null) {
			recordMap.put("type", "new");
		} else {
			recordMap.put("type", "old");
		}
		return recordMap;
	}

	/**
	 * 如果该类不存在列表中，说明已被删除，则返回map；如果该类存在列表中，则返回null。
	 */
	private Map<String, Object> initDeletedRecord(ClassInfo classInfo, List<ClassInfo> currentRecordClassList) {

		ClassInfo classOfCurrentRecord = EntityUtil.getClassByQualifiedName(currentRecordClassList,
		                                                                    classInfo.getFullyQualifiedName());
		//	说明该类在当前记录中已被删除, 返回该条被删除的记录
		if (classOfCurrentRecord == null) {
			Map<String, Object> m1 = initMapList(classInfo);
			m1.put("type", "delete");
			return m1;
		}
		return null;
	}

	/**
	 * 用于dashboard-overview中的第三行图表：关于Class Information
	 */
	private Map<String, Object> initMapList(ClassInfo classInfo) {
		Map<String, Object> map = new HashMap<>();
		map.put("classInfo", classInfo);
		map.put("className", classInfo.getShortName());
		map.put("numOfFields", String.valueOf(classInfo.getNumberOfFields()));
		map.put("numOfMethods", String.valueOf(classInfo.getNumberOfMethods()));
		map.put("numOfInnerClass", String.valueOf(classInfo.getInnerClassList().size()));
		map.put("SLOC",
		        String.valueOf(classInfo.getSlocCounterMap().get(ClassInfo.SLOCType.PHYSICAL_LINES_FROM_SOURCE_FILE)));
		return map;
	}
}
