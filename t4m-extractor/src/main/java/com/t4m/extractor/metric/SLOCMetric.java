package com.t4m.extractor.metric;

import com.t4m.extractor.entity.ClassInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.t4m.extractor.entity.ClassInfo.*;

/**
 * Created by Yuxiang Liao on 2020-06-19 13:30.
 */
public class SLOCMetric {

	public static void SLOCCounterFromRawFile(String sourceLine, Map<SLOCType, Integer> counterMap) {
		String currentLine = sourceLine.strip();
		int codeLines = counterMap.get(SLOCType.CODE_LINES_FROM_SOURCE_FILE);
		int commentLines = counterMap.get(SLOCType.COMMENT_LINES_FROM_SOURCE_FILE);
		int physicalLines = counterMap.get(SLOCType.PHYSICAL_LINES_FROM_SOURCE_FILE);
		// sloc计数
		if (!"".equals(currentLine)) {
			if (currentLine.startsWith("//") || currentLine.startsWith("/**") || currentLine.startsWith("*")) {
				// Comment line
				commentLines++;
			} else if (Pattern.compile(";[ ]*//.*").matcher(currentLine).find()) {
				// Mix comment line
				commentLines++;
				codeLines++;
				physicalLines++;
			} else if (Pattern.compile("^[{}();]*$").matcher(currentLine).matches()) {
				// braces line
				physicalLines++;
			} else {
				codeLines++;
				physicalLines++;
			}
		}
		counterMap.replace(SLOCType.CODE_LINES_FROM_SOURCE_FILE, codeLines);
		counterMap.replace(SLOCType.COMMENT_LINES_FROM_SOURCE_FILE, commentLines);
		counterMap.replace(SLOCType.PHYSICAL_LINES_FROM_SOURCE_FILE, physicalLines);
	}

	public static void SLOCCounterFromAST(String sourceLine, Map<SLOCType, Integer> counterMap) {
		String currentLine = sourceLine.strip();
		int codeLines = counterMap.get(SLOCType.CODE_LINES_FROM_AST);
		int commentLines = counterMap.get(SLOCType.COMMENT_LINES_FROM_AST);
		int physicalLines = counterMap.get(SLOCType.PHYSICAL_LINES_FROM_AST);
		// sloc计数
		if (!"".equals(currentLine)) {
			if (currentLine.startsWith("//") || currentLine.startsWith("/**") || currentLine.startsWith("*")) {
				// Comment line
				commentLines++;
			} else if (Pattern.compile(";[ ]*//.*").matcher(currentLine).find()) {
				// Mix comment line
				commentLines++;
				codeLines++;
				physicalLines++;
			} else if (Pattern.compile("^[{}();]*$").matcher(currentLine).matches()) {
				// braces line
				physicalLines++;
			} else {
				codeLines++;
				physicalLines++;
			}
		}
		counterMap.replace(SLOCType.CODE_LINES_FROM_AST, codeLines);
		counterMap.replace(SLOCType.COMMENT_LINES_FROM_AST, commentLines);
		counterMap.replace(SLOCType.PHYSICAL_LINES_FROM_AST, physicalLines);

	}

	public static void main(String[] args) {
		Map<SLOCType, Integer> counterMap = new HashMap<>();
		counterMap.put(SLOCType.CODE_LINES_FROM_AST, 0); // 不包括空白行，单独大括号和注释行
		counterMap.put(SLOCType.COMMENT_LINES_FROM_AST, 0); // 包括这样的注释和代码混合的行
		counterMap.put(SLOCType.PHYSICAL_LINES_FROM_AST, 0);  // 包括代码行、大括号，不包括单独的注释行
		SLOCCounterFromAST("});", counterMap);
		counterMap.forEach((key, val) -> {
			System.out.println(key + ": " + val);
		});
	}
}
