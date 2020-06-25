package com.t4m.serializer;

import com.t4m.extractor.T4MExtractor;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.PropertyUtil;
import com.t4m.extractor.util.TimeUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class T4MProjectInfoSerializerTest {

	static ProjectInfo projectInfo;

	@BeforeAll
	public static void initProjectInfo() {
		String path = new File("src/test/resources/JSimulation").getAbsolutePath();
		projectInfo = new ProjectInfo(path);
		T4MExtractor t4MExtractor = new T4MExtractor(projectInfo);
		t4MExtractor.scanASP();
	}

	@Test
	void serialization() {
		T4MSerializer serializer = new T4MProjectInfoSerializer();
		String dbFileName = TimeUtil.formatToLogFileName(projectInfo.getCreateDate());
		String dbPath = PropertyUtil.getProperty("OBJECT_DB_PATH");
		serializer.serializeTo(projectInfo, dbFileName);
		File file = new File(dbPath + File.separator + dbFileName);
		assertTrue(file.exists());
		ProjectInfo historyProjectInfo = serializer.deserializeFrom(
				TimeUtil.formatToLogFileName(projectInfo.getCreateDate()));
		assertEquals(projectInfo,historyProjectInfo);
	}

}