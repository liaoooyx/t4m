package com.t4m.extractor.processor;

import com.t4m.conf.GlobalProperties;
import com.t4m.extractor.T4MExtractor;
import com.t4m.extractor.entity.ProjectInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PackageScannerTest {

	static ProjectInfo projectInfo;

	@BeforeAll
	public static void initProjectInfo() {
		String path = new File("src/test/resources/JSimulation").getAbsolutePath();
		projectInfo = new ProjectInfo(path, "/build;/out;/output;", GlobalProperties.DEFAULT_DEPENDENCY_PATH);
		T4MExtractor t4MExtractor = new T4MExtractor();
		t4MExtractor.setCustomScannerChain(new DirectoryFileScanner(), new ClassScanner(),
		                                   new PackageScanner()).extract(projectInfo);

	}

	@Test
	@DisplayName("Tesing the number of package")
	void scan() {
		assertEquals(5, projectInfo.getPackageList().size());
	}

}