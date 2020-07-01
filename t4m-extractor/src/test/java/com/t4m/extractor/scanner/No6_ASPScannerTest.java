package com.t4m.extractor.scanner;

import com.t4m.extractor.T4MExtractor;
import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.exception.DuplicatedInnerClassFoundedException;
import com.t4m.extractor.util.EntityUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class No6_ASPScannerTest {

	static ProjectInfo projectInfo;

	@BeforeAll
	public static void initProjectInfo() {
		String path = new File("src/test/resources/JSimulation").getAbsolutePath();
		projectInfo = new ProjectInfo(path);
		T4MExtractor t4MExtractor = new T4MExtractor(projectInfo);
		t4MExtractor.scanASP();
	}

	@Test
	@DisplayName("测试SLOC数据")
	void testSLOC() {
		ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                         "com.simulation.core.foo.ComplexClassA");
		assertNotNull(classInfo);
		assertAll(() -> {
			Map<ClassInfo.SLOCType, Integer> slocMap = classInfo.getSlocCounterMap();
			assertAll(() -> assertEquals(31, slocMap.get(ClassInfo.SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE)),
			          () -> assertEquals(17, slocMap.get(ClassInfo.SLOCType.ALL_COMMENT_LINES_FROM_SOURCE_FILE)),
			          () -> assertEquals(41, slocMap.get(ClassInfo.SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE)),
			          () -> assertEquals(28, slocMap.get(ClassInfo.SLOCType.LOGIC_CODE_LINES_FROM_AST)),
			          () -> assertEquals(15, slocMap.get(ClassInfo.SLOCType.DOC_COMMENT_LINES_FROM_AST)),
			          () -> assertEquals(38, slocMap.get(ClassInfo.SLOCType.PHYSICAL_CODE_LINES_FROM_AST)));
		});

		ClassInfo innerClass = EntityUtil.getClassByShortName(classInfo.getInnerClassList(),
		                                                      "ComplexClassA$InnnerClassOfComplexClassA");
		assertNotNull(innerClass);
		assertAll(() -> {
			Map<ClassInfo.SLOCType, Integer> slocMap = innerClass.getSlocCounterMap();
			assertAll(() -> assertEquals(0, slocMap.get(ClassInfo.SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE)),
			          () -> assertEquals(0, slocMap.get(ClassInfo.SLOCType.ALL_COMMENT_LINES_FROM_SOURCE_FILE)),
			          () -> assertEquals(0, slocMap.get(ClassInfo.SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE)),
			          () -> assertEquals(7, slocMap.get(ClassInfo.SLOCType.LOGIC_CODE_LINES_FROM_AST)),
			          () -> assertEquals(0, slocMap.get(ClassInfo.SLOCType.DOC_COMMENT_LINES_FROM_AST)),
			          () -> assertEquals(10, slocMap.get(ClassInfo.SLOCType.PHYSICAL_CODE_LINES_FROM_AST)));
		});
	}

	@Test
	@DisplayName("测试字段数量和方法数量")
	void testBasicClassInfo() {
		ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                         "com.simulation.core.foo.ComplexClassA");
		assertNotNull(classInfo);
		assertAll(() -> assertEquals(6, classInfo.getNumberOfMethods()),
		          () -> assertEquals(2, classInfo.getNumberOfFields()));
		List<ClassInfo> innerClassList = classInfo.getInnerClassList();
		assertEquals(1, innerClassList.size());
		try {
			ClassInfo innerClass = EntityUtil.getClassByShortName(innerClassList,
			                                                      "ComplexClassA$InnnerClassOfComplexClassA");
			assertEquals("com.simulation.core.foo.ComplexClassA$InnnerClassOfComplexClassA",
			             innerClass.getFullyQualifiedName());
			assertAll(() -> assertEquals(2, innerClass.getNumberOfMethods()),
			          () -> assertEquals(3, innerClass.getNumberOfFields()));
		} catch (DuplicatedInnerClassFoundedException e) {
			e.printStackTrace();
		}
	}

	@Test
	@DisplayName("测试方法类型")
	void testClassType() {
		assertAll(() -> {
			ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
			                                                         "com.simulation.core.bar.SimpleAbstractClass");
			assertNotNull(classInfo);
			assertEquals(ClassInfo.ClassModifier.ABSTRACT_CLASS, classInfo.getClassModifier());
		}, () -> {
			ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
			                                                         "com.simulation.core.bar.SimpleInterfaceA");
			assertNotNull(classInfo);
			assertEquals(ClassInfo.ClassModifier.INTERFACE, classInfo.getClassModifier());
		}, () -> {
			ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
			                                                         "com.simulation.core.bar.SimpleClassA");
			assertNotNull(classInfo);
			assertEquals(ClassInfo.ClassModifier.CLASS, classInfo.getClassModifier());
		});

	}

	@Test
	@DisplayName("测试类依赖")
	void testClassDependency() {
		ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                         "com.simulation.core.foo.ComplexClassA");
		assertNotNull(classInfo);
		List<ClassInfo> activeDependencyList = classInfo.getActiveDependencyList();
		List<ClassInfo> passiveDependencyList = classInfo.getPassiveDependencyList();
		assertEquals(0, passiveDependencyList.size());
		assertEquals(9, activeDependencyList.size());
		assertAll(() -> {
			ClassInfo referToClass = EntityUtil.getClassByQualifiedName(activeDependencyList,
			                                                            "com.simulation.core.foo.ComplexAbstractClass");
			assertNotNull(referToClass);
		});
		assertAll(() -> {
			ClassInfo referToClass = EntityUtil.getClassByQualifiedName(activeDependencyList,
			                                                            "com.simulation.core.bar.SimpleClassA");
			assertNotNull(referToClass);
		});
		assertAll(() -> {
			ClassInfo referToClass = EntityUtil.getClassByQualifiedName(activeDependencyList,
			                                                            "com.simulation.core.bar.SimpleInterfaceA");
			assertNotNull(referToClass);
		});
		assertAll(() -> {
			ClassInfo referToClass = EntityUtil.getClassByQualifiedName(activeDependencyList,
			                                                            "com.simulation.core.bar.SimpleClassB");
			assertNotNull(referToClass);
		});
		assertAll(() -> {
			ClassInfo referToClass = EntityUtil.getClassByQualifiedName(activeDependencyList,
			                                                            "com.simulation.core.bar.SimpleInterfaceB");
			assertNull(referToClass);
		});
		assertAll(() -> {
			ClassInfo referToClass = EntityUtil.getClassByQualifiedName(activeDependencyList,
			                                                            "com.simulation.core.bar.SimpleClassC");
			assertNotNull(referToClass);
		});
		assertAll(() -> {
			ClassInfo referToClass = EntityUtil.getClassByQualifiedName(activeDependencyList,
			                                                            "com.simulation.core.xoo.XooClassA");
			assertNull(referToClass);
		});
		assertAll(() -> {
			ClassInfo referToClass = EntityUtil.getClassByQualifiedName(activeDependencyList,
			                                                            "com.simulation.core.foo.ComplexClassB");
			assertNotNull(referToClass);
		});
		assertAll(() -> {
			ClassInfo referToClass = EntityUtil.getClassByQualifiedName(activeDependencyList,
			                                                            "com.simulation.core.foo.ComplexClassB$InnerClassOfB");
			assertNotNull(referToClass);
		});
		assertAll(() -> {
			ClassInfo referToClass = EntityUtil.getClassByQualifiedName(activeDependencyList,
			                                                            "com.simulation.core.foo.ComplexClassC");
			assertNotNull(referToClass);
		});
		assertAll(() -> {
			ClassInfo referToClass = EntityUtil.getClassByQualifiedName(activeDependencyList,
			                                                            "com.simulation.core.foo.ComplexClassC$InnerClassOfC");
			assertNull(referToClass);
		});
		assertAll(() -> {
			ClassInfo referToClass = EntityUtil.getClassByQualifiedName(activeDependencyList,
			                                                            "com.simulation.core.foo.ComplexClassD");
			assertNull(referToClass);
		});
		assertAll(() -> {
			ClassInfo referToClass = EntityUtil.getClassByQualifiedName(activeDependencyList,
			                                                            "com.simulation.core.foo.ComplexClassD$InnerClassOfD");
			assertNotNull(referToClass);
		});
	}

	@Test
	@DisplayName("测试父类")
	void testSuperClass() {
		ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                         "com.simulation.core.bar.SimpleClassC");
		ClassInfo superClass = classInfo.getSupperClass();
		ClassInfo classInfo_superClass = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                                    "com.simulation.core.bar.SimpleAbstractClass");
		assertEquals(superClass, classInfo_superClass);
	}

	@Test
	@DisplayName("测试接口")
	void testInterface() {
		ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                         "com.simulation.core.bar.SimpleClassC");
		List<ClassInfo> inferfaceList = classInfo.getInterfaceList();
		assertEquals(2, inferfaceList.size());
		ClassInfo classInfo_interfaceA = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                                    "com.simulation.core.bar.SimpleInterfaceA");
		ClassInfo classInfo_interfaceC = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                                    "com.simulation.core.bar.SimpleInterfaceC");
		assertTrue(inferfaceList.contains(classInfo_interfaceA));
		assertTrue(inferfaceList.contains(classInfo_interfaceC));
	}

	@Test
	@DisplayName("测试ModuleInfo下的类数量和内部类数量")
	void testNumberOfClassAndInnerClass() {
		ModuleInfo moduleInfo = EntityUtil.getModuleByShortName(projectInfo.getModuleList(), "JSimulation");
		assertEquals(14,moduleInfo.getNumberOfClasses());
		assertEquals(4,moduleInfo.getNumberOfInnerClasses());
	}

	@Test
	@DisplayName("测试外部类的SLOC")
	void testSumSLOCforClass(){
		ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                         "com.simulation.core.foo.ComplexClassA");
		int[] slocArray = classInfo.getSumOfSLOC();
		assertEquals(32,slocArray[0]); //SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE
		assertEquals(42,slocArray[1]); //SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE
		assertEquals(20,slocArray[2]); //SLOCType.ALL_COMMENT_LINES_FROM_SOURCE_FILE
		assertEquals(29,slocArray[3]); //SLOCType.LOGIC_CODE_LINES_FROM_AST
		assertEquals(39,slocArray[4]); //SLOCType.PHYSICAL_CODE_LINES_FROM_AST
		assertEquals(15,slocArray[5]); //SLOCType.DOC_COMMENT_LINES_FROM_AST
	}

	@Test
	@DisplayName("测试包的总SLOC")
	void testSumSLOCforPackage(){

	}

}