package com.t4m.web.service;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.DirHierarchyNode;
import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.TimeUtil;
import com.t4m.web.util.ProjectRecord;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Yuxiang Liao on 2020-06-27 02:37.
 */
@Service("ProjectService")
public class ProjectService {

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
	 * 所有记录的类数量
	 */
	public List<Integer> getNumOfClassRecords() {
		List<Integer> numOfClassRecords = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			numOfClassRecords.add(projectInfo.getClassList().size());
		}
		return numOfClassRecords;
	}

	/**
	 * 所有记录的内部类和外部类数量
	 */
	public List<Integer> getNumOfClassAndInnerClassRecords() {
		List<Integer> numOfClassAndInnerClassRecords = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			numOfClassAndInnerClassRecords.add(
					projectInfo.getClassList().size() + projectInfo.getInnerClassList().size());
		}
		return numOfClassAndInnerClassRecords;
	}

	/**
	 * 用于Dashboard-SLOC页面中timeline chart的数据集。 第1层：Map，key为创建时间，嵌套List 第2层：Map，key为前端Echart需要的4个series，分别为Interface,
	 * Abstract Class, Class and Inner Class，嵌套List 第3层：List，包括SLOC-code, SLOC-comment, SLOC-code + SLOC-comment,
	 * ClassName, OfWhichModule。
	 */
	public Map<String, Map<String, List<Object>>> getDataSetOfSLOC() {
		LinkedHashMap<String, Map<String, List<Object>>> timeline = new LinkedHashMap<>();
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			Map<String, List<Object>> series = new HashMap<>();
			series.put("Interface", new ArrayList<>());
			series.put("Abstract Class", new ArrayList<>());
			series.put("Class", new ArrayList<>());
			series.put("Inner Class", new ArrayList<>());
			series.put("Enum", new ArrayList<>());
			series.put("Annotation", new ArrayList<>());
			series.put("package-info.java", new ArrayList<>());
			addDataRow(series, projectInfo.getClassList());
			addDataRow(series, projectInfo.getInnerClassList());
			timeline.put(time, series);
		}
		return timeline;
	}

	private void addDataRow(Map<String, List<Object>> series, List<ClassInfo> classInfoList) {
		for (ClassInfo classInfo : classInfoList) {
			List<Object> rows = null;
			if (classInfo.isInnerClass()) {
				rows = series.get("Inner Class");
			} else if ("package-info".equals(classInfo.getShortName())){
				rows = series.get("Class");
			} else {
				switch (classInfo.getClassModifier()) {
					case CLASS:
						rows = series.get("Class");
						break;
					case ENUM:
						rows = series.get("Enum");
						break;
					case ANNOTATION:
						rows = series.get("ANNOTATION");
						break;
					case ABSTRACT_CLASS:
						rows = series.get("Abstract Class");
						break;
					case INTERFACE:
						rows = series.get("Interface");
						break;
				}
			}

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
	}

}
