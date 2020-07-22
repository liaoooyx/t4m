package com.t4m.web.controller;

import com.t4m.extractor.entity.*;
import com.t4m.extractor.util.EntityUtil;
import com.t4m.web.service.ClassService;
import com.t4m.web.service.ModuleService;
import com.t4m.web.service.PackageService;
import com.t4m.web.service.ProjectService;
import com.t4m.web.util.ProjectRecord;
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
		List<ProjectInfo> projectInfoList = ProjectRecord.getProjectInfoList();
		// 基本信息
		model.addAttribute("projectList", projectInfoList);
		// 用于timeline chart
		model.addAttribute("timeRecords", projectService.getTimeRecords());
		model.addAttribute("datasetAllClass", projectService.getDataSetOfSLOC(ProjectService.FLAG_ALL_CLASS));
		model.addAttribute("datasetMainClass", projectService.getDataSetOfSLOC(ProjectService.FLAG_MAIN_PUBLIC_CLASS));
		// 用于table
		model.addAttribute("dataList", moduleService.getAllModulesSLOC(-1));
		model.addAttribute("previousName", "");
		model.addAttribute("previousType", "");
		model.addAttribute("isRoot", true);
		return "page/dashboard/complexity_metric";
	}

	@GetMapping("/table/method")
	@ResponseBody
	public List<Map<String, Object>> selectMethodRecord(
			@RequestParam(name = "classQualifiedName") String classQualifiedName,
			@RequestParam(name = "projectRecordIndex", defaultValue = "-1") int projectRecordIndex) {
		List<Map<String, Object>> rows = new ArrayList<>();
		ProjectInfo projectInfo = ProjectRecord.getTwoProjectInfoRecordByIndex(projectRecordIndex)[0];
		ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(), classQualifiedName);
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
		ProjectInfo projectInfo = ProjectRecord.getTwoProjectInfoRecordByIndex(projectRecordIndex)[0];
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

	@GetMapping("/table/chart")
	@ResponseBody
	public List<String[]> selectTableChartRecord(
			@RequestParam(name = "name") String name, @RequestParam(name = "type") String type) {
		if ("module".equals(type)) {
			return moduleService.getSLOCTableChartDataset(name);
		} else if ("package".equals(type)) {
			return packageService.getSLOCTableChartDataset(name, true);
		} else if ("current package".equals(type)) {
			return packageService.getSLOCTableChartDataset(name, false);
		} else if ("class".equals(type)) {
			return classService.getSLOCTableChartDataset(name);
		} else {
			LOGGER.error("Unexpected type received [{}] where record name is [{}]", type, name);
			return null;
		}
	}

}
