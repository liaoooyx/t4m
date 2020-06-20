package com.t4m.extractor.scanner;

import com.t4m.extractor.T4MExtractor;
import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ProjectInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class No6_ASPScannerTest {

	static ProjectInfo projectInfo1;
	static ProjectInfo projectInfo2;

	@BeforeAll
	public static void initProjectInfo() {
		projectInfo1 = new ProjectInfo("/Users/liao/myProjects/IdeaProjects/sonarqube");
		projectInfo2 = new ProjectInfo("/Users/liao/myProjects/IdeaProjects/comp5911m/refactor");
		T4MExtractor t4MExtractor = new T4MExtractor(projectInfo2);
		t4MExtractor.scanASP();
	}

	@Test
	@DisplayName("测试SLOC数据")
	void testSLOC() {
		ClassInfo classInfo = projectInfo2.getClassList().get(0);
		assertEquals("com.refactor.refactor3.TestClass", classInfo.getFullyQualifiedName());
		assertAll(() -> {
			Map<ClassInfo.SLOCType, Integer> slocMap = classInfo.getSlocCounterMap();
			assertAll(() -> assertEquals(16, slocMap.get(ClassInfo.SLOCType.CODE_LINES_FROM_SOURCE_FILE)),
			          () -> assertEquals(8, slocMap.get(ClassInfo.SLOCType.COMMENT_LINES_FROM_SOURCE_FILE)),
			          () -> assertEquals(23, slocMap.get(ClassInfo.SLOCType.PHYSICAL_LINES_FROM_SOURCE_FILE)),
			          () -> assertEquals(15, slocMap.get(ClassInfo.SLOCType.CODE_LINES_FROM_AST)),
			          () -> assertEquals(6, slocMap.get(ClassInfo.SLOCType.COMMENT_LINES_FROM_AST)),
			          () -> assertEquals(22, slocMap.get(ClassInfo.SLOCType.PHYSICAL_LINES_FROM_AST)));
		});
		List<ClassInfo> innerClassList = classInfo.getInnerClassList();
		assertEquals(2, innerClassList.size());
		ClassInfo innerClass = innerClassList.get(0);
		assertEquals("com.refactor.refactor3.TestClass$InnerTestClass", innerClass.getFullyQualifiedName());
		assertAll(() -> {
			Map<ClassInfo.SLOCType, Integer> slocMap = innerClass.getSlocCounterMap();
			assertAll(() -> assertEquals(0, slocMap.get(ClassInfo.SLOCType.CODE_LINES_FROM_SOURCE_FILE)),
			          () -> assertEquals(0, slocMap.get(ClassInfo.SLOCType.COMMENT_LINES_FROM_SOURCE_FILE)),
			          () -> assertEquals(0, slocMap.get(ClassInfo.SLOCType.PHYSICAL_LINES_FROM_SOURCE_FILE)),
			          () -> assertEquals(4, slocMap.get(ClassInfo.SLOCType.CODE_LINES_FROM_AST)),
			          () -> assertEquals(3, slocMap.get(ClassInfo.SLOCType.COMMENT_LINES_FROM_AST)),
			          () -> assertEquals(6, slocMap.get(ClassInfo.SLOCType.PHYSICAL_LINES_FROM_AST)));
		});
	}

	@Test
	@DisplayName("测试字段数量和方法数量")
	void testBasicClassInfo() {
		ClassInfo classInfo = projectInfo2.getClassList().get(0);
		assertEquals("com.refactor.refactor3.TestClass", classInfo.getFullyQualifiedName());
		assertAll(() -> assertEquals(2, classInfo.getNumberOfMethods()),
		          () -> assertEquals(1, classInfo.getNumberOfFields()));
		List<ClassInfo> innerClassList = classInfo.getInnerClassList();
		assertEquals(2, innerClassList.size());
		ClassInfo innerClass = innerClassList.get(0);
		assertEquals("com.refactor.refactor3.TestClass$InnerTestClass", innerClass.getFullyQualifiedName());
		assertAll(() -> assertEquals(1, innerClass.getNumberOfMethods()),
		          () -> assertEquals(1, innerClass.getNumberOfFields()));

	}

	@Test
	@DisplayName("测试方法类型")
	void testClassType() {
		assertAll(()->{
			ClassInfo classInfo = projectInfo2.getClassList().get(0);
			assertEquals("com.refactor.refactor3.TestClass", classInfo.getFullyQualifiedName());
			assertEquals(ClassInfo.ClassModifier.CLASS, classInfo.getClassModifier());
		},()->{
			ClassInfo classInfo = projectInfo2.getClassList().get(2);
			assertEquals("com.refactor.refactor3.TestInterface", classInfo.getFullyQualifiedName());
			assertEquals(ClassInfo.ClassModifier.INTERFACE, classInfo.getClassModifier());
		},()->{
			ClassInfo classInfo = projectInfo2.getClassList().get(5);
			assertEquals("com.refactor.refactor3.price.Price", classInfo.getFullyQualifiedName());
			assertEquals(ClassInfo.ClassModifier.ABSTRACT_CLASS, classInfo.getClassModifier());
		});


	}

}