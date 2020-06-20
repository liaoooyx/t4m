package com.t4m.extractor.metric;

import com.t4m.extractor.entity.ClassInfo;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SLOCMetricTest {

	@Test
	void SLOCCounter() {
		Map<ClassInfo.SLOCType, Integer> counterMap = new HashMap<>();
		counterMap.put(ClassInfo.SLOCType.CODE_LINES_FROM_AST, 0); // 不包括空白行，单独大括号和注释行
		counterMap.put(ClassInfo.SLOCType.COMMENT_LINES_FROM_AST, 0); // 包括这样的注释和代码混合的行
		counterMap.put(ClassInfo.SLOCType.PHYSICAL_LINES_FROM_AST, 0);  // 包括代码行、大括号，不包括单独的注释行
		SLOCMetric.SLOCCounterFromAST("counterMap.put(\"codeLines\", 0); // 不包括空白行，单独大括号和注释行", counterMap);
		counterMap.forEach((key, val) -> {
			// System.out.println(key + ": " + val);
			assertAll(() -> assertEquals(1, counterMap.get(ClassInfo.SLOCType.CODE_LINES_FROM_AST)),
			          () -> assertEquals(1, counterMap.get(ClassInfo.SLOCType.COMMENT_LINES_FROM_AST)),
			          () -> assertEquals(1, counterMap.get(ClassInfo.SLOCType.PHYSICAL_LINES_FROM_AST)));
		});

	}
}