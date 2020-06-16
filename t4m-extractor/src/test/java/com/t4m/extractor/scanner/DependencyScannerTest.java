package com.t4m.extractor.scanner;

import com.t4m.extractor.entity.DirectoryNode;
import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class DependencyScannerTest {

	static ProjectInfo projectInfo1;
	static ProjectInfo projectInfo2;

	@BeforeAll
	public static void initProjectInfo() {
		projectInfo1 = new ProjectInfo("/Users/liao/myProjects/IdeaProjects/sonarqube");
		projectInfo2 = new ProjectInfo("/Users/liao/myProjects/IdeaProjects/comp5911m/refactor");
		EntityScanner.scan(projectInfo1);
	}

	@Test
	@DisplayName("测试模块依赖关系")
	void createModuleDependency() {

		DirectoryNode rootNode = new DirectoryNode(new File(projectInfo1.getAbsolutePath()).getName(),
		                                           projectInfo1.getAbsolutePath());

		DependencyScanner.createModuleDependency(rootNode, projectInfo1);

		DirectoryNode directoryNodePlugins = rootNode.getNextNodeList().get(6);
		ModuleInfo xooModule = directoryNodePlugins.getNextNodeList().get(0).getModuleInfo();
		PackageInfo xooPackage = xooModule.getMainPackageList().get(0);
		ClassInfo xooClass = xooPackage.getClassList().get(0);
		PackageInfo xooTestPackage = xooModule.getMainPackageList().get(1);

		assertAll(
				()->{
					assertEquals("sonarqube", rootNode.getName());
					assertEquals(16, rootNode.getNextNodeList().size());
				},
				()->{
					assertEquals("plugins", directoryNodePlugins.getName());
					assertNull(directoryNodePlugins.getModuleInfo());
					assertEquals(1, directoryNodePlugins.getNextNodeList().size());
				},
				()->{
					assertEquals("/Users/liao/myProjects/IdeaProjects/sonarqube/plugins/sonar-xoo-plugin",
					             xooModule.getAbsolutePath());
					assertEquals(9, xooModule.getMainPackageList().size());
				},
				()->{
					assertEquals("org.sonar.xoo", xooPackage.getFullyQualifiedName());
					assertEquals(4, xooPackage.getClassList().size());
				},
				()->{
					assertEquals("Xoo", xooClass.getShortName());
					assertEquals(xooPackage, xooClass.getPackageInfo());
				},
				()->{
					assertEquals("org.sonar.xoo.test",xooTestPackage.getFullyQualifiedName());
					assertEquals(3,xooTestPackage.getClassList().size());
				}
		);

	}

	@Test
	@DisplayName("测试包依赖关系")
	void createPackageDependency() {
		DependencyScanner.createPackageDependency(projectInfo1);
		PackageInfo pkg = projectInfo1.getPackageInfoByFullyQualifiedName("org.sonar.core.issue.tracking");
		assertEquals("org.sonar.core.issue", pkg.getPreviousPackage().getFullyQualifiedName());
	}
}