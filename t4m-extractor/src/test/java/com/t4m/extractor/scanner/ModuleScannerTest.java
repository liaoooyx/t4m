package com.t4m.extractor.scanner;

import com.t4m.extractor.entity.ProjectInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModuleScannerTest {

	static ProjectInfo projectInfo1;
	static ProjectInfo projectInfo2;

	@BeforeAll
	public static void initProjectInfo() {
		projectInfo1 = new ProjectInfo("/Users/liao/myProjects/IdeaProjects/sonarqube");
		projectInfo2 = new ProjectInfo("/Users/liao/myProjects/IdeaProjects/comp5911m/refactor");
		List<File> rawJavaFileList = new ArrayList<>();
		DirectoryScanner.scan(projectInfo1,rawJavaFileList);
		ClassScanner.scan(projectInfo1,rawJavaFileList);
		PackageScanner.scan(projectInfo1,projectInfo1.getClassList());
		ModuleScanner.scan(projectInfo1,projectInfo1.getPackageList());
	}

	@Test
	@DisplayName("获取项目路径下所有Java文件")
	void scan() {
		assertEquals(102, projectInfo1.getModuleList().size());
	}

}