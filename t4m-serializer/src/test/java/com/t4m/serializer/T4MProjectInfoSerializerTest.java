package com.t4m.serializer;


import com.t4m.conf.GlobalProperties;
import com.t4m.extractor.T4MExtractor;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.TimeUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class T4MProjectInfoSerializerTest {

	static ProjectInfo projectInfo;

	@BeforeAll
	public static void initProjectInfo() {
		String path = new File("src/test/resources/JSimulation").getAbsolutePath();
		projectInfo = new ProjectInfo(path, GlobalProperties.DEFAULT_EXCLUDED_PATH,GlobalProperties.DEFAULT_DEPENDENCY_PATH);
		T4MExtractor t4MExtractor = new T4MExtractor();
		t4MExtractor.extract(projectInfo);
	}

	@Test
	@DisplayName("测试序列化操作")
	void serialization() {
		T4MSerializer serializer = new T4MProjectInfoSerializer();
		String dbFileName = TimeUtil.formatToLogFileName(projectInfo.getCreateDate());
		String dbPath = GlobalProperties.DB_ROOT_PATH;
		String currentProjectName = GlobalProperties.CURRENT_PROJECT_IDENTIFIER;
		serializer.serializeTo(projectInfo, dbFileName);
		File file = new File(dbPath + File.separator + currentProjectName + File.separator + dbFileName);
		assertTrue(file.exists());
		ProjectInfo historyProjectInfo = serializer.deserializeFrom(
				TimeUtil.formatToLogFileName(projectInfo.getCreateDate()));
		assertEquals(projectInfo, historyProjectInfo);
	}

	@Test
	@DisplayName("反序列化所有历史记录，并排序")
	void serializeAll() {
		T4MSerializer serializer = new T4MProjectInfoSerializer();
		List<ProjectInfo> projectInfoList = serializer.deserializeAll();
		for (int i = 0; i < projectInfoList.size() - 1; i++) {
			ProjectInfo current = projectInfoList.get(i);
			ProjectInfo next = projectInfoList.get(i + 1);
			assertTrue(current.getCreateDate().getTime() < next.getCreateDate().getTime());
		}

	}

}