package com.t4m.web.controller;

import com.t4m.extractor.entity.*;
import com.t4m.extractor.util.EntityUtil;
import com.t4m.extractor.util.TimeUtil;
import com.t4m.web.service.ClassService;
import com.t4m.web.service.ModuleService;
import com.t4m.web.service.PackageService;
import com.t4m.web.service.ProjectService;
import com.t4m.web.dao.ProjectRecordDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by Yuxiang Liao on 2020-07-21 23:23.
 */
@Controller
@RequestMapping("/dashboard/complexity")
public class ComplexityController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ComplexityController.class);

	@Resource(name = "ProjectService")
	private ProjectService projectService;

	@Resource(name = "ModuleService")
	private ModuleService moduleService;

	@Resource(name = "PackageService")
	private PackageService packageService;

	@Resource(name = "ClassService")
	private ClassService classService;

	@GetMapping("")
	public String slocMetric(Model model) {
		List<ProjectInfo> projectInfoList = ProjectRecordDao.getProjectInfoList();
		// 基本信息
		model.addAttribute("projectList", projectInfoList);
		// 用于timeline chart
		model.addAttribute("timeRecords", projectService.getTimeRecords());
		LinkedHashMap<String, List<List<Object>>> weightedMethodsCountDataset = new LinkedHashMap<>();
		for (ProjectInfo projectInfo : ProjectRecordDao.getProjectInfoList()) {
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
		model.addAttribute("weightedMethodsCountDataset", weightedMethodsCountDataset);

		LinkedHashMap<String, List<List<Object>>> maxComplexityOfClassDataset = new LinkedHashMap<>();
		for (ProjectInfo projectInfo : ProjectRecordDao.getProjectInfoList()) {
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
		model.addAttribute("maxComplexityOfClassDataset", maxComplexityOfClassDataset);

		LinkedHashMap<String, List<List<Object>>> avgComplexityOfClassDataset = new LinkedHashMap<>();
		for (ProjectInfo projectInfo : ProjectRecordDao.getProjectInfoList()) {
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
		model.addAttribute("avgComplexityOfClassDataset", avgComplexityOfClassDataset);

		LinkedHashMap<String, List<List<Object>>> responseForClassDataset = new LinkedHashMap<>();
		for (ProjectInfo projectInfo : ProjectRecordDao.getProjectInfoList()) {
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
		model.addAttribute("responseForClassDataset", responseForClassDataset);


		List<Object[]> methodComplexityDataset = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecordDao.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			for (MethodInfo methodInfo : projectInfo.getMethodList()) {
				Object[] row =
						new Object[]{time, methodInfo.getCyclomaticComplexity(), methodInfo.getFullyQualifiedName(),
						             methodInfo.getMethodDeclarationString()};
				methodComplexityDataset.add(row);
			}
		}
		model.addAttribute("methodComplexityDataset", methodComplexityDataset);

		List<Object[]> methodNumDataset = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecordDao.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			Object[] row = new Object[]{time, projectInfo.getMethodList().size()};
			methodNumDataset.add(row);
		}
		model.addAttribute("methodNumDataset", methodNumDataset);

		return "page/dashboard/complexity_metric";
	}

	@GetMapping("/table/method")
	@ResponseBody
	public List<Map<String, Object>> selectMethodRecord(
			@RequestParam(name = "classQualifiedName") String classQualifiedName,
			@RequestParam(name = "projectRecordIndex", defaultValue = "-1") int projectRecordIndex) {
		List<Map<String, Object>> rows = new ArrayList<>();
		ProjectInfo projectInfo = ProjectRecordDao.getTwoProjectInfoRecordByIndex(projectRecordIndex)[0];
		ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(), classQualifiedName);
		if (classInfo == null){
			LOGGER.info("No such class in this record.");
			return rows;
		}
		for (MethodInfo methodInfo : classInfo.getMethodInfoList()) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("name", methodInfo.getShortName());
			row.put("declaration", methodInfo.getMethodDeclarationString());
			row.put("class", methodInfo.getClassInfo().getFullyQualifiedName());
			row.put("module", methodInfo.getClassInfo().getPackageInfo().getModuleInfo().getShortName());
			row.put("cyclomaticComplexity", methodInfo.getCyclomaticComplexity());
			row.put("qualifiedName", methodInfo.getFullyQualifiedName());
			rows.add(row);
		}
		return rows;
	}

	@GetMapping("/table/class")
	@ResponseBody
	public List<Map<String, Object>> selectClassRecord(
			@RequestParam(name = "projectRecordIndex", defaultValue = "-1") int projectRecordIndex) {
		List<Map<String, Object>> rows = new ArrayList<>();
		ProjectInfo projectInfo = ProjectRecordDao.getTwoProjectInfoRecordByIndex(projectRecordIndex)[0];
		for (ClassInfo classInfo : projectInfo.getAllClassList()) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("name", classInfo.getShortName());
			row.put("type", classInfo.getClassModifier().toString());
			row.put("declaration", classInfo.getClassDeclaration().toString());
			row.put("package", classInfo.getPackageFullyQualifiedName());
			row.put("module", classInfo.getPackageInfo().getModuleInfo().getShortName());
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

	@GetMapping("/table/chart/class")
	@ResponseBody
	public List<Object[]> selectTableChartRecordForClass(@RequestParam(name = "qualifiedName") String qualifiedName) {

		//	第一行是系列名，从第二行开始，每一行是一条记录的数据，其中第一列是时间
		List<Object[]> dataset = new ArrayList<>();
		dataset.add(new String[]{"time", "Number of Method", "Weighted Method Count", "Max Complexity of Class",
		                         "Avg Complexity of Class", "Response for Class"});
		for (ProjectInfo projectInfo : ProjectRecordDao.getProjectInfoList()) {
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

}
