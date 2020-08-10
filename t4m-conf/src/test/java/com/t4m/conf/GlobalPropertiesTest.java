package com.t4m.conf;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalPropertiesTest {
	@Test
	void TestingTheDefaultConfPath() {
		assertEquals(GlobalProperties.DB_ROOT_PATH, System.getenv("T4M_HOME") + File.separator + "db");
		assertEquals(GlobalProperties.DEFAULT_EXCLUDED_PATH,
		             File.separator + ".;" + File.separator + "src" + File.separator + "main" + File.separator +
				             "resource;" + File.separator + "src" + File.separator + "test;" + File.separator +
				             "build;" + File.separator + "out;" + File.separator + "output;" + File.separator +
				             "dist;" + File.separator + "target;" + File.separator + "nbbuild;" + File.separator +
				             "nbdist");
		assertEquals("", GlobalProperties.DEFAULT_DEPENDENCY_PATH);
	}

}