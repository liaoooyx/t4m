package com.t4m.extractor.processor;

import com.t4m.conf.GlobalProperties;
import com.t4m.extractor.T4MExtractor;
import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.EntityUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SourceCodeResolverTest {

	static ProjectInfo projectInfo;

	@BeforeAll
	public static void initProjectInfo() {
		String path = new File("src/test/resources/JSimulation").getAbsolutePath();
		projectInfo = new ProjectInfo(path, "/build;/out;/output;", GlobalProperties.DEFAULT_DEPENDENCY_PATH);
		T4MExtractor t4MExtractor = new T4MExtractor();
		t4MExtractor.setCustomScannerChain(new DirectoryFileScanner(), new ClassScanner(), new PackageScanner(),
		                                   new ModuleScanner(), new BelongingnessScanner(), new SourceCodeResolver())
		            .extract(projectInfo);
	}

	@Test
	@DisplayName("Testing whether ClassInfoVisitor initializes package private outer classes and nested classes")
	void testExtraClassAndNestedClass() {
		assertAll(() -> assertEquals(21, projectInfo.getClassList().size()),
		          () -> assertEquals(9, projectInfo.getNestedClassList().size()),
		          () -> assertEquals(1, projectInfo.getExtraClassList().size()));
		ClassInfo extraClass = EntityUtil.getClassByQualifiedName(projectInfo.getExtraClassList(),
		                                                          "com.simulation.core.foo.ExtraClass");
		assertNotNull(extraClass);
		assertEquals(2, extraClass.getNestedClassList().size());
	}

	@Test
	@DisplayName("Testing whether the DeclarationVisitor initializes MethodInfo and FieldInfo")
	void testMethodAndFieldInfo() {
		ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                         "com.simulation.core.foo.ComplexClassA");
		assertAll(() -> assertEquals(11, classInfo.getMethodInfoList().size()),
		          () -> assertEquals(13, classInfo.getFieldInfoList().size()));

	}


	@Test
	@DisplayName("Testing whether the DeclarationVisitor adds dependencies")
	void testDependency() {
		ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                         "com.simulation.core.xoo.Java8Tester");
		assertAll(() -> assertEquals(2, classInfo.getActiveDependencyAkaFanOutList().size()),
		          () -> assertEquals(0, classInfo.getPassiveDependencyAkaFanInList().size()));
	}

	@Test
	@DisplayName("Testing whether the DeclarationVisitor scans the metadata of SLOC")
	void testSLOCMap() {
		ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                         "com.simulation.core.SlocClass");
		Map<ClassInfo.SLOCType, Integer> counterMap = classInfo.getSlocCounterMap();
		assertAll(() -> assertEquals(10, counterMap.get(ClassInfo.SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE)),
		          () -> assertEquals(12, counterMap.get(ClassInfo.SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE)),
		          () -> assertEquals(16, counterMap.get(ClassInfo.SLOCType.COMMENT_LINES_FROM_SOURCE_FILE)),
		          () -> assertEquals(8, counterMap.get(ClassInfo.SLOCType.LOGIC_CODE_LINES_FROM_AST)),
		          () -> assertEquals(10, counterMap.get(ClassInfo.SLOCType.PHYSICAL_CODE_LINES_FROM_AST)),
		          () -> assertEquals(19, counterMap.get(ClassInfo.SLOCType.COMMENT_LINES_FROM_AST)));
	}


	@Test
	@DisplayName("Testing whether the DeclarationVisitor scans the metadata of the method complexity")
	void testCyclomaticComplexity() {
		ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                         "com.simulation.core.CyclomaticComplexityClass");
		assertAll(() -> assertEquals(17, classInfo.getCyclomaticComplexityList().get(0)),
		          () -> assertEquals(1, classInfo.getCyclomaticComplexityList().get(1)));
	}
}