package com.t4m.extractor.scanner;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;

import java.util.List;

/**
 * Created by Yuxiang Liao on 2020-06-17 02:40.
 */
public class PackageScanner {
	/**
	 * 对于列表中的每个{@code ClassInfo}对象，从中读取信息，并转化为{@code PackageInfo}对象. <br> 包括{@code fullyQualifiedName},{@code
	 * absolutePath}, 并添加{@code classInfo}到{@code classList}中
	 */
	public static List<PackageInfo> scan(ProjectInfo projectInfo, List<ClassInfo> classInfoList) {
		classInfoList.forEach(classInfo -> {
			String pkgAbsolutePath = classInfo.getAbsolutePath().replaceFirst("/{1}?[^/]*?\\.java", "").strip();
			// 保证包的唯一性
			PackageInfo packageInfo = projectInfo.safeAddPackageList(new PackageInfo(pkgAbsolutePath));
			packageInfo.setFullyQualifiedName(classInfo.getPackageFullyQualifiedName());
			packageInfo.safeAddClassList(classInfo);
			classInfo.setPackageInfo(packageInfo);
		});
		return projectInfo.getPackageList();
	}

}
