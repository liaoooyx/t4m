package com.t4m.extractor.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PropertyUtilTest {

	@Test
	void testGetProperty(){
		assertEquals("/build",PropertyUtil.getProperty("EXCLUDED_PATH"));
	}

}