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

import static org.junit.jupiter.api.Assertions.*;

class T4MProjectInfoSerializerTest {

	static ProjectInfo projectInfo;

	@BeforeAll
	public static void initProjectInfo() {
		String path = new File("src/test/resources/JSimulation").getAbsolutePath();
		projectInfo = new ProjectInfo(path, GlobalProperties.DEFAULT_EXCLUDED_PATH,
		                              GlobalProperties.DEFAULT_DEPENDENCY_PATH);
		GlobalProperties.setCurrentProjectIdentifier("JSimulation#" + System.currentTimeMillis());
		T4MExtractor t4MExtractor = new T4MExtractor();
		t4MExtractor.extract(projectInfo);
	}

	@Test
	@DisplayName("Test serialization operation")
	void serialization() {
		T4MSerializer serializer = new T4MProjectInfoSerializer();
		String dbFileName = TimeUtil.formatToLogFileName(projectInfo.getCreateDate());
		String dbPath = GlobalProperties.DB_ROOT_PATH;
		String currentProjectIdentifier = GlobalProperties.getCurrentProjectIdentifier();
		serializer.serializeTo(projectInfo, dbFileName);
		File file = new File(dbPath + File.separator + currentProjectIdentifier + File.separator + dbFileName);
		assertTrue(file.exists());
		ProjectInfo historyProjectInfo = serializer.deserializeFrom(
				TimeUtil.formatToLogFileName(projectInfo.getCreateDate()));
		assertEquals(projectInfo, historyProjectInfo);

		// deserialization
		String path = new File("src/test/resources/JSimulation").getAbsolutePath();
		T4MExtractor t4MExtractor = new T4MExtractor();
		t4MExtractor.extract(new ProjectInfo(path, GlobalProperties.DEFAULT_EXCLUDED_PATH,
		                                     GlobalProperties.DEFAULT_DEPENDENCY_PATH));

		List<ProjectInfo> projectInfoList = serializer.deserializeAll();
		for (int i = 0; i < projectInfoList.size() - 1; i++) {
			ProjectInfo current = projectInfoList.get(i);
			ProjectInfo next = projectInfoList.get(i + 1);
			assertTrue(current.getCreateDate().getTime() < next.getCreateDate().getTime());
		}
		// delete
		serializer.delete(GlobalProperties.getCurrentProjectIdentifier());
		File projectDir = new File(dbPath + File.separator + currentProjectIdentifier);
		assertNull(projectDir.listFiles());
	}
}