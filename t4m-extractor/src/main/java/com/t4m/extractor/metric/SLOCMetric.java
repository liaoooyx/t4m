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
public class SLOCMetric {

	private static final String BLOCK_COMMENT_END_MARK = "*/";
	private static final String BLOCK_COMMENT_START_MARK = "/*";
	private static final String LINE_COMMENT_MARK = "//";

	private boolean inBlockComment = false;

	private int logicLines;
	private int physicalLines;
	private int commentLines;

	/**
	 * 去掉所有注释内容后，对代码进行判断：即代码行，单独的括号行，空行
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
	 * 去除单行块注释后，进行内容判断：是否有多行块注释起始符，是否有行注释起始符。注意此方法会改变{@code inBlockComment}
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

	/**
	 * 判断代码行的SLOC，并计数。
	 */
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
						currentLine = currentLine.substring(currentLine.indexOf(BLOCK_COMMENT_END_MARK) + 2).strip();
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

	/**
	 * 此方法应该传入ClassInfo中的slocCounterMap对象。
	 * 将扫描出来的sloc计数，加入到source_file对应的3个key中。
	 */
	public void setSourceFileSLOCToCounterMap(Map<SLOCType, Integer> counterMap) {
		counterMap.replace(SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE, logicLines);
		counterMap.replace(SLOCType.COMMENT_LINES_FROM_SOURCE_FILE, commentLines);
		counterMap.replace(SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE, physicalLines);
	}

	/**
	 * 此方法应该传入ClassInfo中的slocCounterMap对象。
	 * 将扫描出来的sloc计数，加入到ast对应的3个key中。
	 */
	public void setASTSLOCToCounterMap(Map<SLOCType, Integer> counterMap) {
		counterMap.replace(SLOCType.LOGIC_CODE_LINES_FROM_AST, logicLines);
		counterMap.replace(SLOCType.COMMENT_LINES_FROM_AST, commentLines);
		counterMap.replace(SLOCType.PHYSICAL_CODE_LINES_FROM_AST, physicalLines);
	}


	/**
	 * 将SLOCMap转换成Array
	 */
	public static void calculateSloc(ClassInfo classInfo) {
		int[] slocArray = new int[6];
		Arrays.fill(slocArray, 0);
		sumSLOC(slocArray, classInfo.getSlocCounterMap());
		classInfo.setSlocArray(slocArray);
	}

	/**
	 * 计算各个类的SLOC之和：1，计算当前包，2，计算当前包+子包
	 */
	public static void calculateSloc(PackageInfo packageInfo) {
		int[] slocArray = sumSLOCForCurrentPkg(packageInfo);
		packageInfo.setSlocArrayForCurrentPkg(slocArray);
		int[] slocArrayPlus = sumSLOCForCurrentAndSubPkg(packageInfo);
		packageInfo.setSlocArrayForCurrentAndSubPkg(slocArrayPlus);
	}

	/**
	 * 计算各个包的SLOC之和
	 */
	public static void calculateSloc(ModuleInfo moduleInfo) {
		int[] slocArray = new int[6];
		Arrays.fill(slocArray, 0);
		for (PackageInfo packageInfo : moduleInfo.getPackageList()) {
			SLOCMetric.sumSLOC(slocArray, packageInfo.getSlocArrayForCurrentPkg());
		}
		moduleInfo.setSlocArray(slocArray);
	}

	/**
	 * 该方法会直接将数值叠加在传入参数{@code slocArray}上
	 * 索引与对应的SLOC：
	 * 0--SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE；
	 * 1--SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE
	 * 2--SLOCType.COMMENT_LINES_FROM_SOURCE_FILE；
	 * 3--SLOCType.LOGIC_CODE_LINES_FROM_AST；
	 * 4--SLOCType.PHYSICAL_CODE_LINES_FROM_AST；
	 * 5--SLOCType.COMMENT_LINES_FROM_AST
	 */
	private static void sumSLOC(int[] slocArray, Map<SLOCType, Integer> slocMap) {
		slocArray[0] += slocMap.get(SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE);
		slocArray[1] += slocMap.get(SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE);
		slocArray[2] += slocMap.get(SLOCType.COMMENT_LINES_FROM_SOURCE_FILE);
		slocArray[3] += slocMap.get(SLOCType.LOGIC_CODE_LINES_FROM_AST);
		slocArray[4] += slocMap.get(SLOCType.PHYSICAL_CODE_LINES_FROM_AST);
		slocArray[5] += slocMap.get(SLOCType.COMMENT_LINES_FROM_AST);
	}

	/**
	 * @param slocArray 目标数组，数值将会叠加在次数组上
	 * @param inputSLOC 需要将此数组中的值，加到{@code slocArray}上
	 */
	private static void sumSLOC(int[] slocArray, int[] inputSLOC) {
		slocArray[0] += inputSLOC[0];
		slocArray[1] += inputSLOC[1];
		slocArray[2] += inputSLOC[2];
		slocArray[3] += inputSLOC[3];
		slocArray[4] += inputSLOC[4];
		slocArray[5] += inputSLOC[5];
	}


	/**
	 * 获取自身直接持有的外部类的SLOC（外部类的SLOC以及包括了内部类的SLOC），以数组形式返回。
	 * 索引与对应的值，查看{@link SLOCMetric#sumSLOC}
	 */
	private static int[] sumSLOCForCurrentPkg(PackageInfo packageInfo) {
		int[] slocArray = new int[6];
		Arrays.fill(slocArray, 0);
		for (ClassInfo classInfo : packageInfo.getAllClassList()) {
			SLOCMetric.sumSLOC(slocArray, classInfo.getSlocArray());
		}
		return slocArray;
	}

	/**
	 * 获取自身直接持有的外部类的SLOC（外部类的SLOC以及包括了内部类的SLOC），以及子包的SLOC，以数组形式返回。
	 * 如果没有子包则返回自身的sloc
	 * 索引与对应的值，查看{@link SLOCMetric#sumSLOC}
	 */
	private static int[] sumSLOCForCurrentAndSubPkg(PackageInfo packageInfo) {
		int[] slocArray = sumSLOCForCurrentPkg(packageInfo);
		if (packageInfo.getSubPackageList().isEmpty()){
			return slocArray;
		}
		for (PackageInfo subPackageInfo : packageInfo.getSubPackageList()) {
			if (subPackageInfo.getSlocArrayForCurrentAndSubPkg()==null){
				int[] slocArrayPlusForSubPkg = sumSLOCForCurrentAndSubPkg(subPackageInfo);
				subPackageInfo.setSlocArrayForCurrentAndSubPkg(slocArrayPlusForSubPkg);
			}
			SLOCMetric.sumSLOC(slocArray, subPackageInfo.getSlocArrayForCurrentAndSubPkg());
		}
		return slocArray;
	}


}
