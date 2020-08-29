package com.t4m.web.service;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.EntityUtil;
import com.t4m.extractor.util.MathUtil;
import com.t4m.extractor.util.TimeUtil;
import com.t4m.web.util.ProjectRecordUtil;
import com.t4m.web.util.dataset.BasicDatasetUtil;
import com.t4m.web.util.dataset.ClassTypeUtil;
import com.t4m.web.util.dataset.SLOCDatasetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Yuxiang Liao on 2020-06-26 15:56.
 */
@Service("ClassService")
public class ClassService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClassService.class);

	public static final int FLAG_ALL_CLASS = 1;
	public static final int FLAG_MAIN_PUBLIC_CLASS = 2;

	public List<String[]> getSLOCForTableChart(String className) {
		List<String[]> dataset = new ArrayList<>();
		dataset.add(new String[]{"time", "Logic Code Lines (Source File)", "Physical Code Lines (Source File)",
		                         "Comment Lines", "% of Comment Lines (Source File)", "Logic Code Lines (JavaParser)",
		                         "Physical Code Lines (JavaParser)", "Comment Lines (JavaParser)",
		                         "% of Comment Lines (JavaParser)"});
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(), className);
			String[] tempRow;
			if (classInfo == null) {
				tempRow = SLOCDatasetUtil.formatRowForTableChart(projectInfo.getCreateDate());
			} else {
				tempRow = SLOCDatasetUtil.formatRowForTableChart(projectInfo.getCreateDate(), classInfo.getSlocArray());
			}
			dataset.add(tempRow);
		}
		return dataset;
	}

	public List<Map<String, Object>> getBasicInfoForTable(ProjectInfo projectInfo) {
		List<Map<String, Object>> rows = new ArrayList<>();
		for (ClassInfo classInfo : projectInfo.getAllClassList()) {
			Map<String, Object> row = new LinkedHashMap<>();
			BasicDatasetUtil.insertCommonDatasetForTable(row, classInfo);
			row.put("fieldNum", classInfo.getNumberOfFields());
			row.put("methodNum", classInfo.getNumberOfMethods());
			row.put("enumConstantsNum", classInfo.getNumberOfEnumConstants());
			row.put("annotationMembers", classInfo.getNumberOfAnnotationMembers());
			row.put("qualifiedName", classInfo.getFullyQualifiedName());
			rows.add(row);
		}
		return rows;
	}

	public Map<String, Map<String, List<Object>>> getCohesionForOverviewChart() {
		LinkedHashMap<String, Map<String, List<Object>>> classCohesionDataset = new LinkedHashMap<>();
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			Map<String, List<Object>> series = new HashMap<>();
			ClassTypeUtil.insertCommonClassTypeRowForOverviewChart(series);
			for (ClassInfo classInfo : projectInfo.getAllClassList()) {
				ClassInfo.ClassModifier classModifier = classInfo.getClassModifier();
				List<Object> cols = new ArrayList<>();
				cols.add(classInfo.getTightClassCohesion());
				cols.add(classInfo.getLooseClassCohesion());
				cols.add(classInfo.getLackOfCohesionOfMethods4());
				cols.add(classInfo.getFullyQualifiedName()); // class qualified name
				cols.add(classInfo.getPackageInfo().getModuleInfo().getRelativePath()); // of which module
				List<Object> rows = ClassTypeUtil.chooseSeriesByClassModifier(series, classModifier);
				rows.add(cols);
			}
			classCohesionDataset.put(time, series);
		}
		return classCohesionDataset;
	}

	public List<Map<String, Object>> getCohesionForTable(ProjectInfo projectInfo) {
		List<Map<String, Object>> rows = new ArrayList<>();
		for (ClassInfo classInfo : projectInfo.getAllClassList()) {
			Map<String, Object> row = new LinkedHashMap<>();
			BasicDatasetUtil.insertCommonDatasetForTable(row, classInfo);
			row.put("lackOfCohesionInMethods4", classInfo.getLackOfCohesionOfMethods4());
			row.put("tightClassCohesion", classInfo.getTightClassCohesion());
			row.put("looseClassCohesion", classInfo.getLooseClassCohesion());
			row.put("qualifiedName", classInfo.getFullyQualifiedName());
			rows.add(row);
		}
		return rows;
	}

	public List<Object[]> getCohesionForTableChart(String qualifiedName) {
		//	第一行是系列名，从第二行开始，每一行是一条记录的数据，其中第一列是时间
		List<Object[]> dataset = new ArrayList<>();
		dataset.add(
				new String[]{"time", "Lack of Cohesion in Methods 4", "Tight Class Cohesion", "Loose Class Cohesion"});
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(), qualifiedName);
			Object[] tempRow = new Object[4];
			tempRow[0] = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			Arrays.fill(tempRow, 1, 3, null);
			if (classInfo != null) { //类可能还未创建或已经删除
				tempRow[1] = classInfo.getLackOfCohesionOfMethods4();
				tempRow[2] = classInfo.getTightClassCohesion();
				tempRow[3] = classInfo.getLooseClassCohesion();
			}
			dataset.add(tempRow);
		}
		return dataset;
	}

	public Map<String, List<List<Object>>> getWeightedMethodCountForOverviewChart() {
		LinkedHashMap<String, List<List<Object>>> weightedMethodsCountDataset = new LinkedHashMap<>();
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			List<List<Object>> rows = new ArrayList<>();
			for (ClassInfo classInfo : projectInfo.getAllClassList()) {
				List<Object> cols = new ArrayList<>();
				cols.add(classInfo.getNumberOfMethods()); // code line
				cols.add(classInfo.getWeightedMethodsCount()); // code line
				cols.add(classInfo.getFullyQualifiedName()); // class qualified name
				cols.add(classInfo.getPackageInfo().getModuleInfo().getRelativePath()); // of which module
				rows.add(cols);
			}
			weightedMethodsCountDataset.put(time, rows);
		}
		return weightedMethodsCountDataset;
	}

	public Map<String, List<List<Object>>> getMaxComplexityForOverviewChart() {
		LinkedHashMap<String, List<List<Object>>> maxComplexityOfClassDataset = new LinkedHashMap<>();
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			List<List<Object>> rows = new ArrayList<>();
			for (ClassInfo classInfo : projectInfo.getAllClassList()) {
				List<Object> cols = new ArrayList<>();
				cols.add(classInfo.getNumberOfMethods()); // code line
				cols.add(classInfo.getMaxCyclomaticComplexity()); // code line
				cols.add(classInfo.getFullyQualifiedName()); // class qualified name
				cols.add(classInfo.getPackageInfo().getModuleInfo().getRelativePath()); // of which module
				rows.add(cols);
			}
			maxComplexityOfClassDataset.put(time, rows);
		}
		return maxComplexityOfClassDataset;
	}

	public Map<String, List<List<Object>>> getAvgComplexityForOverviewChart() {
		LinkedHashMap<String, List<List<Object>>> avgComplexityOfClassDataset = new LinkedHashMap<>();
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			List<List<Object>> rows = new ArrayList<>();
			for (ClassInfo classInfo : projectInfo.getAllClassList()) {
				List<Object> cols = new ArrayList<>();
				cols.add(classInfo.getNumberOfMethods());
				cols.add(classInfo.getAvgCyclomaticComplexity());
				cols.add(classInfo.getFullyQualifiedName()); // class qualified name
				cols.add(classInfo.getPackageInfo().getModuleInfo().getRelativePath()); // of which module
				rows.add(cols);
			}
			avgComplexityOfClassDataset.put(time, rows);
		}
		return avgComplexityOfClassDataset;
	}

	public Map<String, List<List<Object>>> getResponseForClassForOverviewChart() {
		LinkedHashMap<String, List<List<Object>>> responseForClassDataset = new LinkedHashMap<>();
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			List<List<Object>> rows = new ArrayList<>();
			for (ClassInfo classInfo : projectInfo.getAllClassList()) {
				List<Object> cols = new ArrayList<>();
				cols.add(classInfo.getNumberOfMethods());
				cols.add(classInfo.getResponseForClass());
				cols.add(classInfo.getFullyQualifiedName()); // class qualified name
				cols.add(classInfo.getPackageInfo().getModuleInfo().getRelativePath()); // of which module
				rows.add(cols);
			}
			responseForClassDataset.put(time, rows);
		}
		return responseForClassDataset;
	}

	public List<Map<String, Object>> getComplexityForTable(ProjectInfo projectInfo) {
		List<Map<String, Object>> rows = new ArrayList<>();
		for (ClassInfo classInfo : projectInfo.getAllClassList()) {
			Map<String, Object> row = new LinkedHashMap<>();
			BasicDatasetUtil.insertCommonDatasetForTable(row, classInfo);
			row.put("numOfFields", classInfo.getNumberOfFields());
			row.put("numOfMethod", classInfo.getNumberOfMethods());
			row.put("weightedMethodsCount", classInfo.getWeightedMethodsCount());
			row.put("maxComplexity", classInfo.getMaxCyclomaticComplexity());
			row.put("avgComplexity", classInfo.getAvgCyclomaticComplexity());
			row.put("responseForClass", classInfo.getResponseForClass());
			row.put("qualifiedName", classInfo.getFullyQualifiedName());
			rows.add(row);
		}
		return rows;
	}

	public List<Object[]> getComplexityForTableChart(String qualifiedName) {
		//	第一行是系列名，从第二行开始，每一行是一条记录的数据，其中第一列是时间
		List<Object[]> dataset = new ArrayList<>();
		dataset.add(new String[]{"time", "Number of Method", "Weighted Method Count", "Max Complexity of Class",
		                         "Avg Complexity of Class", "Response for Class"});
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(), qualifiedName);
			Object[] tempRow = new Object[6];
			tempRow[0] = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			Arrays.fill(tempRow, 1, 5, null);
			if (classInfo != null) { //类可能还未创建或已经删除
				tempRow[1] = classInfo.getNumberOfMethods();
				tempRow[2] = classInfo.getWeightedMethodsCount();
				tempRow[3] = classInfo.getMaxCyclomaticComplexity();
				tempRow[4] = classInfo.getAvgCyclomaticComplexity();
				tempRow[5] = classInfo.getResponseForClass();
			}
			dataset.add(tempRow);
		}
		return dataset;
	}

	public List<Object[]> getCouplingBetweenObjectsForOverviewChart() {
		List<Object[]> classCouplingBetweenObjectsDataset = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			for (ClassInfo classInfo : projectInfo.getAllClassList()) {
				Object[] row =
						new Object[]{time, classInfo.getCouplingBetweenObjects(), classInfo.getFullyQualifiedName(),
						             classInfo.getPackageInfo().getModuleInfo().getRelativePath()};
				classCouplingBetweenObjectsDataset.add(row);
			}
		}
		return classCouplingBetweenObjectsDataset;
	}

	public List<Object[]> getMessagePassingCouplingForOverviewChart() {
		List<Object[]> classMessagePassingCouplingDataset = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			for (ClassInfo classInfo : projectInfo.getAllClassList()) {
				Object[] row =
						new Object[]{time, classInfo.getMessagePassingCoupling(), classInfo.getFullyQualifiedName(),
						             classInfo.getPackageInfo().getModuleInfo().getRelativePath()};
				classMessagePassingCouplingDataset.add(row);
			}
		}
		return classMessagePassingCouplingDataset;
	}

	public List<Map<String, Object>> getCouplingForTable(ProjectInfo projectInfo, String pkgQualifiedName) {
		List<Map<String, Object>> rows = new ArrayList<>();
		PackageInfo packageInfo = EntityUtil.getPackageByQualifiedName(projectInfo.getPackageList(), pkgQualifiedName);
		if (packageInfo == null) {
			LOGGER.info("No such package in this record.");
			return rows;
		}
		for (ClassInfo classInfo : packageInfo.getAllClassList()) {
			Map<String, Object> row = new LinkedHashMap<>();
			BasicDatasetUtil.insertCommonDatasetForTable(row, classInfo);
			row.put("couplingBetweenObjects", classInfo.getCouplingBetweenObjects());
			row.put("afferentCoupling", classInfo.getAfferentCoupling());
			row.put("efferentCoupling", classInfo.getEfferentCoupling());
			row.put("instability", classInfo.getInstability());
			row.put("messagePassingCoupling", classInfo.getMessagePassingCoupling());
			row.put("qualifiedName", classInfo.getFullyQualifiedName());
			rows.add(row);
		}
		return rows;
	}

	public List<Object[]> getCouplingForTableChart(String qualifiedName) {
		//	第一行是系列名，从第二行开始，每一行是一条记录的数据，其中第一列是时间
		List<Object[]> dataset = new ArrayList<>();
		dataset.add(new String[]{"time", "Coupling Between Objects", "Afferent Coupling", "Efferent Coupling",
		                         "Instability", "Message Passing Coupling"});
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(), qualifiedName);
			Object[] tempRow = new Object[6];
			tempRow[0] = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			Arrays.fill(tempRow, 1, 5, null);
			if (classInfo != null) { //类可能还未创建或已经删除
				tempRow[1] = classInfo.getMessagePassingCoupling();
				tempRow[2] = classInfo.getCouplingBetweenObjects();
				tempRow[3] = classInfo.getAfferentCoupling();
				tempRow[4] = classInfo.getEfferentCoupling();
				tempRow[5] = classInfo.getInstability();
			}
			dataset.add(tempRow);
		}
		return dataset;
	}

	public List<Object[]> getDeepOfInheritanceTreeForOverviewChart() {
		List<Object[]> deepOfInheritanceTreeDataset = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			for (ClassInfo classInfo : projectInfo.getAllClassList()) {
				Object[] row =
						new Object[]{time, classInfo.getDeepOfInheritanceTree(), classInfo.getFullyQualifiedName(),
						             classInfo.getPackageInfo().getModuleInfo().getRelativePath()};
				deepOfInheritanceTreeDataset.add(row);
			}
		}
		return deepOfInheritanceTreeDataset;
	}

	public List<Object[]> getNumberOfChildForOverviewChart() {
		List<Object[]> numberOfChildrenDataset = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			for (ClassInfo classInfo : projectInfo.getAllClassList()) {
				Object[] row = new Object[]{time, classInfo.getNumberOfChildren(), classInfo.getFullyQualifiedName(),
				                            classInfo.getPackageInfo().getModuleInfo().getRelativePath()};
				numberOfChildrenDataset.add(row);
			}
		}
		return numberOfChildrenDataset;
	}

	public List<Map<String, Object>> getInheritanceForTable(ProjectInfo projectInfo) {
		List<Map<String, Object>> rows = new ArrayList<>();
		for (ClassInfo classInfo : projectInfo.getAllClassList()) {
			Map<String, Object> row = new LinkedHashMap<>();
			BasicDatasetUtil.insertCommonDatasetForTable(row, classInfo);
			row.put("deepOfInheritanceTree", classInfo.getDeepOfInheritanceTree());
			row.put("numberOfChildren", classInfo.getNumberOfChildren());
			row.put("qualifiedName", classInfo.getFullyQualifiedName());
			rows.add(row);
		}
		return rows;
	}

	public List<Object[]> getInheritanceForTableChart(String qualifiedName) {
		//	第一行是系列名，从第二行开始，每一行是一条记录的数据，其中第一列是时间
		List<Object[]> dataset = new ArrayList<>();
		dataset.add(new String[]{"time", "Deep of Inheritance Tree", "Number of Children"});
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(), qualifiedName);
			Object[] tempRow = new Object[3];
			tempRow[0] = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			Arrays.fill(tempRow, 1, 2, null);
			if (classInfo != null) { //类可能还未创建或已经删除
				tempRow[1] = classInfo.getDeepOfInheritanceTree();
				tempRow[2] = classInfo.getNumberOfChildren();
			}
			dataset.add(tempRow);
		}
		return dataset;
	}

	/**
	 * For Dashboard-SLOC -- overall chart
	 * 1st level: Map，key is create time. Nested with list
	 * 2nd level: Map，key are four series that required by Echart: Interface, Abstract Class, Class and Inner Class. Nested with List
	 * 3rd level: Including SLOC-code, SLOC-comment, SLOC-comment / SLOC-code, ClassName, OfWhichModule.
	 *
	 * @param flag When it is 1, use all classes (including inner classes and extra classes);
	 * when it is 2, use only the main public class corresponding to the java file
	 * @return The data that repose to the front-end.
	 */
	public Map<String, Map<String, List<Object>>> getDataSetOfSLOC(int flag) {
		LinkedHashMap<String, Map<String, List<Object>>> timeline = new LinkedHashMap<>();
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			Map<String, List<Object>> series = new HashMap<>();
			ClassTypeUtil.insertCommonClassTypeRowForOverviewChart(series);
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
		for (ClassInfo classInfo : classInfoList) {
			// 注意package-info.java的getClassModifier()可能为null
			ClassInfo.ClassModifier classModifier = classInfo.getClassModifier();
			List<Object> cols = new ArrayList<>();
			int codeLine = 0;
			int commentLine = 0;
			if (flag == FLAG_ALL_CLASS) {
				Map<ClassInfo.SLOCType, Integer> counterMap = classInfo.getSlocCounterMap();
				codeLine = counterMap.get(ClassInfo.SLOCType.PHYSICAL_CODE_LINES_FROM_AST);
				commentLine = counterMap.get(ClassInfo.SLOCType.COMMENT_LINES_FROM_AST);
			} else if (flag == FLAG_MAIN_PUBLIC_CLASS) {
				Map<ClassInfo.SLOCType, Integer> counterMap = classInfo.getSlocCounterMap();
				codeLine = counterMap.get(ClassInfo.SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE);
				commentLine = counterMap.get(ClassInfo.SLOCType.COMMENT_LINES_FROM_SOURCE_FILE);
			}
			cols.add(codeLine); // code line
			cols.add(commentLine); // comment line
			String percentage = MathUtil.percentage(commentLine, (float) codeLine + commentLine);
			cols.add(percentage);
			cols.add(classInfo.getFullyQualifiedName()); // class qualified name
			cols.add(classInfo.getPackageInfo().getModuleInfo().getRelativePath()); // of which module
			List<Object> rows = ClassTypeUtil.chooseSeriesByClassModifier(series, classModifier);
			rows.add(cols);
		}
	}

}
