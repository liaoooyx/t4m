package com.t4m.web.service;

import com.t4m.extractor.entity.ModuleInfo;
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
 * Created by Yuxiang Liao on 2020-06-26 13:48.
 */
@Service("ModuleService")
public class ModuleService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModuleService.class);

	/**
	 * 构造绑定到前端页面的数据。List的每条数据都是一个模块，每个模块的信息以键值对方式存储在Map中。
	 */
	public List<Map<String, Object>> getModuleMapList(int index) {
		ProjectInfo[] projectInfos = ProjectRecord.getProjectInfoRecordByIndex(index);
		ProjectInfo current = projectInfos[0];
		ProjectInfo previous = projectInfos[1];

		List<Map<String, Object>> moduleMapList = new ArrayList<>();
		if (previous == null){
			for (ModuleInfo moduleInfo : current.getModuleList()) {
				Map<String, Object> map = initMapList(moduleInfo);
				map.put("type", "old");
				moduleMapList.add(map);
			}
		}else {
			//添加新、旧记录
			for (ModuleInfo moduleInfo : current.getModuleList()) {
				Map<String, Object> map = initMapList(moduleInfo);
				ModuleInfo moduleOfPreviousRecord = EntityUtil.getModuleByRelativeName(previous.getModuleList(),
				                                                                       moduleInfo.getRelativePath());
				if (moduleOfPreviousRecord == null) {
					map.put("type", "new");
				} else {
					map.put("type", "old");
				}
				moduleMapList.add(map);
			}
			//添加已删除记录
			for (ModuleInfo moduleInfo : previous.getModuleList()) {
				ModuleInfo moduleOfCurrentRecord = EntityUtil.getModuleByRelativeName(current.getModuleList(),
				                                                                      moduleInfo.getRelativePath());
				if (moduleOfCurrentRecord == null) {
					//	说明该模块在当前记录中被删除
					Map<String, Object> map = initMapList(moduleInfo);
					map.put("type", "delete");
					moduleMapList.add(map);
				}
			}
		}
		return moduleMapList;
	}

	/**
	 * 用于dashboard-overview中的第一行图表：关于Project Information
	 */
	private Map<String, Object> initMapList(ModuleInfo moduleInfo) {
		Map<String, Object> map = new HashMap<>();
		map.put("moduleInfo", moduleInfo);
		map.put("moduleName", moduleInfo.getRelativePath());
		if (moduleInfo.hasMainPackageList()) {
			map.put("numOfPkg", String.valueOf(moduleInfo.getMainPackageList().size()));
		} else if (moduleInfo.hasOtherPackageList()) {
			map.put("numOfPkg", String.valueOf(moduleInfo.getOtherPackageList().size()));
		} else {
			map.put("numOfPkg", "0");
		}
		map.put("numOfClass", String.valueOf(moduleInfo.getNumberOfClasses()));
		map.put("numOfInnerClass", String.valueOf(moduleInfo.getNumberOfInnerClasses()));
		return map;
	}
}
