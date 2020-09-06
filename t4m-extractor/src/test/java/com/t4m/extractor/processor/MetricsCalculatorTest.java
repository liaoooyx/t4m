package com.t4m.extractor.processor;

import com.t4m.conf.GlobalProperties;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MetricsCalculatorTest {

	static ProjectInfo projectInfo;

	@BeforeAll
	public static void initProjectInfo() {
		String path = new File("src/test/resources/JSimulation").getAbsolutePath();
		projectInfo = new ProjectInfo(path, "/build;/out;/output;", GlobalProperties.DEFAULT_DEPENDENCY_PATH);
		T4MExtractor t4MExtractor = new T4MExtractor();
		t4MExtractor.setCustomScannerChain(new DirectoryFileScanner(), new ClassScanner(),
		                                   new PackageScanner(), new ModuleScanner(),
		                                   new BelongingnessScanner(), new SourceCodeResolver(),
		                                   new MetricsCalculator()).extract(projectInfo);
	}

	@Test
	@DisplayName("Testing CyclomaticComplexity in class level")
	void testClassLevelCyclomaticComplexity() {
		ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                         "com.simulation.core.CyclomaticComplexityClass");
		assertAll(() -> assertEquals(17, classInfo.getMaxCyclomaticComplexity()),
		          () -> assertEquals("8.50", classInfo.getAvgCyclomaticComplexity()),
		          () -> assertEquals(18, classInfo.getWeightedMethodsCount()));
	}

	@Test
	@DisplayName("Testing the Response For a Class in class level")
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
			assertEquals(28, classInfo.getResponseForClass());
		});
	}

	@Test
	@DisplayName("Testing the Inheritance in class level")
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
	@DisplayName("Testing the Coupling in class level")
	void testClassLevelCoupling() {
		assertAll(() -> {
			ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
			                                                         "com.simulation.core.foo.ComplexClassA");
			assertEquals(15, classInfo.getCouplingBetweenObjects());
			assertEquals(1, classInfo.getAfferentCoupling());
			assertEquals(15, classInfo.getEfferentCoupling());
			assertEquals(17, classInfo.getMessagePassingCoupling());
		});
	}

	@Test
	@DisplayName("Testing the Cohesion in class level")
	void testClassLevelCohesion() {
		assertAll(() -> {
			ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
			                                                         "com.simulation.core.CohesionClass");
			assertEquals(3, classInfo.getLackOfCohesionOfMethods4());
			assertEquals("0.21", classInfo.getTightClassCohesion());
			assertEquals("0.32", classInfo.getLooseClassCohesion());
		});
	}


	@Test
	@DisplayName("Testing the Lines of Code in class level")
	void testClassLevelSLOC() {
		ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                         "com.simulation.core.SlocClass");
		int[] slocArray = classInfo.getSlocArray();
		assertEquals(10, slocArray[0]); //SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE
		assertEquals(12, slocArray[1]); //SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE
		assertEquals(16, slocArray[2]); //SLOCType.COMMENT_LINES_FROM_SOURCE_FILE
		assertEquals(18, slocArray[3]); //SLOCType.TOTAL_LINES_FROM_SOURCE_FILE
		assertEquals(8, slocArray[4]); //SLOCType.LOGIC_CODE_LINES_FROM_AST
		assertEquals(10, slocArray[5]); //SLOCType.PHYSICAL_CODE_LINES_FROM_AST
		assertEquals(19, slocArray[6]); //SLOCType.COMMENT_LINES_FROM_AST
		assertEquals(28, slocArray[7]); //SLOCType.TOTAL_LINES_FROM_AST
	}

	@Test
	@DisplayName("Testing the Coupling in package level")
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
	@DisplayName("Testing the Lines of Code in package level")
	void testPkgLevelSLOC() {
		assertAll(() -> {
			PackageInfo packageInfo = EntityUtil.getPackageByQualifiedName(projectInfo.getPackageList(),
			                                                               "com.simulation.core");
			int[] slocArray = packageInfo.getSlocArrayForCurrentPkg();
			assertEquals(72, slocArray[0]); //SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE
			assertEquals(95, slocArray[1]); //SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE
			assertEquals(37, slocArray[2]); //SLOCType.COMMENT_LINES_FROM_SOURCE_FILE
			assertEquals(108, slocArray[3]); //SLOCType.TOTAL_LINES_FROM_SOURCE_FILE
			assertEquals(67, slocArray[4]); //SLOCType.LOGIC_CODE_LINES_FROM_AST
			assertEquals(89, slocArray[5]); //SLOCType.PHYSICAL_CODE_LINES_FROM_AST
			assertEquals(39, slocArray[6]); //SLOCType.COMMENT_LINES_FROM_AST
			assertEquals(125, slocArray[7]); //SLOCType.TOTAL_LINES_FROM_AST

		}, () -> {
			PackageInfo packageInfo = EntityUtil.getPackageByQualifiedName(projectInfo.getPackageList(),
			                                                               "com.simulation.core");
			int[] slocArray = packageInfo.getSlocArrayForCurrentAndSubPkg();
			assertEquals(237, slocArray[0]); //SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE
			assertEquals(314, slocArray[1]); //SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE
			assertEquals(92, slocArray[2]); //SLOCType.COMMENT_LINES_FROM_SOURCE_FILE
			assertEquals(380, slocArray[3]); //SLOCType.TOTAL_LINES_FROM_SOURCE_FILE
			assertEquals(224, slocArray[4]); //SLOCType.LOGIC_CODE_LINES_FROM_AST
			assertEquals(317, slocArray[5]); //SLOCType.PHYSICAL_CODE_LINES_FROM_AST
			assertEquals(95, slocArray[6]); //SLOCType.COMMENT_LINES_FROM_AST
			assertEquals(409, slocArray[7]); //SLOCType.TOTAL_LINES_FROM_AST
		});
	}

	@Test
	@DisplayName("Testing the Lines of Code in module level")
	void testModuleLevelSLOC() {
		ModuleInfo moduleInfo = EntityUtil.getModuleByShortName(projectInfo.getModuleList(), "JSimulation");
		int[] slocArray = moduleInfo.getSlocArray();
		assertEquals(237, slocArray[0]); //SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE
		assertEquals(314, slocArray[1]); //SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE
		assertEquals(92, slocArray[2]); //SLOCType.COMMENT_LINES_FROM_SOURCE_FILE
		assertEquals(380, slocArray[3]); //SLOCType.TOTAL_LINES_FROM_SOURCE_FILE
		assertEquals(224, slocArray[4]); //SLOCType.LOGIC_CODE_LINES_FROM_AST
		assertEquals(317, slocArray[5]); //SLOCType.PHYSICAL_CODE_LINES_FROM_AST
		assertEquals(95, slocArray[6]); //SLOCType.COMMENT_LINES_FROM_AST
		assertEquals(409, slocArray[7]); //SLOCType.TOTAL_LINES_FROM_AST
	}

	@Test
	@DisplayName("Testing the number of classes and nested class in Module level")
	void testNumberOfClassAndInnerClass() {
		ModuleInfo moduleInfo = EntityUtil.getModuleByShortName(projectInfo.getModuleList(), "JSimulation");
		assertEquals(20, moduleInfo.getNumberOfJavaFile());
		assertEquals(30, moduleInfo.getNumberOfAllClass());
	}


}