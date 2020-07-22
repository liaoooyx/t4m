package com.t4m.web.controller;

import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
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
import java.util.List;
import java.util.Objects;

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

	@GetMapping("/table")
	public String selectRecord(
			@RequestParam(name = "name", defaultValue = "") String name,
			@RequestParam(name = "type", defaultValue = "") String type,
			@RequestParam(name = "projectRecordIndex", defaultValue = "-1") int projectRecordIndex, Model model) {
		//用于"返回上级"的操作
		boolean isRoot = false;
		String preName = "";
		String preType = "";
		if ("".equals(type) || "".equals(name)) { // 获取所有模块信息
			isRoot = true; // root没有"返回上级"选项
			model.addAttribute("dataList", moduleService.getAllModulesSLOC(projectRecordIndex));
		} else if ("module".equals(type)) { // 获取指定模块下的第一层包和类
			// "返回上级"后，直接回到所有模块SLOC的展示
			model.addAttribute("dataList", moduleService.getSLOCRecordByModuleName(name, projectRecordIndex));
		} else if ("package".equals(type)) { // 获取指定包的类和直接子包
			ProjectInfo projectInfo = Objects.requireNonNull(
					ProjectRecord.getTwoProjectInfoRecordByIndex(projectRecordIndex))[0];
			PackageInfo packageInfo = EntityUtil.getPackageByQualifiedName(projectInfo.getPackageList(), name);
			if (packageInfo.hasPreviousPackage()) {
				preName = packageInfo.getPreviousPackage().getFullyQualifiedName();
				preType = "package";
			} else {
				preName = packageInfo.getModuleInfo().getRelativePath();
				preType = "module";
			}
			model.addAttribute("dataList", packageService.getSLOCRecordByPackageName(name, projectRecordIndex));
		}
		model.addAttribute("previousName", preName);
		model.addAttribute("previousType", preType);
		model.addAttribute("isRoot", isRoot);
		return "fragments/dashboard/sloc_list_template";
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
