package com.t4m.web.service;

import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.PackageInfo;
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
 * Created by Yuxiang Liao on 2020-06-26 13:57.
 */
@Service("PackageService")
public class PackageService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PackageService.class);

	/**
	 * 构造绑定到前端页面的数据。List的每条数据都是一个包，每个模块的信息以键值对方式存储在Map中。
	 */
	public List<Map<String, Object>> getPackageMapList(int index) {
		ProjectInfo[] projectInfos = ProjectRecord.getProjectInfoRecordByIndex(index);
		ProjectInfo current = projectInfos[0];
		ProjectInfo previous = projectInfos[1];

		List<Map<String, Object>> packageMapList = new ArrayList<>();
		if (previous == null){
			for (PackageInfo packageInfo : current.getPackageList()) {
				Map<String, Object> map = initMapList(packageInfo);
				map.put("type", "old");
				packageMapList.add(map);
			}
		}else {
			//添加新、旧记录
			for (PackageInfo packageInfo : current.getPackageList()) {
				Map<String, Object> map = initMapList(packageInfo);
				PackageInfo pkgOfPreviousRecord = EntityUtil.getPackageByQualifiedName(previous.getPackageList(),
				                                                                       packageInfo.getFullyQualifiedName());
				if (pkgOfPreviousRecord == null) {
					map.put("type", "new");
				} else {
					map.put("type", "old");
				}
				packageMapList.add(map);
			}
			//添加已删除记录
			for (PackageInfo packageInfo : previous.getPackageList()) {
				PackageInfo pkgOfCurrentRecord = EntityUtil.getPackageByQualifiedName(current.getPackageList(),
				                                                                      packageInfo.getFullyQualifiedName());
				if (pkgOfCurrentRecord == null) {
					//	说明该包在当前记录中已被删除
					Map<String, Object> map = initMapList(packageInfo);
					map.put("type", "delete");
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
		map.put("numOfClass", String.valueOf(packageInfo.getNumberOfClasses()));
		map.put("numOfInnerClass", String.valueOf(packageInfo.getNumberOfInnerClasses()));
		return map;
	}
}
