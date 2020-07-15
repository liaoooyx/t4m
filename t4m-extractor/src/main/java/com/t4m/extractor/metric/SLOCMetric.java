package com.t4m.extractor.metric;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.t4m.extractor.entity.ClassInfo.*;

/**
 * Created by Yuxiang Liao on 2020-06-19 13:30.
 */
public class SLOCMetric {

	private boolean inBlockComment = false;

	public void slocCounterFromRawFile(String sourceLine, Map<SLOCType, Integer> counterMap) {
		String currentLine = sourceLine.strip();
		int codeLines = counterMap.get(SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE);
		int commentLines = counterMap.get(SLOCType.ALL_COMMENT_LINES_FROM_SOURCE_FILE);
		int physicalLines = counterMap.get(SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE);
		// sloc计数
		if (!"".equals(currentLine)) {
			// 将引号内容删除：".*?"
			currentLine = currentLine.replaceAll("\".*?\"", "foo");
			if (inBlockComment) {
				commentLines++;
				if (currentLine.contains("*/")) {
					inBlockComment = false;
					int index = currentLine.indexOf("*/");
					if (index + 2 == currentLine.length()) {
						// 单独注释行 comment*/
					} else {
						// 混合行
						// super()/*comment
						// comment
						// //comment*/ foo/*comment*/.method();/*comment*/ System.out.println();
						currentLine = currentLine.substring(currentLine.indexOf("*/") + 2).strip();
						if (currentLine.startsWith("//")) {
							// 单独的注释行
						} else if (currentLine.startsWith("/*")) {
							if (currentLine.contains("*/")) {
								// /*comment*/foo./*comment*/method()/*
								// comment*/;
								// /*comment*/});/*comment*/
								// /*comment*/}//comment
								currentLine = currentLine.replaceAll("/\\*.*?\\*/", "").strip();
								if (currentLine.contains("/*")) {
									inBlockComment = true;
									currentLine = currentLine.split("/\\*")[0].strip(); // 【/*】右边的必然是注释语句（因为没有结束符*/），因此只需要取数组的第一个即可
									if (Pattern.compile("^[{}();]*$").matcher(currentLine).matches()) {
										// braces line
										physicalLines++;
									} else {
										codeLines++;
										physicalLines++;
									}
								} else if (currentLine.contains("//")) {
									currentLine = currentLine.split("//")[0].strip(); // 【//】右边的必然是注释语句，因此只需要取数组的第一个即可
									if (Pattern.compile("^[{}();]*$").matcher(currentLine).matches()) {
										// braces line
										physicalLines++;
									} else {
										codeLines++;
										physicalLines++;
									}
								} else {
									// 非注释行
									if (Pattern.compile("^[{}();]*$").matcher(currentLine).matches()) {
										// braces line
										physicalLines++;
									} else {
										codeLines++;
										physicalLines++;
									}
								}
							} else {
								// 多行注释，当前行全是注释
								inBlockComment = true;
							}
						} else if (currentLine.contains("/*")) {
							//	混合行
							// foo/*comment*/.method();/*comment*/
							// }/*comment*/);/*comment*/
							if (currentLine.contains("*/")) {
								currentLine = currentLine.replaceAll("/\\*.*?\\*/", "");
								// Foo foo /*//comment*/ = new Foo(); //comment
								if (currentLine.contains("/*")) {
									inBlockComment = true;
									currentLine = currentLine.split("/\\*")[0].strip(); // 【/*】右边的必然是注释语句（因为没有结束符*/），因此只需要取数组的第一个即可
									if (Pattern.compile("^[{}();]*$").matcher(currentLine).matches()) {
										// braces line
										physicalLines++;
									} else {
										codeLines++;
										physicalLines++;
									}
								} else if (currentLine.contains("//")) {
									currentLine = currentLine.split("//")[0].strip(); // 【//】右边的必然是注释语句，因此只需要取数组的第一个即可
									if (Pattern.compile("^[{}();]*$").matcher(currentLine).matches()) {
										// braces line
										physicalLines++;
									} else {
										codeLines++;
										physicalLines++;
									}
								} else {
									// 非注释行
									if (Pattern.compile("^[{}();]*$").matcher(currentLine).matches()) {
										// braces line
										physicalLines++;
									} else {
										codeLines++;
										physicalLines++;
									}
								}
							} else {
								// callMethod();/*comment
								// }/*comment
								inBlockComment = true;
								currentLine = currentLine.split("/\\*")[0]
										.strip(); // 【/*】右边的必然是注释语句（因为没有结束符*/），因此只需要取数组的第一个即可
								if (Pattern.compile("^[{}();]*$").matcher(currentLine).matches()) {
									// braces line
									physicalLines++;
								} else {
									codeLines++;
									physicalLines++;
								}
							}
						} else if (currentLine.contains("//")) {
							//混合行
							currentLine = currentLine.split("//")[0].strip(); // 【//】右边的必然是注释语句，因此只需要取数组的第一个即可
							if (Pattern.compile("^[{}();]*$").matcher(currentLine).matches()) {
								// braces line
								physicalLines++;
							} else {
								codeLines++;
								physicalLines++;
							}
						} else {
							// 非注释行
							if (Pattern.compile("^[{}();]*$").matcher(currentLine).matches()) {
								// braces line
								physicalLines++;
							} else {

								codeLines++;
								physicalLines++;
							}
						}
					}

				} else {
					//	还处于块注释中
				}
			} else {
				if (currentLine.startsWith("//")) {
					// 单独的注释行
					commentLines++;
				} else if (currentLine.startsWith("/*")) {
					commentLines++;
					if (currentLine.contains("*/")) {
						// /*comment*/foo.method();/*comment*/
						// /*comment*/});/*comment*/
						// /*comment*/}//comment
						currentLine = currentLine.replaceAll("/\\*.*?\\*/", "").strip();
						if (currentLine.contains("//")) {
							currentLine = currentLine.split("//")[0].strip(); // 【//】右边的必然是注释语句，因此只需要取数组的第一个即可
							if (Pattern.compile("^[{}();]*$").matcher(currentLine).matches()) {
								// braces line
								physicalLines++;
							} else {
								codeLines++;
								physicalLines++;
							}
						} else {
							// 非注释行
							if (Pattern.compile("^[{}();]*$").matcher(currentLine).matches()) {
								// braces line
								physicalLines++;
							} else {
								codeLines++;
								physicalLines++;
							}
						}
					} else {
						// 多行注释，当前行全是注释
						inBlockComment = true;
					}
				} else if (currentLine.contains("/*")) {
					//	混合行
					// foo/*comment*/.method();/*comment*/
					// }/*comment*/);/*comment*/
					commentLines++;
					if (currentLine.contains("*/")) {
						currentLine = currentLine.replaceAll("/\\*.*?\\*/", "");
						// Foo foo /*//comment*/ = new Foo(); //comment
						if (currentLine.contains("//")) {
							currentLine = currentLine.split("//")[0].strip(); // 【//】右边的必然是注释语句，因此只需要取数组的第一个即可
							if (Pattern.compile("^[{}();]*$").matcher(currentLine).matches()) {
								// braces line
								physicalLines++;
							} else {
								codeLines++;
								physicalLines++;
							}
						} else {
							// 非注释行
							if (Pattern.compile("^[{}();]*$").matcher(currentLine).matches()) {
								// braces line
								physicalLines++;
							} else {
								codeLines++;
								physicalLines++;
							}
						}
					} else {
						// callMethod();/*comment
						// }/*comment
						inBlockComment = true;
						currentLine = currentLine.split("/\\*")[0].strip(); // 【/*】右边的必然是注释语句（因为没有结束符*/），因此只需要取数组的第一个即可
						if (Pattern.compile("^[{}();]*$").matcher(currentLine).matches()) {
							// braces line
							physicalLines++;
						} else {
							codeLines++;
							physicalLines++;
						}
					}
				} else if (currentLine.contains("//")) {
					//混合行
					commentLines++;
					currentLine = currentLine.split("//")[0].strip(); // 【//】右边的必然是注释语句，因此只需要取数组的第一个即可
					if (Pattern.compile("^[{}();]*$").matcher(currentLine).matches()) {
						// braces line
						physicalLines++;
					} else {
						codeLines++;
						physicalLines++;
					}
				} else {
					// 非注释行
					if (Pattern.compile("^[{}();]*$").matcher(currentLine).matches()) {
						// braces line
						physicalLines++;
					} else {
						codeLines++;
						physicalLines++;
					}
				}
			}

		}
		counterMap.replace(SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE, codeLines);
		counterMap.replace(SLOCType.ALL_COMMENT_LINES_FROM_SOURCE_FILE, commentLines);
		counterMap.replace(SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE, physicalLines);
	}

