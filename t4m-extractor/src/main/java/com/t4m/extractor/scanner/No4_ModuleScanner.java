package com.t4m.extractor.scanner;

import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.EntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * Created by Yuxiang Liao on 2020-06-17 02:40.
 */
public class No4_ModuleScanner {

	public static final Logger LOGGER = LoggerFactory.getLogger(No4_ModuleScanner.class);

	private ProjectInfo projectInfo;

	public No4_ModuleScanner(ProjectInfo projectInfo) {
		this.projectInfo = projectInfo;
	}

	/**
	 * 根据列表中的{@code PackageInfo}创建模块信息
	 */
	public List<ModuleInfo> scan() {
		LOGGER.info("Extracting the information of module level.");
		projectInfo.getPackageList().forEach(packageInfo -> {
			String pkgFullName = packageInfo.getFullyQualifiedName();
			String pkgPath = packageInfo.getAbsolutePath();
			// 去除包名路径，得到模块添加路径
			String regx = "";
			if (!PackageInfo.EMPTY_IDENTIFIER.equals(packageInfo.getFullyQualifiedName())) {
				regx = File.separator + pkgFullName.replaceAll("\\.", File.separator) + "$";
			}
			String moduleAbsolutePathWithSuffix;
			if (!"".equals(regx)) {
				moduleAbsolutePathWithSuffix = pkgPath.replaceAll(regx, "").strip();
			} else {
				moduleAbsolutePathWithSuffix = pkgPath.strip();
			}
			String regex =
					File.separator + "src(" + File.separator + "main|" + File.separator + "test)" + File.separator +
							"java$"; // "/src(/main|/test)/java"
			String moduleAbsolutePath = moduleAbsolutePathWithSuffix.replaceAll(regex, "");
			// 保证模块的唯一性
			ModuleInfo moduleInfo = EntityUtil.safeAddEntityToList(new ModuleInfo(moduleAbsolutePath),
			                                                       projectInfo.getModuleList());
			// 为模块添加子包，分为3个域：main，test，other。包括域路径和域下的包
			// 包在加入列表中时，已去重
			if (moduleAbsolutePathWithSuffix.contains(File.separator + "main")) {
				EntityUtil.safeAddEntityToList(packageInfo,moduleInfo.getMainPackageList());
				moduleInfo.setMainScopePath(moduleAbsolutePathWithSuffix);
			} else if (moduleAbsolutePathWithSuffix.contains(File.separator + "test")) {
				EntityUtil.safeAddEntityToList(packageInfo,moduleInfo.getTestPackageList());
				moduleInfo.setTestScopePath(moduleAbsolutePathWithSuffix);
			} else {
				EntityUtil.safeAddEntityToList(packageInfo,moduleInfo.getOtherPackageList());
				moduleInfo.setOtherScopePath(moduleAbsolutePathWithSuffix);
			}
			packageInfo.setModuleInfo(moduleInfo);
		});
		//如果需要重置project的classList，packageList，则在这里进行
		return projectInfo.getModuleList();
	}

}
