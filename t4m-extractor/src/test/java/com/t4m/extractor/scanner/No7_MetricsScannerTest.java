package com.t4m.extractor.scanner;

import com.t4m.extractor.T4MExtractor;
import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.EntityUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class No7_MetricsScannerTest {

	static ProjectInfo projectInfo;

	@BeforeAll
	public static void initProjectInfo() {
		String path = new File("src/test/resources/JSimulation").getAbsolutePath();
		projectInfo = new ProjectInfo(path);
		T4MExtractor t4MExtractor = new T4MExtractor(projectInfo);
		t4MExtractor.scanMetricData();
	}

	@Test
	@DisplayName("测试类级别的CyclomaticComplexity")
	void testClassLevelCyclomaticComplexity() {
		ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                         "com.simulation.core.CyclomaticComplexityClass");
		assertAll(() -> assertEquals(17, classInfo.getMaxCyclomaticComplexity()),
		          () -> assertEquals("8.50", classInfo.getAvgCyclomaticComplexity()),
		          () -> assertEquals(18, classInfo.getWeightedMethodsCount()));
	}

	@Test
	@DisplayName("测试类级别的RFC")
	void testClassLevelRFC() {
		assertAll(() -> {
			ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
			                                                         "com.simulation.core.CyclomaticComplexityClass");
			assertEquals(4, classInfo.getResponseForClass());
		}, () -> {
			ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
			                                                         "com.simulation.core.xoo.Java8Tester");
			assertEquals(6, classInfo.getResponseForClass());
		}, () -> {
			ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
			                                                         "com.simulation.core.foo.ComplexClassA");
			assertEquals(29, classInfo.getResponseForClass());
		});
	}

	@Test
	@DisplayName("测试类级别的Inheritance")
	void testClassLevelInheritance() {
		assertAll(() -> {
			ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
			                                                         "com.simulation.core.foo.ComplexClassA");
			assertEquals(3, classInfo.getDeepOfInheritanceTree());
		}, () -> {
			ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
			                                                         "com.simulation.core.bar.SimpleInterfaceA");
			assertEquals(1, classInfo.getDeepOfInheritanceTree());
			assertEquals(2, classInfo.getNumberOfChildren());
		});
	}

	@Test
	@DisplayName("测试类级别的Coupling")
	void testClassLevelCoupling() {
		assertAll(() -> {
			ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
			                                                         "com.simulation.core.foo.ComplexClassA");
			assertEquals(15, classInfo.getCouplingBetweenObjects());
			assertEquals(1, classInfo.getAfferentCoupling());
			assertEquals(14, classInfo.getEfferentCoupling());
		});
	}

	@Test
	@DisplayName("测试类级别的Cohesion")
	void testClassLevelCohesion() {
		assertAll(() -> {
			ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
			                                                         "com.simulation.core.CohesionClass");
			assertEquals(3, classInfo.getLackOfCohesionInMethods4());
			assertEquals("0.21", classInfo.getTightClassCohesion());
			assertEquals("0.32", classInfo.getLooseClassCohesion());
		});
	}


	@Test
	@DisplayName("测试类级别的SLOC")
	void testClassLevelSLOC() {
		ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                         "com.simulation.core.SlocClass");
		int[] slocArray = classInfo.getSlocArray();
		assertEquals(10, slocArray[0]); //SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE
		assertEquals(12, slocArray[1]); //SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE
		assertEquals(16, slocArray[2]); //SLOCType.COMMENT_LINES_FROM_SOURCE_FILE
		assertEquals(8, slocArray[3]); //SLOCType.LOGIC_CODE_LINES_FROM_AST
		assertEquals(10, slocArray[4]); //SLOCType.PHYSICAL_CODE_LINES_FROM_AST
		assertEquals(19, slocArray[5]); //SLOCType.COMMENT_LINES_FROM_AST
	}

	@Test
	@DisplayName("测试类级别的Coupling")
	void testPkgLevelCoupling() {
		assertAll(() -> {
			PackageInfo packageInfo = EntityUtil.getPackageByQualifiedName(projectInfo.getPackageList(),
			                                                               "com.simulation.core.bar");
			assertEquals("0.50", packageInfo.getInstability());
			assertEquals("0.62", packageInfo.getAbstractness());
			assertEquals(1, packageInfo.getAfferentCoupling());
			assertEquals(1, packageInfo.getEfferentCoupling());
		});
	}

	@Test
	@DisplayName("测试包级别的SLOC")
	void testPkgLevelSLOC() {
		assertAll(() -> {
			PackageInfo packageInfo = EntityUtil.getPackageByQualifiedName(projectInfo.getPackageList(),
			                                                               "com.simulation.core");
			int[] slocArray = packageInfo.getSlocArrayForCurrentPkg();
			assertEquals(67, slocArray[0]); //SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE
			assertEquals(89, slocArray[1]); //SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE
			assertEquals(39, slocArray[2]); //SLOCType.COMMENT_LINES_FROM_SOURCE_FILE
			assertEquals(61, slocArray[3]); //SLOCType.LOGIC_CODE_LINES_FROM_AST
			assertEquals(83, slocArray[4]); //SLOCType.PHYSICAL_CODE_LINES_FROM_AST
			assertEquals(41, slocArray[5]); //SLOCType.COMMENT_LINES_FROM_AST
		}, () -> {
			PackageInfo packageInfo = EntityUtil.getPackageByQualifiedName(projectInfo.getPackageList(),
			                                                               "com.simulation.core");
			int[] slocArray = packageInfo.getSlocArrayForCurrentAndSubPkg();
			assertEquals(238, slocArray[0]); //SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE
			assertEquals(314, slocArray[1]); //SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE
			assertEquals(95, slocArray[2]); //SLOCType.COMMENT_LINES_FROM_SOURCE_FILE
			assertEquals(218, slocArray[3]); //SLOCType.LOGIC_CODE_LINES_FROM_AST
			assertEquals(311, slocArray[4]); //SLOCType.PHYSICAL_CODE_LINES_FROM_AST
			assertEquals(97, slocArray[5]); //SLOCType.COMMENT_LINES_FROM_AST
		});
	}

	@Test
	@DisplayName("测试模块级别的SLOC")
	void testModuleLevelSLOC() {
		ModuleInfo moduleInfo = EntityUtil.getModuleByShortName(projectInfo.getModuleList(), "JSimulation");
		int[] slocArray = moduleInfo.getSlocArray();
		assertEquals(238, slocArray[0]); //SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE
		assertEquals(314, slocArray[1]); //SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE
		assertEquals(95, slocArray[2]); //SLOCType.COMMENT_LINES_FROM_SOURCE_FILE
		assertEquals(218, slocArray[3]); //SLOCType.LOGIC_CODE_LINES_FROM_AST
		assertEquals(311, slocArray[4]); //SLOCType.PHYSICAL_CODE_LINES_FROM_AST
		assertEquals(97, slocArray[5]); //SLOCType.COMMENT_LINES_FROM_AST
	}

	@Test
	@DisplayName("测试ModuleInfo下的类数量和内部类数量")
	void testNumberOfClassAndInnerClass() {
		ModuleInfo moduleInfo = EntityUtil.getModuleByShortName(projectInfo.getModuleList(), "JSimulation");
		assertEquals(20, moduleInfo.getNumberOfJavaFile());
		assertEquals(30, moduleInfo.getNumberOfAllClass());
	}


}