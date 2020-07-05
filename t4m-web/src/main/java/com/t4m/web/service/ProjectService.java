package com.t4m.web.service;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.DirHierarchyNode;
import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.TimeUtil;
import com.t4m.web.util.ProjectRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Yuxiang Liao on 2020-06-27 02:37.
 */
@Service("ProjectService")
public class ProjectService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectService.class);

	public static final int FLAG_ALL_CLASS = 1;
	public static final int FLAG_MAIN_PUBLIC_CLASS = 2;

	/**
	 * 所有记录的创建时间
	 */
	public List<String> getTimeRecords() {
		List<String> timeRecords = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			timeRecords.add(TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate()));
		}
		return timeRecords;
	}

	/**
	 * 所有记录的模块数量
	 */
	public List<Integer> getNumOfModuleRecords() {
		List<Integer> numOfModuleRecords = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			numOfModuleRecords.add(projectInfo.getModuleList().size());
		}
		return numOfModuleRecords;
	}

	/**
	 * 所有记录的包数量
	 */
	public List<Integer> getNumOfPackageRecords() {
		List<Integer> numOfPackageRecords = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			numOfPackageRecords.add(projectInfo.getPackageList().size());
		}
		return numOfPackageRecords;
	}

	/**
	 * 所有记录的java文件数量
	 */
	public List<Integer> getNumOfJavaFileRecords() {
		List<Integer> numOfClassRecords = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			numOfClassRecords.add(projectInfo.getClassList().size());
		}
		return numOfClassRecords;
	}

	/**
	 * 所有记录的外部类数量
	 */
	public List<Integer> getNumOfClassRecords() {
		List<Integer> numOfClassRecords = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			numOfClassRecords.add(projectInfo.getClassList().size() + projectInfo.getExtraClassList().size());
		}
		return numOfClassRecords;
	}

	/**
	 * 所有记录的类数量（ClassDeclaration中的3种都包括）
	 */
	public List<Integer> getNumOfAllClassRecords() {
		List<Integer> numOfClassAndInnerClassRecords = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			numOfClassAndInnerClassRecords.add(projectInfo.getAllClassList().size());
		}
		return numOfClassAndInnerClassRecords;
	}

	/**
	 * 用于Dashboard-SLOC页面中timeline chart的数据集。 第1层：Map，key为创建时间，嵌套List 第2层：Map，key为前端Echart需要的4个series，分别为Interface,
	 * Abstract Class, Class and Inner Class，嵌套List 第3层：List，包括SLOC-code, SLOC-comment, SLOC-code + SLOC-comment,
	 * ClassName, OfWhichModule。
	 *
	 * @param flag 当为1时，使用所有类（包括内部类和extra类）；当为2时，仅使用与java文件对应的main public类
	 */
	public Map<String, Map<String, List<Object>>> getDataSetOfSLOC(int flag) {
		LinkedHashMap<String, Map<String, List<Object>>> timeline = new LinkedHashMap<>();
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			Map<String, List<Object>> series = new HashMap<>();
			series.put("Interface", new ArrayList<>());
			series.put("Abstract Class", new ArrayList<>());
			series.put("Class", new ArrayList<>());
			series.put("Enum", new ArrayList<>());
			series.put("Annotation", new ArrayList<>());
			if (flag == FLAG_ALL_CLASS) {
				addDataRow(series, projectInfo.getAllClassList(), flag);
			} else if (flag == FLAG_MAIN_PUBLIC_CLASS) {
				addDataRow(series, projectInfo.getClassList(), flag);
			}
			timeline.put(time, series);
		}
		return timeline;
	}

	private void addDataRow(Map<String, List<Object>> series, List<ClassInfo> classInfoList, int flag) {
		int i = 0;
		for (ClassInfo classInfo : classInfoList) {
			System.out.println(i++);
			List<Object> rows = null;
			// 注意package-info.java的getClassModifier()可能为null
			ClassInfo.ClassModifier classModifier = classInfo.getClassModifier();
			if (classModifier != null) {
				switch (classInfo.getClassModifier()) {
					case ENUM:
						rows = series.get("Enum");
						break;
					case ANNOTATION:
						rows = series.get("Annotation");
						break;
					case ABSTRACT_CLASS:
						rows = series.get("Abstract Class");
						break;
					case INTERFACE:
						rows = series.get("Interface");
						break;
					default:
						rows = series.get("Class");
				}
			} else if ("package-info".equals(classInfo.getShortName())) {
				rows = series.get("Class");
			} else {
				LOGGER.debug("Should not go into this statement, please use debug and check the program again.");
				throw new RuntimeException(
						"Should not go into this statement, please use debug and check the program again.");
			}
			if (flag == FLAG_ALL_CLASS) {
				addFromAST(rows, classInfo);
			} else if (flag == FLAG_MAIN_PUBLIC_CLASS) {
				addFromSourceFile(rows, classInfo);
			}
		}
	}

	private void addFromAST(List<Object> rows, ClassInfo classInfo) {
		List<Object> cols = new ArrayList<>();
		Map<ClassInfo.SLOCType, Integer> couterMap = classInfo.getSlocCounterMap();
		cols.add(couterMap.get(ClassInfo.SLOCType.LOGIC_CODE_LINES_FROM_AST)); // logic code line
		cols.add(couterMap.get(ClassInfo.SLOCType.DOC_COMMENT_LINES_FROM_AST)); // comment line
		int classSize = couterMap.get(ClassInfo.SLOCType.DOC_COMMENT_LINES_FROM_AST) + couterMap.get(
				ClassInfo.SLOCType.LOGIC_CODE_LINES_FROM_AST);
		cols.add(classSize);
		cols.add(classInfo.getFullyQualifiedName()); // class qualified name
		cols.add(classInfo.getPackageInfo().getModuleInfo().getRelativePath()); // of which module
		rows.add(cols);
	}

	private void addFromSourceFile(List<Object> rows, ClassInfo classInfo) {
		List<Object> cols = new ArrayList<>();
		Map<ClassInfo.SLOCType, Integer> couterMap = classInfo.getSlocCounterMap();
		cols.add(couterMap.get(ClassInfo.SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE)); // logic code line
		cols.add(couterMap.get(ClassInfo.SLOCType.ALL_COMMENT_LINES_FROM_SOURCE_FILE)); // comment line
		int classSize = couterMap.get(ClassInfo.SLOCType.ALL_COMMENT_LINES_FROM_SOURCE_FILE) + couterMap.get(
				ClassInfo.SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE);
		cols.add(classSize);
		cols.add(classInfo.getFullyQualifiedName()); // class qualified name
		cols.add(classInfo.getPackageInfo().getModuleInfo().getRelativePath()); // of which module
		rows.add(cols);
	}

}
