package com.t4m.extractor.util;

import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.scanner.EntityScanner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("第三步")
class PathNodeTest {

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
	@DisplayName("建立模块依赖-测试sonarqube")
	void createModuleDependency1() {

		EntityScanner entityScanner = new EntityScanner(projectInfo1);
		entityScanner.scan();
		projectInfo1 = entityScanner.getProjectInfo();

		PathNode rootNode = new PathNode(new File(projectInfo1.getRootPath()).getName(), projectInfo1.getRootPath(),
		                                 true);

		PathNode.createModuleDependency(rootNode, projectInfo1);

		Assertions.assertEquals(rootNode.getName(), "sonarqube");
		Assertions.assertNull(rootNode.getModuleInfo());
		Assertions.assertEquals(rootNode.getNextNodeList().size(), 18);
		Assertions.assertEquals(rootNode.getNextNodeList().get(1).getName(), "sonar-scanner-engine");
		Assertions.assertEquals(rootNode.getNextNodeList().get(1).getModuleInfo().getSubModuleSet().size(), 31);
	}

	@Test
	@DisplayName("建立模块依赖-测试refactor")
	void createModuleDependency() {

		EntityScanner entityScanner = new EntityScanner(projectInfo2);
		entityScanner.scan();
		projectInfo2 = entityScanner.getProjectInfo();

		PathNode rootNode = new PathNode(new File(projectInfo2.getRootPath()).getName(), projectInfo2.getRootPath(),
		                                 true);

		PathNode.createModuleDependency(rootNode, projectInfo2);

		Assertions.assertEquals(rootNode.getName(), "refactor");
		Assertions.assertEquals(rootNode.getModuleInfo().getPathName(), "refactor/src");
		Assertions.assertEquals(rootNode.getNextNodeList().size(), 1);
		Assertions.assertEquals(rootNode.getNextNodeList().get(0).getName(), "refactor-module2");
		Assertions.assertTrue(
				rootNode.getModuleInfo().getSubModuleSet().contains(rootNode.getNextNodeList().get(0).getModuleInfo()));
	}
}