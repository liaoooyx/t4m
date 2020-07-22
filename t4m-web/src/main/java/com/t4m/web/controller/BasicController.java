package com.t4m.web.controller;

import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.web.service.ClassService;
import com.t4m.web.service.ModuleService;
import com.t4m.web.service.PackageService;
import com.t4m.web.service.ProjectService;
import com.t4m.web.util.GlobalVariable;
import com.t4m.web.util.ProjectRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Created by Yuxiang Liao on 2020-06-23 22:50.
 */
@Controller
@RequestMapping("/dashboard/basic")
public class BasicController {

	private static final Logger LOGGER = LoggerFactory.getLogger(BasicController.class);

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
		ProjectInfo[] projectInfos = ProjectRecord.getTwoProjectInfoRecordByIndex(-1);
		// 基本信息
		model.addAttribute("currentProjectInfo", projectInfos[0]);
		model.addAttribute("preProjectInfo", projectInfos[1]);
		model.addAttribute("currentProjectName", GlobalVariable.CURRENT_PROJECT_NAME);
		// 用于趋势图
		model.addAttribute("timeRecords", projectService.getTimeRecords());
		model.addAttribute("moduleRecords", projectService.getNumOfModuleRecords());
		model.addAttribute("packageRecords", projectService.getNumOfPackageRecords());
		model.addAttribute("javaFileRecords", projectService.getNumOfJavaFileRecords());
		model.addAttribute("classRecords", projectService.getNumOfClassRecords());
		model.addAttribute("allClassRecords", projectService.getNumOfAllClassRecords());
		// 用于表格
		model.addAttribute("moduleMapList", moduleService.getModuleMapList(-1));
		model.addAttribute("packageMapList", packageService.getPackageMapList(-1));
		model.addAttribute("classMapList", classService.getClassMapList(-1));
		return "page/dashboard/basic_metric";
	}

	@GetMapping("/select/{projectRecordIndex}")
	public String selectProjectRecordByIndex(
			Model model, @PathVariable(name = "projectRecordIndex") int projectRecordIndex) {
		// 用于表格
		model.addAttribute("moduleMapList", moduleService.getModuleMapList(projectRecordIndex));
		model.addAttribute("packageMapList", packageService.getPackageMapList(projectRecordIndex));
		// TODO 关于SLOC的参数选择有问题。外部类的参数包括内部类，且ast格式的commentline只有docline，不包括"//"。需要更周全的选择
		model.addAttribute("classMapList", classService.getClassMapList(projectRecordIndex));
		return "fragments/dashboard/project_list_template";
	}

}
