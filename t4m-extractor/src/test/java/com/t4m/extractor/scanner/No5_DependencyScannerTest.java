package com.t4m.extractor.scanner;

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

import static org.junit.jupiter.api.Assertions.*;

class No5_DependencyScannerTest {

	static ProjectInfo projectInfo;

	@BeforeAll
	public static void initProjectInfo() {
		String path = new File("src/test/resources/JSimulation").getAbsolutePath();
		projectInfo = new ProjectInfo(path, GlobalProperties.DEFAULT_EXCLUDED_PATH, GlobalProperties.DEFAULT_DEPENDENCY_PATH);
		T4MExtractor t4MExtractor = new T4MExtractor(projectInfo);
		t4MExtractor.scanDependency();
	}

	@Test
	@DisplayName("测试模块依赖关系")
	void createModuleDependency() {
		ModuleInfo moduleInfo1 = EntityUtil.getModuleByShortName(projectInfo.getModuleList(), "JSimulation");
		ModuleInfo subModule = EntityUtil.getModuleByShortName(moduleInfo1.getSubModuleList(), "submodule1");
		ModuleInfo moduleInfo2 = EntityUtil.getModuleByShortName(projectInfo.getModuleList(), "submodule1");
		assertEquals(moduleInfo2, subModule);

	}

	@Test
	@DisplayName("测试模块信息完整性")
	void testModuleInfoIntegrity() {
		ModuleInfo rootModule = EntityUtil.getModuleByShortName(projectInfo.getModuleList(), "JSimulation");
		assertEquals("JSimulation", rootModule.getRelativePath());
		ModuleInfo subModule1 = EntityUtil.getModuleByShortName(rootModule.getSubModuleList(), "submodule1");
		assertNotNull(subModule1);
		assertEquals("JSimulation/submodule1", subModule1.getRelativePath());
	}

	@Test
	@DisplayName("测试包依赖关系")
	void createPackageDependency() {
		ModuleInfo moduleInfo = EntityUtil.getModuleByShortName(projectInfo.getModuleList(), "JSimulation");
		PackageInfo rootPkg = EntityUtil.getPackageByQualifiedName(moduleInfo.getMainPackageList(),
		                                                           "com.simulation.core");
		PackageInfo pkg = EntityUtil.getPackageByQualifiedName(rootPkg.getSubPackageList(), "com.simulation.core.foo");
		assertNotNull(pkg);
	}
}