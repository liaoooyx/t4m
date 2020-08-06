package com.t4m.extractor.scanner;

import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.EntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Yuxiang Liao on 2020-06-17 02:40.
 */
public class No3_PackageScanner {

	private static final Logger LOGGER = LoggerFactory.getLogger(No3_PackageScanner.class);

	private ProjectInfo projectInfo;

	public No3_PackageScanner(ProjectInfo projectInfo) {
		this.projectInfo = projectInfo;
	}

	/**
	 * 对于列表中的每个{@code ClassInfo}对象，从中读取信息，并转化为{@code PackageInfo}对象. <br> 包括{@code fullyQualifiedName},{@code
	 * absolutePath}, 并添加{@code classInfo}到{@code classList}中
	 */
	public List<PackageInfo> scan() {
		LOGGER.info("Extracting the information of package level.");
		projectInfo.getClassList().forEach(classInfo -> {
			String pkgAbsolutePath = classInfo.getAbsolutePath().replaceFirst("/{1}?[^/]*?\\.java", "").strip();
			// 保证包的唯一性
			PackageInfo packageInfo = EntityUtil.safeAddEntityToList(new PackageInfo(pkgAbsolutePath),
			                                                         projectInfo.getPackageList());
			packageInfo.setFullyQualifiedName(classInfo.getPackageFullyQualifiedName());
			EntityUtil.safeAddEntityToList(classInfo, packageInfo.getClassList());
			classInfo.setPackageInfo(packageInfo);
		});
		return projectInfo.getPackageList();
	}

}
