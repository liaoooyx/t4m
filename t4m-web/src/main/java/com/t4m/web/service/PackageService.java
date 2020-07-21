package com.t4m.web.service;

import com.t4m.extractor.entity.ClassInfo;
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
 * Created by Yuxiang Liao on 2020-06-26 13:57.
 */
@Service("PackageService")
public class PackageService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PackageService.class);

	/**
	 * 构造绑定到前端页面的数据。List的每条数据都是一个包，每个模块的信息以键值对方式存储在Map中。
	 */
	public List<Map<String, Object>> getPackageMapList(int index) {
		ProjectInfo[] projectInfos = ProjectRecord.getTwoProjectInfoRecordByIndex(index);
		ProjectInfo current = projectInfos[0];
		ProjectInfo previous = projectInfos[1];

		List<Map<String, Object>> packageMapList = new ArrayList<>();
		if (previous == null) {
			for (PackageInfo packageInfo : current.getPackageList()) {
				Map<String, Object> map = initMapList(packageInfo);
				map.put("newness", "old");
				packageMapList.add(map);
			}
		} else {
			//添加新、旧记录
			for (PackageInfo packageInfo : current.getPackageList()) {
				Map<String, Object> map = initMapList(packageInfo);
				PackageInfo pkgOfPreviousRecord = EntityUtil.getPackageByQualifiedName(previous.getPackageList(),
				                                                                       packageInfo
						                                                                       .getFullyQualifiedName());
				if (pkgOfPreviousRecord == null) {
					map.put("newness", "new");
				} else {
					map.put("newness", "old");
				}
				packageMapList.add(map);
			}
			//添加已删除记录
			for (PackageInfo packageInfo : previous.getPackageList()) {
				PackageInfo pkgOfCurrentRecord = EntityUtil.getPackageByQualifiedName(current.getPackageList(),
				                                                                      packageInfo
						                                                                      .getFullyQualifiedName());
				if (pkgOfCurrentRecord == null) {
					//	说明该包在当前记录中已被删除
					Map<String, Object> map = initMapList(packageInfo);
					map.put("newness", "delete");
					packageMapList.add(map);
				}
			}
		}
		return packageMapList;
	}

	/**
	 * 用于dashboard-overview中的第一行图表：关于Project Information
	 */
	private Map<String, Object> initMapList(PackageInfo packageInfo) {
		Map<String, Object> map = new HashMap<>();
		map.put("packageInfo", packageInfo);
		map.put("packageName", packageInfo.getFullyQualifiedName());
		map.put("numOfClass", String.valueOf(packageInfo.getNumberOfJavaFile()));
		map.put("numOfInnerClass", String.valueOf(packageInfo.getNumberOfAllClass()));
		return map;
	}

	/**
	 * 用于dashboard-sloc的列表数据。根据包名，获取其下的类的SLOC，以及子包的SLOC
	 */
	public List<Map<String, Object>> getSLOCRecordByPackageName(String packageName, int index) {
		ProjectInfo projectInfo = ProjectRecord.getTwoProjectInfoRecordByIndex(index)[0];
		PackageInfo packageInfo = EntityUtil.getPackageByQualifiedName(projectInfo.getPackageList(), packageName);
		List<Map<String, Object>> rows = new ArrayList<>();
		// 子包的SLOC（直接类，和下一层的子包）
		for (PackageInfo subPkg : packageInfo.getSubPackageList()) {
			Map<String, Object> rowForSubPkg = SLOCUtil.initSLOCRowRecordForFrontPage(
					subPkg.getFullyQualifiedName(), "package", subPkg.getSlocArrayForCurrentAndSubPkg());
			rows.add(rowForSubPkg);
		}
		// 当前包的SLOC（直接类，不包括子包）
		Map<String, Object> rowForPkg = SLOCUtil.initSLOCRowRecordForFrontPage(
				packageInfo.getFullyQualifiedName(), "current package", packageInfo.getSlocArrayForCurrentPkg());
		rows.add(rowForPkg);
		for (ClassInfo classInfo : packageInfo.getClassList()) {
			Map<String, Object> rowForClass = SLOCUtil.initSLOCRowRecordForFrontPage(
					classInfo.getFullyQualifiedName(), "class", classInfo.getSlocArray());
			rows.add(rowForClass);
		}
		return rows;
	}

	/**
	 * 返回用于dashboard-sloc页面的表格弹框的chart数据集
	 */
	public List<String[]> getSLOCTableChartDataset(String pkgName, boolean includeSubPkgSLOC) {
		List<String[]> dataset = new ArrayList<>();
		dataset.add(new String[]{"time", "Logic Code Line (Source File)", "Physical Code Line (Source File)",
		                         "All Comment Line", "Logic Code Line (AST)", "Physical Code Line (AST)",
		                         "JavaDoc Comment Line"});
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			PackageInfo packageInfo = EntityUtil.getPackageByQualifiedName(projectInfo.getPackageList(), pkgName);
			String[] tempRow = new String[7];
			tempRow[0] = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			Arrays.fill(tempRow, 1, 6, null);
			if (packageInfo != null){
				int[] slocArray;
				if (includeSubPkgSLOC){
					slocArray = packageInfo.getSlocArrayForCurrentAndSubPkg();
				}else {
					slocArray = packageInfo.getSlocArrayForCurrentPkg();
				}
				for (int i = 0; i < 6; i++) {
					tempRow[i + 1] = String.valueOf(slocArray[i]);
				}
			}
			dataset.add(tempRow);
		}
		return dataset;
	}
}
