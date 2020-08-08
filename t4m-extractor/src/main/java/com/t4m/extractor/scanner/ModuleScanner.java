package com.t4m.extractor.scanner;

import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.EntityUtil;
import com.t4m.extractor.util.RegularExprUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by Yuxiang Liao on 2020-06-17 02:40.
 */
public class ModuleScanner implements T4MScanner {

	public static final Logger LOGGER = LoggerFactory.getLogger(ModuleScanner.class);

	@Override
	public void scan(ProjectInfo projectInfo, ScannerChain scannerChain) {
		LOGGER.info("Extracting the information of module level.");
		projectInfo.getPackageList().forEach(packageInfo -> {
			String pkgFullName = packageInfo.getFullyQualifiedName();
			String pkgPath = packageInfo.getAbsolutePath();
			String regExprOfPackage = "";
			if (!PackageInfo.EMPTY_IDENTIFIER.equals(packageInfo.getFullyQualifiedName())) {
				regExprOfPackage = RegularExprUtil.compat("/" + pkgFullName.replaceAll("\\.", "/") + "$");
			}
			String moduleAbsolutePathWithSuffix;
			if (!"".equals(regExprOfPackage)) {
				moduleAbsolutePathWithSuffix = pkgPath.replaceAll(regExprOfPackage, "").strip();
			} else {
				moduleAbsolutePathWithSuffix = pkgPath.strip();
			}
			String regExprOfSuffix = RegularExprUtil.compat("/src(/main|/test)/java$");
			String moduleAbsolutePath = moduleAbsolutePathWithSuffix.replaceAll(regExprOfSuffix, "");
			ModuleInfo moduleInfo = EntityUtil.safeAddEntityToList(new ModuleInfo(moduleAbsolutePath),
			                                                       projectInfo.getModuleList());
			// There are three scopes to classify the packages: main, test, other.
			if (moduleAbsolutePathWithSuffix.contains(File.separator + "main")) {
				EntityUtil.safeAddEntityToList(packageInfo, moduleInfo.getMainPackageList());
				moduleInfo.setMainScopePath(moduleAbsolutePathWithSuffix);
			} else if (moduleAbsolutePathWithSuffix.contains(File.separator + "test")) {
				EntityUtil.safeAddEntityToList(packageInfo, moduleInfo.getTestPackageList());
				moduleInfo.setTestScopePath(moduleAbsolutePathWithSuffix);
			} else {
				EntityUtil.safeAddEntityToList(packageInfo, moduleInfo.getOtherPackageList());
				moduleInfo.setOtherScopePath(moduleAbsolutePathWithSuffix);
			}
			packageInfo.setModuleInfo(moduleInfo);
		});
		//If need to modify the classList，packageList of ProjectInfo object, do it here.

		scannerChain.scan(projectInfo);
	}

}
