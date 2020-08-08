package com.t4m.extractor.scanner;

import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.EntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by Yuxiang Liao on 2020-06-17 02:40.
 */
public class PackageScanner implements T4MScanner {

	private static final Logger LOGGER = LoggerFactory.getLogger(PackageScanner.class);


	@Override
	public void scan(ProjectInfo projectInfo, ScannerChain scannerChain) {
		LOGGER.info("Extracting the information of package level.");
		String regExpr;
		if ("/".equals(File.separator)) {
			regExpr = "/[^/]*?\\.java";
		} else {
			regExpr = "\\\\[^\\\\]*?\\.java";
		}
		projectInfo.getClassList().forEach(classInfo -> {
			String pkgAbsolutePath = classInfo.getAbsolutePath().replaceFirst(regExpr, "").strip();
			PackageInfo packageInfo = EntityUtil.safeAddEntityToList(new PackageInfo(pkgAbsolutePath),
			                                                         projectInfo.getPackageList());
			packageInfo.setFullyQualifiedName(classInfo.getPackageFullyQualifiedName());
			EntityUtil.safeAddEntityToList(classInfo, packageInfo.getClassList());
			classInfo.setPackageInfo(packageInfo);
		});
		scannerChain.scan(projectInfo);
	}
}
