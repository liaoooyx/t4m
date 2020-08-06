package com.t4m.extractor.scanner;

import com.t4m.conf.GlobalProperties;
import com.t4m.extractor.T4MExtractor;
import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.EntityUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class No6_JavaParserScannerTest {

	static ProjectInfo projectInfo;

	@BeforeAll
	public static void initProjectInfo() {
		String path = new File("src/test/resources/JSimulation").getAbsolutePath();
		projectInfo = new ProjectInfo(path, "/build;/out;/output;", GlobalProperties.DEFAULT_DEPENDENCY_PATH);
		T4MExtractor t4MExtractor = new T4MExtractor(projectInfo);
		t4MExtractor.scanJavaParser();
	}

	@Test
	@DisplayName("测试ClassInfoVisitor是否初始化非公共类和嵌套类")
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
	@DisplayName("测试DeclarationVisitor是否初始化MethodInfo和FieldInfo")
	void testMethodAndFieldInfo() {
		ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                         "com.simulation.core.foo.ComplexClassA");
		assertAll(() -> assertEquals(11, classInfo.getMethodInfoList().size()),
		          () -> assertEquals(13, classInfo.getFieldInfoList().size()));

	}


	@Test
	@DisplayName("测试DeclarationVisitor是否添加依赖关系")
	void testDependency() {
		ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                         "com.simulation.core.xoo.Java8Tester");
		assertAll(() -> assertEquals(2, classInfo.getActiveDependencyAkaFanOutList().size()),
		          () -> assertEquals(0, classInfo.getPassiveDependencyAkaFanInList().size()));
	}

	@Test
	@DisplayName("测试DeclarationVisitor是否扫描出SLOC的元数据")
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
	@DisplayName("测试DeclarationVisitor是扫描出方法复杂度的元数据")
	void testCyclomaticComplexity() {
		ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                         "com.simulation.core.CyclomaticComplexityClass");
		assertAll(() -> assertEquals(17, classInfo.getCyclomaticComplexityList().get(0)),
		          () -> assertEquals(1, classInfo.getCyclomaticComplexityList().get(1)));
	}
}