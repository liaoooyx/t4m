package com.t4m.web.controller.dashboard;

import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.web.service.ClassService;
import com.t4m.web.service.ModuleService;
import com.t4m.web.service.PackageService;
import com.t4m.web.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Yuxiang Liao on 2020-07-21 23:23.
 */
@Controller
@RequestMapping("/dashboard/sloc")
public class SLOCController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SLOCController.class);

	@Resource(name = "ProjectService")
	private ProjectService projectService;

	@Resource(name = "ModuleService")
	private ModuleService moduleService;

	@Resource(name = "PackageService")
	private PackageService packageService;

	@Resource(name = "ClassService")
	private ClassService classService;

	@GetMapping("")
	public String overview(Model model) {
		model.addAttribute("projectList", projectService.getAllProjectInfos());
		// for timeline chart
		model.addAttribute("timeRecords", projectService.getTimeRecords());
		model.addAttribute("datasetAllClass", classService.getDataSetOfSLOC(ClassService.FLAG_ALL_CLASS));
		model.addAttribute("datasetMainClass", classService.getDataSetOfSLOC(ClassService.FLAG_MAIN_PUBLIC_CLASS));
		return "page/dashboard/sloc_metric";
	}

	@GetMapping("/table/module")
	@ResponseBody
	public List<Map<String, Object>> selectModuleRecord(
			@RequestParam(name = "projectRecordIndex", defaultValue = "-1") int projectRecordIndex) {
		ProjectInfo projectInfo = projectService.getCurrentProjectInfoOfIndex(projectRecordIndex);
		if (projectInfo == null) {
			return new ArrayList<>();
		}
		return moduleService.getSLOCForTable(projectInfo);
	}

	@PostMapping("/table/package")
	@ResponseBody
	public List<Map<String, Object>> selectPackageRecord(
			@RequestParam(name = "name") String moduleRelativePath,
			@RequestParam(name = "projectRecordIndex", defaultValue = "-1") int projectRecordIndex) {
		ProjectInfo projectInfo = projectService.getCurrentProjectInfoOfIndex(projectRecordIndex);
		if (projectInfo == null) {
			return new ArrayList<>();
		}
		ModuleInfo moduleInfo = moduleService.getModuleInfoByPath(projectInfo, moduleRelativePath);
		if (moduleInfo == null) {
			LOGGER.debug("No such module in this record. [{}]", moduleRelativePath);
			return new ArrayList<>();
		}
		return packageService.getSLOCForTable(moduleInfo);
	}

	@PostMapping("/table/subpackage")
	@ResponseBody
	public List<Map<String, Object>> selectSubPackageRecord(
			@RequestParam(name = "name") String packageQualifiedName,
			@RequestParam(name = "projectRecordIndex", defaultValue = "-1") int projectRecordIndex) {
		ProjectInfo projectInfo = projectService.getCurrentProjectInfoOfIndex(projectRecordIndex);
		if (projectInfo == null) {
			return new ArrayList<>();
		}
		PackageInfo packageInfo = packageService.getPackageInfoByQualifiedName(projectInfo, packageQualifiedName);
		if (packageInfo == null) {
			LOGGER.info("No such package in this record. [{}]", packageQualifiedName);
			return new ArrayList<>();
		}
		return packageService.getSLOCForTable(packageInfo);
	}

	@PostMapping("/table/chart")
	@ResponseBody
	public List<String[]> selectTableChartRecord(
			@RequestParam(name = "name") String name, @RequestParam(name = "level") String level) {
		if ("module".equals(level)) {
			return moduleService.getSLOCForTableChart(name);
		} else if ("package".equals(level)) {
			return packageService.getSLOCForTableChart(name, true);
		} else if ("current package".equals(level)) {
			return packageService.getSLOCForTableChart(name, false);
		} else if ("class".equals(level)) {
			return classService.getSLOCForTableChart(name);
		} else if ("project".equals(level)){
			return projectService.getSLOCForTableChart();
		}else {
			LOGGER.error("Unexpected type received [{}] where record name is [{}]", level, name);
			return new ArrayList<>();
		}
	}

}
