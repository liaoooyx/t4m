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

class AfterScan {

	static ProjectInfo projectInfo;

	@BeforeAll
	public static void initProjectInfo() {
		String path = new File("src/test/resources/JSimulation").getAbsolutePath();
		projectInfo = new ProjectInfo(path);
		T4MExtractor t4MExtractor = new T4MExtractor(projectInfo);
		t4MExtractor.scanASTParser();
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
		PackageInfo packageInfo = EntityUtil.getPackageByQualifiedName(projectInfo.getPackageList(), "com.simulation.core.foo");
		int[] slocArray = packageInfo.getSumOfSLOCForCurrentPkg();
		assertEquals(48,slocArray[0]); //SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE
		assertEquals(68,slocArray[1]); //SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE
		assertEquals(32,slocArray[2]); //SLOCType.ALL_COMMENT_LINES_FROM_SOURCE_FILE
		assertEquals(45,slocArray[3]); //SLOCType.LOGIC_CODE_LINES_FROM_AST
		assertEquals(65,slocArray[4]); //SLOCType.PHYSICAL_CODE_LINES_FROM_AST
		assertEquals(27,slocArray[5]); //SLOCType.DOC_COMMENT_LINES_FROM_AST
	}

	@Test
	@DisplayName("测试模块的总SLOC")
	void testSumSLOCforModule(){
		ModuleInfo moduleInfo = EntityUtil.getModuleByShortName(projectInfo.getModuleList(), "JSimulation");
		int[] slocArray = moduleInfo.getSumOfSLOC();
		assertEquals(70,slocArray[0]); //SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE
		assertEquals(101,slocArray[1]); //SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE
		assertEquals(50,slocArray[2]); //SLOCType.ALL_COMMENT_LINES_FROM_SOURCE_FILE
		assertEquals(67,slocArray[3]); //SLOCType.LOGIC_CODE_LINES_FROM_AST
		assertEquals(98,slocArray[4]); //SLOCType.PHYSICAL_CODE_LINES_FROM_AST
		assertEquals(45,slocArray[5]); //SLOCType.DOC_COMMENT_LINES_FROM_AST
	}

}