package com.t4m.web.controller.dashboard;

import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.web.service.ClassService;
import com.t4m.web.service.PackageService;
import com.t4m.web.service.ProjectService;
import com.t4m.web.util.ProjectRecordUtil;
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
 * Created by Yuxiang Liao on 2020-07-21 23:23.
 */
@Controller
@RequestMapping("/dashboard/coupling")
public class CouplingController {

	@Resource(name = "ProjectService")
	private ProjectService projectService;

	@Resource(name = "PackageService")
	private PackageService packageService;

	@Resource(name = "ClassService")
	private ClassService classService;

	@GetMapping("")
	public String overview(Model model) {
		List<ProjectInfo> projectInfoList = ProjectRecordUtil.getProjectInfoList();
		// 基本信息
		model.addAttribute("projectList", projectInfoList);
		// 用于timeline chart
		model.addAttribute("timeRecords", projectService.getTimeRecords());
		model.addAttribute("packageInstabilityAbstractnessGraphDataset",
		                   packageService.getInstabilityAbstractnessGraphForOverviewChart());
		model.addAttribute("classCouplingBetweenObjectsDataset",
		                   classService.getCouplingBetweenObjectsForOverviewChart());
		model.addAttribute("classMessagePassingCouplingDataset",
		                   classService.getMessagePassingCouplingForOverviewChart());
		return "page/dashboard/coupling_metric";
	}

	@GetMapping("/table/package")
	@ResponseBody
	public List<Map<String, Object>> selectPackageRecord(
			@RequestParam(name = "projectRecordIndex", defaultValue = "-1") int projectRecordIndex) {
		ProjectInfo projectInfo = projectService.getCurrentProjectInfoOfIndex(projectRecordIndex);
		if (projectInfo == null) {
			return new ArrayList<>();
		}
		return packageService.getCouplingForTable(projectInfo);
	}

	@GetMapping("/table/chart/package")
	@ResponseBody
	public List<Object[]> selectTableChartRecordForPackage(@RequestParam(name = "qualifiedName") String qualifiedName) {
		return packageService.getCouplingForTableChart(qualifiedName);
	}

	@GetMapping("/table/class")
	@ResponseBody
	public List<Map<String, Object>> selectClassRecord(
			@RequestParam(name = "pkgQualifiedName") String pkgQualifiedName,
			@RequestParam(name = "projectRecordIndex", defaultValue = "-1") int projectRecordIndex) {
		ProjectInfo projectInfo = projectService.getCurrentProjectInfoOfIndex(projectRecordIndex);
		if (projectInfo == null) {
			return new ArrayList<>();
		}
		return classService.getCouplingForTable(projectInfo, pkgQualifiedName);
	}

	@GetMapping("/table/chart/class")
	@ResponseBody
	public List<Object[]> selectTableChartRecordForClass(@RequestParam(name = "qualifiedName") String qualifiedName) {
		return classService.getCouplingForTableChart(qualifiedName);
	}

}
