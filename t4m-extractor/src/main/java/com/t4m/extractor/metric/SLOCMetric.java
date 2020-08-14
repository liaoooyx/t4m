package com.t4m.extractor.metric;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.PackageInfo;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

import static com.t4m.extractor.entity.ClassInfo.SLOCType;

/**
 * Created by Yuxiang Liao on 2020-06-19 13:30.
 */
public class SLOCMetric implements ClassLevelMetric, PackageLevelMetric, ModuleLevelMetric {

	@Override
	public void calculate(ClassInfo classInfo) {
		int[] slocArray = new int[6];
		Arrays.fill(slocArray, 0);
		sumSLOC(slocArray, classInfo.getSlocCounterMap());
		classInfo.setSlocArray(slocArray);
	}

	@Override
	public void calculate(PackageInfo packageInfo) {
		int[] slocArray = sumSLOCForCurrentPkg(packageInfo);
		packageInfo.setSlocArrayForCurrentPkg(slocArray);
		int[] slocArrayPlus = sumSLOCForCurrentAndSubPkg(packageInfo);
		packageInfo.setSlocArrayForCurrentAndSubPkg(slocArrayPlus);
	}

	@Override
	public void calculate(ModuleInfo moduleInfo) {
		int[] slocArray = new int[6];
		Arrays.fill(slocArray, 0);
		for (PackageInfo packageInfo : moduleInfo.getPackageList()) {
			sumSLOC(slocArray, packageInfo.getSlocArrayForCurrentPkg());
		}
		moduleInfo.setSlocArray(slocArray);
	}

	private void sumSLOC(int[] slocArray, Map<SLOCType, Integer> slocMap) {
		slocArray[0] += slocMap.get(SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE);
		slocArray[1] += slocMap.get(SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE);
		slocArray[2] += slocMap.get(SLOCType.COMMENT_LINES_FROM_SOURCE_FILE);
		slocArray[3] += slocMap.get(SLOCType.LOGIC_CODE_LINES_FROM_AST);
		slocArray[4] += slocMap.get(SLOCType.PHYSICAL_CODE_LINES_FROM_AST);
		slocArray[5] += slocMap.get(SLOCType.COMMENT_LINES_FROM_AST);
	}

	private void sumSLOC(int[] slocArray, int[] inputSLOC) {
		slocArray[0] += inputSLOC[0];
		slocArray[1] += inputSLOC[1];
		slocArray[2] += inputSLOC[2];
		slocArray[3] += inputSLOC[3];
		slocArray[4] += inputSLOC[4];
		slocArray[5] += inputSLOC[5];
	}

	private int[] sumSLOCForCurrentPkg(PackageInfo packageInfo) {
		int[] slocArray = new int[6];
		Arrays.fill(slocArray, 0);
		for (ClassInfo classInfo : packageInfo.getAllClassList()) {
			sumSLOC(slocArray, classInfo.getSlocArray());
		}
		return slocArray;
	}

	private int[] sumSLOCForCurrentAndSubPkg(PackageInfo packageInfo) {
		int[] slocArray = sumSLOCForCurrentPkg(packageInfo);
		if (packageInfo.getSubPackageList().isEmpty()) {
			return slocArray;
		}
		for (PackageInfo subPackageInfo : packageInfo.getSubPackageList()) {
			if (subPackageInfo.getSlocArrayForCurrentAndSubPkg() == null) {
				int[] slocArrayPlusForSubPkg = sumSLOCForCurrentAndSubPkg(subPackageInfo);
				subPackageInfo.setSlocArrayForCurrentAndSubPkg(slocArrayPlusForSubPkg);
			}
			sumSLOC(slocArray, subPackageInfo.getSlocArrayForCurrentAndSubPkg());
		}
		return slocArray;
	}


	public static class SLOCCounter {
		private static final String BLOCK_COMMENT_END_MARK = "*/";
		private static final String BLOCK_COMMENT_START_MARK = "/*";
		private static final String LINE_COMMENT_MARK = "//";

		private boolean inBlockComment = false;

		private int logicLines;
		private int physicalLines;
		private int commentLines;

		/**
		 * After removing all the comments, classify the line of code: that is,
		 * the code line, the bracket line, and the blank line
		 * @param currentLine The content of current line
		 */
		private void countNonCommentCodeLine(String currentLine) {
			if (!"".equals(currentLine)) {
				if (!Pattern.compile("^[{}();]*$").matcher(currentLine).matches()) {
					logicLines++;
				}
				physicalLines++;
			}
		}

