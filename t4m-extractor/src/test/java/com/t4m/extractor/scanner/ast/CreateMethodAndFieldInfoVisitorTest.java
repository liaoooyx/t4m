package com.t4m.extractor.scanner.ast;

import com.t4m.extractor.T4MExtractor;
import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.FieldInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.EntityUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class CreateMethodAndFieldInfoVisitorTest {

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
}