package com.t4m.extractor.scanner;

import com.t4m.conf.GlobalProperties;
import com.t4m.extractor.T4MExtractor;
import com.t4m.extractor.entity.ProjectInfo;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;

class AfterScanAndExtract {

	static ProjectInfo projectInfo;

	@BeforeAll
	public static void initProjectInfo() {
		String path = new File("testProject/JSimulationProject").getAbsolutePath();
		projectInfo = new ProjectInfo(path, GlobalProperties.DEFAULT_EXCLUDED_PATH,GlobalProperties.DEFAULT_DEPENDENCY_PATH);
		T4MExtractor t4MExtractor = new T4MExtractor();
		t4MExtractor.extract(projectInfo);
	}



}