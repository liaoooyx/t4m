package com.t4m.conf;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalPropertiesTest {
	@Test
	void SLOCCounter() {
		assertEquals(System.getenv("T4M_HOME") + File.separator + "db", GlobalProperties.DB_ROOT_PATH);
		assertEquals("/build;/out;/output;/src/main/resource;/src/test", GlobalProperties.DEFAULT_EXCLUDED_PATH);
		assertEquals("", GlobalProperties.DEFAULT_DEPENDENCY_PATH);
	}

}