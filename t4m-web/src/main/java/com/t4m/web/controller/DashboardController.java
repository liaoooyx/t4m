package com.t4m.web.controller;

import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.EntityUtil;
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
import java.util.List;

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
		return "page/dashboard/overview";
	}

	@GetMapping("/overview/select/{projectRecordIndex}")
	public String selectProjectRecordByIndex(
			Model model, @PathVariable(name = "projectRecordIndex") int projectRecordIndex) {
		// 用于表格
		model.addAttribute("moduleMapList", moduleService.getModuleMapList(projectRecordIndex));
		model.addAttribute("packageMapList", packageService.getPackageMapList(projectRecordIndex));
		// TODO 关于SLOC的参数选择有问题。外部类的参数包括内部类，且ast格式的commentline只有docline，不包括"//"。需要更周全的选择
		model.addAttribute("classMapList", classService.getClassMapList(projectRecordIndex));
		return "fragments/dashboard/project_list_template";
	}

	@GetMapping("/sloc")
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
		return "page/dashboard/sloc_metric";
	}

	@GetMapping("/sloc/table")
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
			ProjectInfo projectInfo = ProjectRecord.getTwoProjectInfoRecordByIndex(projectRecordIndex)[0];
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

	@GetMapping("/sloc/table/chart")
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
