package com.t4m.extractor.metric;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.PackageInfo;

/**
 * Created by Yuxiang Liao on 2020-07-18 03:55.
 */
public class BasicMetric {

	/**
	 * 计算基本信息
	 */
	public static void calculateBasic(PackageInfo packageInfo) {
		packageInfo.setNumberOfJavaFile(packageInfo.getClassList().size());
		packageInfo.setNumberOfAllClass(packageInfo.getAllClassList().size());
	}

	/**
	 * 计算基本信息
	 */
	public static void calculateBasic(ModuleInfo moduleInfo) {
		int numOfJavaFile = 0;
		int numOfAllClass = 0;
		for (PackageInfo packageInfo : moduleInfo.getPackageList()) {
			numOfJavaFile += packageInfo.getNumberOfJavaFile();
			numOfAllClass += packageInfo.getNumberOfAllClass();
		}
		moduleInfo.setNumberOfJavaFile(numOfJavaFile);
		moduleInfo.setNumberOfAllClass(numOfAllClass);
	}

}
