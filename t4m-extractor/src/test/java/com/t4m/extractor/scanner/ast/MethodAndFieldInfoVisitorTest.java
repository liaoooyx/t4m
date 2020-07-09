package com.t4m.extractor.scanner.ast;

import com.t4m.extractor.T4MExtractor;
import com.t4m.extractor.entity.*;
import com.t4m.extractor.util.EntityUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class MethodAndFieldInfoVisitorTest {

	static ProjectInfo projectInfo;

	@BeforeAll
	public static void initProjectInfo() {
		String path = new File("src/test/resources/JSimulation").getAbsolutePath();
		projectInfo = new ProjectInfo(path);
		T4MExtractor t4MExtractor = new T4MExtractor(projectInfo);
		t4MExtractor.scanASP();
	}

	@Test
	@DisplayName("测试构造FieldInfo")
	void testToCreateFieldInfo() {
		ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                         "com.simulation.core.foo.ComplexClassA");
		List<FieldInfo> fieldInfoList = classInfo.getFieldInfoList();
		assertEquals(10, fieldInfoList.size());
		FieldInfo f1 = EntityUtil.getFieldByShortName(fieldInfoList, "simpleInterfaceA");
		ClassInfo target1 = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                       "com.simulation.core.bar.SimpleInterfaceA");
		assertEquals(target1, f1.getTypeAsClassInfoList().get(0));
		FieldInfo f2 = EntityUtil.getFieldByShortName(fieldInfoList, "map");
		ClassInfo target2 = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                       "com.simulation.core.bar.SimpleClassC");
		assertEquals(target2, f2.getTypeAsClassInfoList().get(0));
		FieldInfo f3 = EntityUtil.getFieldByShortName(fieldInfoList, "a");
		assertEquals(0, f3.getTypeAsClassInfoList().size());
		assertEquals("int", f3.getTypeString());
		FieldInfo f4 = EntityUtil.getFieldByShortName(fieldInfoList, "cB");
		ClassInfo target4 = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(),
		                                                       "com.simulation.core.foo.ComplexClassB$InnerClassOfB");
		assertEquals(target4, f4.getTypeAsClassInfoList().get(0));
	}

	@Test
	@DisplayName("测试构造MethodInfo")
	void testToCreateMethodInfo() {
		ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                         "com.simulation.core.foo.ComplexClassA");

		List<MethodInfo> m1List = EntityUtil.getMethodByShortName(classInfo.getMethodInfoList(), "ComplexClassA");
		assertEquals("", m1List.get(0).getReturnTypeString());
		assertEquals(0, m1List.get(0).getReturnTypeAsClassInfoList().size());

		List<MethodInfo> m2List = EntityUtil.getMethodByShortName(classInfo.getMethodInfoList(),
		                                                          "referSimpleClassCInParams");
		ClassInfo c2r = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                   "com.simulation.core.xoo.XooClassA");
		ClassInfo c2p = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                   "com.simulation.core.bar.SimpleClassC");
		assertEquals("XooClassA", m2List.get(0).getReturnTypeString());
		assertEquals(c2r, m2List.get(0).getReturnTypeAsClassInfoList().get(0));
		assertEquals(c2p, m2List.get(0).getParamsTypeAsClassInfoListMap().get("simpleClassC").get(0));

		List<MethodInfo> m3List = EntityUtil.getMethodByShortName(classInfo.getMethodInfoList(), "callList");
		ClassInfo c3r = EntityUtil.getClassByQualifiedName(projectInfo.getClassList(),
		                                                   "com.simulation.core.bar.SimpleClassA");
		Map<String, String> paramsMap3 = m3List.get(0).getParamsNameTypeMap();
		assertEquals("List<SimpleClassA>", m3List.get(0).getReturnTypeString());
		assertEquals(c3r, m3List.get(0).getReturnTypeAsClassInfoList().get(0));
		assertTrue(paramsMap3.containsKey("listA"));
		assertTrue(paramsMap3.containsKey("mapC"));

		List<MethodInfo> m4List = EntityUtil.getMethodByShortName(classInfo.getMethodInfoList(), "multiParams");
		ClassInfo c4p1 = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(),
		                                                    "com.simulation.core.foo.ComplexClassA$InnnerClassOfComplexClassA");
		ClassInfo c4p2 = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(),
		                                                    "com.simulation.core.foo.ComplexClassC$InnerClassOfC");
		List<ClassInfo> paramsList4  = m4List.get(0).getParamsTypeAsClassInfoList();
		assertTrue(paramsList4.contains(c4p1));
		assertTrue(paramsList4.contains(c4p2));
	}
}