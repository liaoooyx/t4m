package com.t4m.extractor.scanner;

import com.t4m.extractor.entity.ProjectInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("第一步")
class EntityScannerTest {

	static ProjectInfo projectInfo1;
	static ProjectInfo projectInfo2;

	@BeforeAll
	public static void initProjectInfo() {
		projectInfo1 = new ProjectInfo("/Users/liao/myProjects/IdeaProjects/sonarqube");
		projectInfo2 = new ProjectInfo("/Users/liao/myProjects/IdeaProjects/comp5911m/refactor");
		EntityScanner.scan(projectInfo1);
		EntityScanner.scan(projectInfo2);
	}

	@DisplayName("扫描并构造实体")
	@Test
	void scan() {

		assertAll(() -> {
			assertEquals(6989, projectInfo1.getClassList().size());
			assertEquals(1174, projectInfo1.getPackageList().size());
			assertEquals(102, projectInfo1.getModuleList().size());
		}, () -> {
			assertEquals(24, projectInfo2.getClassList().size());
			assertEquals(5, projectInfo2.getPackageList().size());
			assertEquals(2, projectInfo2.getModuleList().size());
		});
	}


}