package com.t4m.extractor.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileUtilTest {

	@Test
	void readJavaSource() {
		String javaSource ="/**\n" +
				" * This package is defining the entities that used to store and transfer the information of a target project. The\n" +
				" * information is extracted by INFORMATION EXTRACTOR and will be used by METRICS CALCULATOR\n" +
				" */\n" + "package com.t4m.extractor.entity;";
		String path =
				"/Users/liao/myProjects/IdeaProjects/t4m/t4m-extractor/src/main/java/com/t4m/extractor/entity/package-info.java";
		String output = FileUtil.readStringFromJavaSourceFile(path);
		assertEquals(javaSource, output);
	}
}