	public static void slocCounterFromAST(String sourceLine, Map<SLOCType, Integer> counterMap) {
		String currentLine = sourceLine.strip();
		int codeLines = counterMap.get(SLOCType.LOGIC_CODE_LINES_FROM_AST);
		int commentLines = counterMap.get(SLOCType.DOC_COMMENT_LINES_FROM_AST);
		int physicalLines = counterMap.get(SLOCType.PHYSICAL_CODE_LINES_FROM_AST);
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
		counterMap.replace(SLOCType.LOGIC_CODE_LINES_FROM_AST, codeLines);
		counterMap.replace(SLOCType.DOC_COMMENT_LINES_FROM_AST, commentLines);
		counterMap.replace(SLOCType.PHYSICAL_CODE_LINES_FROM_AST, physicalLines);

	}

	/**
	 * 该方法会直接将数值叠加在传入参数{@code slocArray}上 索引与对应的SLOC：0--SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE；1--SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE
	 * 2--SLOCType.ALL_COMMENT_LINES_FROM_SOURCE_FILE；3--SLOCType.LOGIC_CODE_LINES_FROM_AST；
	 * 4--SLOCType.PHYSICAL_CODE_LINES_FROM_AST；5--SLOCType.DOC_COMMENT_LINES_FROM_AST
	 */
	public static void sumSLOC(int[] slocArray, Map<SLOCType, Integer> slocMap) {
		slocArray[0] += slocMap.get(SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE);
		slocArray[1] += slocMap.get(SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE);
		slocArray[2] += slocMap.get(SLOCType.ALL_COMMENT_LINES_FROM_SOURCE_FILE);
		slocArray[3] += slocMap.get(SLOCType.LOGIC_CODE_LINES_FROM_AST);
		slocArray[4] += slocMap.get(SLOCType.PHYSICAL_CODE_LINES_FROM_AST);
		slocArray[5] += slocMap.get(SLOCType.DOC_COMMENT_LINES_FROM_AST);
	}

	/**
	 * @param slocArray 目标数组，数值将会叠加在次数组上
	 * @param inputSLOC 需要将此数组中的值，加到{@code slocArray}上
	 */
	public static void sumSLOC(int[] slocArray, int[] inputSLOC) {
		slocArray[0] += inputSLOC[0];
		slocArray[1] += inputSLOC[1];
		slocArray[2] += inputSLOC[2];
		slocArray[3] += inputSLOC[3];
		slocArray[4] += inputSLOC[4];
		slocArray[5] += inputSLOC[5];
	}

	public static void main(String[] args) {
		Map<SLOCType, Integer> counterMap = new HashMap<>();
		counterMap.put(SLOCType.LOGIC_CODE_LINES_FROM_AST, 0); // 不包括空白行，单独大括号和注释行
		counterMap.put(SLOCType.DOC_COMMENT_LINES_FROM_AST, 0); // 包括这样的注释和代码混合的行
		counterMap.put(SLOCType.PHYSICAL_CODE_LINES_FROM_AST, 0);  // 包括代码行、大括号，不包括单独的注释行
		slocCounterFromAST("});", counterMap);
		counterMap.forEach((key, val) -> {
			System.out.println(key + ": " + val);
		});
	}

}
