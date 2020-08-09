package com.t4m.web.util.dataset;

import com.t4m.extractor.entity.ClassInfo;

import java.util.Map;

/**
 * Created by Yuxiang Liao on 2020-08-09 01:39.
 */
public class BasicDatasetUtil {

	private BasicDatasetUtil() {
	}

	public static void insertCommonDatasetForTable(Map<String, Object> row, ClassInfo classInfo) {
		row.put("name", classInfo.getShortName());
		row.put("type", classInfo.getClassModifier().toString());
		row.put("declaration", classInfo.getClassDeclaration().toString());
		row.put("package", classInfo.getPackageFullyQualifiedName());
		row.put("module", classInfo.getPackageInfo().getModuleInfo().getShortName());
	}
}