		/**
		 * After removing the inline block comment, classify the content:
		 * whether there is a multi-line block comment start character, whether there is a line comment start character.
		 * Note that this method will change the value of {@link #inBlockComment}
		 * @param currentLine The content of current line
		 */
		private void checkRestCodeLineWithoutSingleBlockComment(String currentLine) {
			if (currentLine.contains(BLOCK_COMMENT_START_MARK)) {
				inBlockComment = true;
				currentLine = currentLine.split("/\\*")[0].strip(); // 【/*】右边的必然是注释语句（因为没有结束符*/），因此只需要取数组的第一个即可
				countNonCommentCodeLine(currentLine);
			} else if (currentLine.contains(LINE_COMMENT_MARK)) {
				currentLine = currentLine.split(LINE_COMMENT_MARK)[0].strip(); // 【//】右边的必然是注释语句，因此只需要取数组的第一个即可
				countNonCommentCodeLine(currentLine);
			} else {
				// 非注释行
				countNonCommentCodeLine(currentLine);
			}
		}

		/**
		 * 判断是否为行注释，单行块注释，或多行块注释，并计数。注意此方法会改变{@code inBlockComment}
		 * Determine whether it is a line comment, a inline block comment, or a multi-line block comment, and then count.
		 * Note that this method will change the value of @link SLOCMetric#inBlockComment
		 * @param currentLine The content of current line
		 */
		private void countAndCheckToMeetInBlockComment(String currentLine) {
			if (currentLine.startsWith(LINE_COMMENT_MARK)) {
				// 单独的注释行
				commentLines++;
				inBlockComment = false;
			} else if (currentLine.startsWith(BLOCK_COMMENT_START_MARK)) {
				commentLines++;
				if (currentLine.contains(BLOCK_COMMENT_END_MARK)) {
					// /*comment*/foo.method();/*comment*/
					// /*comment*/});/*comment*/
					// /*comment*/}//comment
					currentLine = currentLine.replaceAll("/\\*.*?\\*/", "").strip();
					inBlockComment = false; // 必须放在checkRestCodeLineWithoutSingleBlockComment之前
					checkRestCodeLineWithoutSingleBlockComment(currentLine);
				} else {
					// 多行注释，当前行全是注释
					inBlockComment = true;
				}
			} else if (currentLine.contains(BLOCK_COMMENT_START_MARK)) {
				//	混合行
				// foo/*comment*/.method();/*comment*/
				// }/*comment*/);/*comment*/
				commentLines++;
				if (currentLine.contains(BLOCK_COMMENT_END_MARK)) {
					// Foo foo /*//comment*/ = new Foo(); //comment
					currentLine = currentLine.replaceAll("/\\*.*?\\*/", "").strip();
					inBlockComment = false;
					checkRestCodeLineWithoutSingleBlockComment(currentLine);
				} else {
					// callMethod();/*comment
					// }/*comment
					currentLine = currentLine.split("/\\*")[0].strip(); // 【/*】右边的必然是注释语句（因为没有结束符*/），因此只需要取数组的第一个即可
					inBlockComment = true;
					countNonCommentCodeLine(currentLine);
				}
			} else if (currentLine.contains(LINE_COMMENT_MARK)) {
				//混合行
				commentLines++;
				currentLine = currentLine.split(LINE_COMMENT_MARK)[0].strip(); // 【//】右边的必然是注释语句，因此只需要取数组的第一个即可
				inBlockComment = false;
				countNonCommentCodeLine(currentLine);
			} else {
				// 非注释行
				countNonCommentCodeLine(currentLine);
				if (inBlockComment) {
					commentLines++;
				}
				inBlockComment = false; // 必须放在if之后
			}
		}

		public void countSLOCByLine(String sourceLine) {
			String currentLine = sourceLine.strip();
			// sloc计数
			if (!"".equals(currentLine)) {
				// 将引号内容删除：".*?"
				currentLine = currentLine.replaceAll("\".*?\"", "foo");
				if (inBlockComment) {
					if (currentLine.contains(BLOCK_COMMENT_END_MARK)) {
						int index = currentLine.indexOf(BLOCK_COMMENT_END_MARK);
						if (index + 2 == currentLine.length()) {
							// 单独注释行 comment*/
							commentLines++;
							inBlockComment = false;
						} else {
							// comment*/super()/*comment
							currentLine = currentLine.substring(currentLine.indexOf(BLOCK_COMMENT_END_MARK) + 2)
							                         .strip();
							countAndCheckToMeetInBlockComment(currentLine);
						}
					} else {
						commentLines++;
						//	还处于块注释中
					}
				} else {
					countAndCheckToMeetInBlockComment(currentLine);
				}
			}
		}

		public void setSourceFileSLOCToCounterMap(Map<SLOCType, Integer> counterMap) {
			counterMap.replace(SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE, logicLines);
			counterMap.replace(SLOCType.COMMENT_LINES_FROM_SOURCE_FILE, commentLines);
			counterMap.replace(SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE, physicalLines);
		}

		public void setASTSLOCToCounterMap(Map<SLOCType, Integer> counterMap) {
			counterMap.replace(SLOCType.LOGIC_CODE_LINES_FROM_AST, logicLines);
			counterMap.replace(SLOCType.COMMENT_LINES_FROM_AST, commentLines);
			counterMap.replace(SLOCType.PHYSICAL_CODE_LINES_FROM_AST, physicalLines);
		}
	}


}
