package com.t4m.web.controller;

import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.web.service.ClassService;
import com.t4m.web.service.ModuleService;
import com.t4m.web.service.PackageService;
import com.t4m.web.service.ProjectService;
import com.t4m.web.util.ProjectRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yuxiang Liao on 2020-06-23 22:50.
 */
@Controller
@RequestMapping("/dashboard")
public class DashboardController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DashboardController.class);

	@Resource(name = "ProjectService")
	private ProjectService projectService;

	@Resource(name = "ModuleService")
	private ModuleService moduleService;

	@Resource(name = "PackageService")
	private PackageService packageService;

	@Resource(name = "ClassService")
	private ClassService classService;

	@GetMapping("/overview")
	public String overview(Model model) {
		List<ProjectInfo> projectInfoList = ProjectRecord.getProjectInfoList();
		// 基本信息
		model.addAttribute("projectList", projectInfoList);
		// 用于趋势图
		model.addAttribute("timeRecords", projectService.getTimeRecords());
		model.addAttribute("moduleRecords", projectService.getNumOfModuleRecords());
		model.addAttribute("packageRecords", projectService.getNumOfPackageRecords());
		model.addAttribute("classRecords", projectService.getNumOfClassRecords());
		model.addAttribute("classAndInnerClassRecords", projectService.getNumOfClassAndInnerClassRecords());
		// 用于表格
		model.addAttribute("moduleMapList", moduleService.getModuleMapList(-1));
		model.addAttribute("packageMapList", packageService.getPackageMapList(-1));
		model.addAttribute("classMapList", classService.getClassMapList(-1));

		return "page/dashboard/overview";
	}

	@GetMapping("/overview/select/{projectRecordIndex}")
	public String selectProjectRecordByIndex(
			Model model, @PathVariable(name = "projectRecordIndex") int projectRecordIndex) {
		Map response = new LinkedHashMap();
		// 用于表格
		// 用于表格
		model.addAttribute("moduleMapList", moduleService.getModuleMapList(projectRecordIndex));
		model.addAttribute("packageMapList", packageService.getPackageMapList(projectRecordIndex));
		model.addAttribute("classMapList", classService.getClassMapList(projectRecordIndex));
		return "fragments/overview/project_info_overview";
	}


}
