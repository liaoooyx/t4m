package com.t4m.extractor.processor;

import com.t4m.conf.GlobalProperties;
import com.t4m.extractor.T4MExtractor;
import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.EntityUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BelongingnessScannerTest {

	static ProjectInfo projectInfo;

	@BeforeAll
	public static void initProjectInfo() {
		String path = new File("src/test/resources/JSimulation").getAbsolutePath();
		projectInfo = new ProjectInfo(path, "/build;/out;/output;", GlobalProperties.DEFAULT_DEPENDENCY_PATH);
		T4MExtractor t4MExtractor = new T4MExtractor();
		t4MExtractor.setCustomScannerChain(new DirectoryFileScanner(), new ClassScanner(), new PackageScanner(),
		                                   new ModuleScanner(), new BelongingnessScanner()).extract(projectInfo);

	}

	@Test
	@DisplayName("Tesing module dependencies")
	void createModuleDependency() {
		ModuleInfo moduleInfo1 = EntityUtil.getModuleByShortName(projectInfo.getModuleList(), "JSimulation");
		ModuleInfo subModule = EntityUtil.getModuleByShortName(moduleInfo1.getSubModuleList(), "submodule1");
		ModuleInfo moduleInfo2 = EntityUtil.getModuleByShortName(projectInfo.getModuleList(), "submodule1");
		assertEquals(moduleInfo2, subModule);

	}

	@Test
	@DisplayName("Testing module information integrity")
	void testModuleInfoIntegrity() {
		ModuleInfo rootModule = EntityUtil.getModuleByShortName(projectInfo.getModuleList(), "JSimulation");
		assertEquals("JSimulation", rootModule.getRelativePath());
		ModuleInfo subModule1 = EntityUtil.getModuleByShortName(rootModule.getSubModuleList(), "submodule1");
		assertNotNull(subModule1);
		assertEquals("JSimulation" + File.separator + "submodule1", subModule1.getRelativePath());
	}

	@Test
	@DisplayName("Testing package dependencies")
	void createPackageDependency() {
		ModuleInfo moduleInfo = EntityUtil.getModuleByShortName(projectInfo.getModuleList(), "JSimulation");
		PackageInfo rootPkg = EntityUtil.getPackageByQualifiedName(moduleInfo.getMainPackageList(),
		                                                           "com.simulation.core");
		PackageInfo pkg = EntityUtil.getPackageByQualifiedName(rootPkg.getSubPackageList(), "com.simulation.core.foo");
		assertNotNull(pkg);
	}
}