package com.t4m.web.service;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.EntityUtil;
import com.t4m.extractor.util.TimeUtil;
import com.t4m.web.util.ProjectRecord;
import com.t4m.web.util.SLOCUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

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
		if (previous == null) {
			for (ModuleInfo moduleInfo : current.getModuleList()) {
				Map<String, Object> map = initMapList(moduleInfo);
				map.put("newness", "old");
				moduleMapList.add(map);
			}
		} else {
			//添加新、旧记录
			for (ModuleInfo moduleInfo : current.getModuleList()) {
				Map<String, Object> map = initMapList(moduleInfo);
				ModuleInfo moduleOfPreviousRecord = EntityUtil.getModuleByRelativeName(previous.getModuleList(),
				                                                                       moduleInfo.getRelativePath());
				if (moduleOfPreviousRecord == null) {
					map.put("newness", "new");
				} else {
					map.put("newness", "old");
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
					map.put("newness", "delete");
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

	/**
	 * 用于dashboard-sloc的列表数据，获取所有模块的SLOC信息
	 */
	public List<Map<String, Object>> getAllModulesSLOC(int index) {
		ProjectInfo projectInfo = ProjectRecord.getProjectInfoRecordByIndex(index)[0];
		List<Map<String, Object>> rows = new ArrayList<>();
		for (ModuleInfo moduleInfo : projectInfo.getModuleList()) {
			Map<String, Object> cols = SLOCUtil.initSLOCRowRecordForFrontPage(moduleInfo.getRelativePath(), "module",
			                                                                  moduleInfo.getSumOfSLOC());
			rows.add(cols);
		}
		return rows;
	}

	/**
	 * 用于dashboard-sloc的列表数据。根据模块名，获取其下的第一层包的SLOC
	 */
	public List<Map<String, Object>> getSLOCRecordByModuleName(String moduleName, int index) {
		ProjectInfo projectInfo = ProjectRecord.getProjectInfoRecordByIndex(index)[0];
		ModuleInfo moduleInfo = EntityUtil.getModuleByRelativeName(projectInfo.getModuleList(), moduleName);
		List<Map<String, Object>> rows = new ArrayList<>();
		for (PackageInfo packageInfo : moduleInfo.getPackageList()) {
			if (!packageInfo.hasPreviousPackage()) { // 说明是第一层包
				Map<String, Object> colsAkaRow = SLOCUtil.initSLOCRowRecordForFrontPage(
						packageInfo.getFullyQualifiedName(), "package", packageInfo.getSumOfSLOCForCurrentAndSubPkg());
				rows.add(colsAkaRow);
			}
		}
		return rows;
	}

	/**
	 * 返回用于dashboard-sloc页面的表格弹框的chart数据集
	 */
	public List<String[]> getSLOCTableChartDataset(String moduleName) {
		List<String[]> dataset = new ArrayList<>();
		dataset.add(new String[]{"time", "Logic Code Line (Source File)", "Physical Code Line (Source File)",
		                         "All Comment Line", "Logic Code Line (AST)", "Physical Code Line (AST)",
		                         "JavaDoc Comment Line"});
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			ModuleInfo moduleInfo = EntityUtil.getModuleByRelativeName(projectInfo.getModuleList(), moduleName);
			String[] tempRow = new String[7];
			tempRow[0] = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			Arrays.fill(tempRow,1,6,null);
			if (moduleInfo != null){
				int[] slocArray = moduleInfo.getSumOfSLOC();
				for (int i = 0; i < 6; i++) {
					tempRow[i + 1] = String.valueOf(slocArray[i]);
				}
			}
			dataset.add(tempRow);
		}
		return dataset;
	}

}
