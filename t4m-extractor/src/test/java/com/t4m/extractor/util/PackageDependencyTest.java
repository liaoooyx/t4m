package com.t4m.extractor.util;

import com.t4m.extractor.ProjectInfoProvider;
import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.scanner.EntityScanner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PackageDependencyTest {

	static ProjectInfo projectInfo1;
	static ProjectInfo projectInfo2;

	@BeforeAll
	public static void initProjectInfo() {
		projectInfo1 = new ProjectInfo();
		projectInfo1.setRootPath("/Users/liao/myProjects/IdeaProjects/sonarqube");
		projectInfo1.setProjectName("TestProject1-sonarqube");

		projectInfo2 = new ProjectInfo();
		projectInfo2.setRootPath("/Users/liao/myProjects/IdeaProjects/comp5911m/refactor");
		projectInfo2.setProjectName("TestProject2-refactor");
	}

	@Test
	void createPkgDependency() {

		EntityScanner entityScanner = new EntityScanner(projectInfo1);
		entityScanner.scan();
		projectInfo1 = entityScanner.getProjectInfo();

		PackageDependency packageDependency = new PackageDependency(projectInfo1);
		packageDependency.createPkgDependency(new File(projectInfo1.getRootPath()), null);

		PathNode rootNode = new PathNode(new File(projectInfo1.getRootPath()).getName(), projectInfo1.getRootPath(),
		                                 true);

		// 建立模块层级关系
		projectInfo1.getModuleList().forEach(moduleInfo -> {
			String suffixPath = moduleInfo.getAbsolutePath().replace(projectInfo1.getRootPath(), "").replaceFirst(
					File.separator, "").strip();
			// 递归生成路径节点
			if (!"".equals(suffixPath)) {
				String[] fileNames = suffixPath.split(File.separator);
				PathNode.createPathNode(fileNames, rootNode, moduleInfo);
			}
		});

		PackageInfo pkgInfo = rootNode.getNextNodeList().get(10).getNextNodeList().get(0).getModuleInfo()
		                              .getMainPackageSet().stream().filter(
						pkg -> "org.sonar.core.issue.tracking".equals(pkg.getFullPackageName())).findFirst().get();

		assertEquals(pkgInfo.getParentPackage().getFullPackageName(), "org.sonar.core.issue");
	}
}