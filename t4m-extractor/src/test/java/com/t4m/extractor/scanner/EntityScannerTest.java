package com.t4m.extractor.scanner;

import com.t4m.extractor.entity.ProjectInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("第一步")
class EntityScannerTest {

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

	@DisplayName("扫描并构造实体")
	@Test
	void scan() {
		EntityScanner entityScanner = new EntityScanner(projectInfo1);
		entityScanner.scan();
		Assertions.assertAll("Check list size", () -> Assertions.assertEquals(projectInfo1.getClassList().size(), 6989),
		                     () -> Assertions.assertEquals(projectInfo1.getPackageList().size(), 1174),
		                     () -> Assertions.assertEquals(projectInfo1.getModuleList().size(), 97));
		entityScanner.setProjectInfo(projectInfo2);
		entityScanner.scan();
		Assertions.assertAll("Check list size", () -> Assertions.assertEquals(projectInfo2.getClassList().size(), 24),
		                     () -> Assertions.assertEquals(projectInfo2.getPackageList().size(), 5),
		                     () -> Assertions.assertEquals(projectInfo2.getModuleList().size(), 2));
	}


}