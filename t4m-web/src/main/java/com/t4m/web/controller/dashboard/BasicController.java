package com.t4m.web.controller.dashboard;

import com.t4m.conf.GlobalProperties;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.web.service.ClassService;
import com.t4m.web.service.ModuleService;
import com.t4m.web.service.PackageService;
import com.t4m.web.service.ProjectService;
import com.t4m.web.util.ProjectRecordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Yuxiang Liao on 2020-06-23 22:50.
 */
@Controller
@RequestMapping("/dashboard/basic")
public class BasicController {

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
		// basic
		model.addAttribute("currentProjectInfo", projectService.getCurrentProjectInfoOfIndex(-1));
		model.addAttribute("preProjectInfo", projectService.getPreviousProjectInfoOfIndex(-1));
		model.addAttribute("currentProjectIdentifier", GlobalProperties.getCurrentProjectIdentifier());
		// for the timeline chart
		model.addAttribute("timeRecords", projectService.getTimeRecords());
		model.addAttribute("moduleRecords", projectService.getNumOfModuleForOverviewChart());
		model.addAttribute("packageRecords", projectService.getNumOfPackageForOverviewChart());
		model.addAttribute("javaFileRecords", projectService.getNumOfJavaFileForOverviewChart());
		model.addAttribute("classRecords", projectService.getNumOfClassForOverviewChart());
		model.addAttribute("allClassRecords", projectService.getNumOfAllClassForOverviewChart());
		return "page/dashboard/basic_metric";
	}

	@GetMapping("/table/module")
	@ResponseBody
	public List<Map<String, Object>> selectModuleRecord(
			@RequestParam(name = "projectRecordIndex", defaultValue = "-1") int projectRecordIndex) {
		ProjectInfo projectInfo = projectService.getCurrentProjectInfoOfIndex(projectRecordIndex);
		if (projectInfo == null) {
			return new ArrayList<>();
		}
		return moduleService.getBasicInfoForTable(projectInfo);
	}

	@GetMapping("/table/package")
	@ResponseBody
	public List<Map<String, Object>> selectPackageRecord(
			@RequestParam(name = "projectRecordIndex", defaultValue = "-1") int projectRecordIndex) {
		ProjectInfo projectInfo = projectService.getCurrentProjectInfoOfIndex(projectRecordIndex);
		if (projectInfo == null) {
			return new ArrayList<>();
		}
		return packageService.getBasicInfoForTable(projectInfo);
	}

	@GetMapping("/table/class")
	@ResponseBody
	public List<Map<String, Object>> selectClassRecord(
			@RequestParam(name = "projectRecordIndex", defaultValue = "-1") int projectRecordIndex) {
		ProjectInfo projectInfo = projectService.getCurrentProjectInfoOfIndex(projectRecordIndex);
		if (projectInfo == null) {
			return new ArrayList<>();
		}
		return classService.getBasicInfoForTable(projectInfo);
	}

}
