package com.t4m.extractor.scanner;

import com.t4m.conf.GlobalProperties;
import com.t4m.extractor.T4MExtractor;
import com.t4m.extractor.entity.ProjectInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class No4_ModuleScannerTest {

	static ProjectInfo projectInfo;

	@BeforeAll
	public static void initProjectInfo() {
		String path = new File("src/test/resources/JSimulation").getAbsolutePath();
		projectInfo = new ProjectInfo(path, "/build;/out;/output;", GlobalProperties.DEFAULT_DEPENDENCY_PATH);
		T4MExtractor t4MExtractor = new T4MExtractor();
		t4MExtractor.setCustomScannerChain(new No1_DirectoryFileScanner(), new No2_ClassScanner(),
		                                   new No3_PackageScanner(), new No4_ModuleScanner()).extract(projectInfo);
	}

	@Test
	@DisplayName("测试模块数量")
	void scan() {
		assertEquals(2, projectInfo.getModuleList().size());
	}

}