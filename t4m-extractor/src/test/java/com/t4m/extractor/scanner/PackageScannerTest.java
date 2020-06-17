package com.t4m.extractor.scanner;

import com.t4m.extractor.entity.ProjectInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PackageScannerTest {

	static ProjectInfo projectInfo1;
	static ProjectInfo projectInfo2;

	@BeforeAll
	public static void initProjectInfo() {
		projectInfo1 = new ProjectInfo("/Users/liao/myProjects/IdeaProjects/sonarqube");
		projectInfo2 = new ProjectInfo("/Users/liao/myProjects/IdeaProjects/comp5911m/refactor");
		T4MScanner t4MScanner = new T4MScanner(projectInfo1);
		t4MScanner.scanPackageAndClassAndDirectory();
	}

	@Test
	@DisplayName("获取项目路径下所有Java文件")
	void scan() {
		assertEquals(1174, projectInfo1.getPackageList().size());

	}

}