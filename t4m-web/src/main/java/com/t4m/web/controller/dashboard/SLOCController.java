package com.t4m.web.controller.dashboard;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.EntityUtil;
import com.t4m.extractor.util.MathUtil;
import com.t4m.web.dao.ProjectRecordDao;
import com.t4m.web.service.ClassService;
import com.t4m.web.service.ModuleService;
import com.t4m.web.service.PackageService;
import com.t4m.web.service.ProjectService;
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
import java.util.LinkedHashMap;
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
	public String slocMetric(Model model) {
		List<ProjectInfo> projectInfoList = ProjectRecordDao.getProjectInfoList();
		// 基本信息
		model.addAttribute("projectList", projectInfoList);
		// 用于timeline chart
		model.addAttribute("timeRecords", projectService.getTimeRecords());
		model.addAttribute("datasetAllClass", projectService.getDataSetOfSLOC(ProjectService.FLAG_ALL_CLASS));
		model.addAttribute("datasetMainClass", projectService.getDataSetOfSLOC(ProjectService.FLAG_MAIN_PUBLIC_CLASS));
		return "page/dashboard/sloc_metric";
	}

	@GetMapping("/table/module")
	@ResponseBody
	public List<Map<String, Object>> selectModuleRecord(
			@RequestParam(name = "projectRecordIndex", defaultValue = "-1") int projectRecordIndex) {
		List<Map<String, Object>> rows = new ArrayList<>();
		ProjectInfo projectInfo = ProjectRecordDao.getTwoProjectInfoRecordByIndex(projectRecordIndex)[0];
		for (ModuleInfo moduleInfo : projectInfo.getModuleList()) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("name", moduleInfo.getRelativePath());
			row.put("level", "module");
			int[] slocArray = moduleInfo.getSlocArray();
			row.put("logicCodeLinesSF", slocArray[0]);
			row.put("physicalCodeLinesSF", slocArray[1]);
			row.put("CommentLinesSF", slocArray[2]);
			row.put("percentageOfCommentSF", MathUtil.percentage(slocArray[2], slocArray[1] + slocArray[2]));
			row.put("logicCodeLinesJP", slocArray[3]);
			row.put("physicalCodeLinesJP", slocArray[4]);
			row.put("CommentLinesJP", slocArray[5]);
			row.put("percentageOfCommentJP", MathUtil.percentage(slocArray[5], slocArray[4] + slocArray[5]));
			rows.add(row);
		}
		return rows;
	}

	@GetMapping("/table/package")
	@ResponseBody
	public List<Map<String, Object>> selectPackageRecord(
			@RequestParam(name = "moduleRelativePath") String moduleRelativePath,
			@RequestParam(name = "projectRecordIndex", defaultValue = "-1") int projectRecordIndex) {
		List<Map<String, Object>> rows = new ArrayList<>();
		ProjectInfo projectInfo = ProjectRecordDao.getTwoProjectInfoRecordByIndex(projectRecordIndex)[0];
		ModuleInfo moduleInfo = EntityUtil.getModuleByRelativeName(projectInfo.getModuleList(), moduleRelativePath);
		if (moduleInfo == null) {
			LOGGER.info("No such module in this record.");
			return rows;
		}
		for (PackageInfo packageInfo : moduleInfo.getPackageList()) {
			if (!packageInfo.hasPreviousPackage()) {
				Map<String, Object> row = new LinkedHashMap<>();
				row.put("name", packageInfo.getFullyQualifiedName());
				row.put("module", moduleInfo.getRelativePath());
				row.put("level", "package");
				int[] slocArray = packageInfo.getSlocArrayForCurrentAndSubPkg();
				row.put("logicCodeLinesSF", slocArray[0]);
				row.put("physicalCodeLinesSF", slocArray[1]);
				row.put("CommentLinesSF", slocArray[2]);
				row.put("percentageOfCommentSF", MathUtil.percentage(slocArray[2], slocArray[1] + slocArray[2]));
				row.put("logicCodeLinesJP", slocArray[3]);
				row.put("physicalCodeLinesJP", slocArray[4]);
				row.put("CommentLinesJP", slocArray[5]);
				row.put("percentageOfCommentJP", MathUtil.percentage(slocArray[5], slocArray[4] + slocArray[5]));
				rows.add(row);
			}
		}
		return rows;
	}

	@GetMapping("/table/subpackage")
	@ResponseBody
	public List<Map<String, Object>> selectSubPackageRecord(
			@RequestParam(name = "packageQualifiedName") String packageQualifiedName,
			@RequestParam(name = "projectRecordIndex", defaultValue = "-1") int projectRecordIndex) {
		List<Map<String, Object>> rows = new ArrayList<>();
		ProjectInfo projectInfo = ProjectRecordDao.getTwoProjectInfoRecordByIndex(projectRecordIndex)[0];
		PackageInfo packageInfo = EntityUtil.getPackageByQualifiedName(projectInfo.getPackageList(),
		                                                               packageQualifiedName);
		if (packageInfo == null) {
			LOGGER.info("No such package in this record.");
			return rows;
		}
		int[] slocArray;
		for (PackageInfo subPkgInfo : packageInfo.getSubPackageList()) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("name", subPkgInfo.getFullyQualifiedName());
			row.put("module", subPkgInfo.getModuleInfo().getRelativePath());
			row.put("level", "package");
			slocArray = subPkgInfo.getSlocArrayForCurrentAndSubPkg();
			row.put("logicCodeLinesSF", slocArray[0]);
			row.put("physicalCodeLinesSF", slocArray[1]);
			row.put("CommentLinesSF", slocArray[2]);
			row.put("percentageOfCommentSF", MathUtil.percentage(slocArray[2], slocArray[1] + slocArray[2]));
			row.put("logicCodeLinesJP", slocArray[3]);
			row.put("physicalCodeLinesJP", slocArray[4]);
			row.put("CommentLinesJP", slocArray[5]);
			row.put("percentageOfCommentJP", MathUtil.percentage(slocArray[5], slocArray[4] + slocArray[5]));
			rows.add(row);
		}
		Map<String, Object> row1 = new LinkedHashMap<>();
		row1.put("name", packageInfo.getFullyQualifiedName());
		row1.put("module", packageInfo.getModuleInfo().getRelativePath());
		row1.put("level", "current package");
		slocArray = packageInfo.getSlocArrayForCurrentPkg();
		row1.put("logicCodeLinesSF", slocArray[0]);
		row1.put("physicalCodeLinesSF", slocArray[1]);
		row1.put("CommentLinesSF", slocArray[2]);
		row1.put("percentageOfCommentSF", MathUtil.percentage(slocArray[2], slocArray[1] + slocArray[2]));
		row1.put("logicCodeLinesJP", slocArray[3]);
		row1.put("physicalCodeLinesJP", slocArray[4]);
		row1.put("CommentLinesJP", slocArray[5]);
		row1.put("percentageOfCommentJP", MathUtil.percentage(slocArray[5], slocArray[4] + slocArray[5]));
		rows.add(row1);
		for (ClassInfo classInfo : packageInfo.getClassList()) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("name", classInfo.getShortName());
			row.put("module", classInfo.getPackageInfo().getModuleInfo().getRelativePath());
			row.put("qualifiedName", classInfo.getFullyQualifiedName());
			row.put("level", "class");
			slocArray = classInfo.getSlocArray();
			row.put("logicCodeLinesSF", slocArray[0]);
			row.put("physicalCodeLinesSF", slocArray[1]);
			row.put("CommentLinesSF", slocArray[2]);
			row.put("percentageOfCommentSF", MathUtil.percentage(slocArray[2], slocArray[1] + slocArray[2]));
			row.put("logicCodeLinesJP", slocArray[3]);
			row.put("physicalCodeLinesJP", slocArray[4]);
			row.put("CommentLinesJP", slocArray[5]);
			row.put("percentageOfCommentJP", MathUtil.percentage(slocArray[5], slocArray[4] + slocArray[5]));
			rows.add(row);
		}
		return rows;
	}

	//TODO 应该再加一个展示方法的

	@GetMapping("/table/chart")
	@ResponseBody
	public List<String[]> selectTableChartRecord(
			@RequestParam(name = "name") String name, @RequestParam(name = "level") String level) {
		if ("module".equals(level)) {
			return moduleService.getSLOCTableChartDataset(name);
		} else if ("package".equals(level)) {
			return packageService.getSLOCTableChartDataset(name, true);
		} else if ("current package".equals(level)) {
			return packageService.getSLOCTableChartDataset(name, false);
		} else if ("class".equals(level)) {
			return classService.getSLOCTableChartDataset(name);
		} else {
			LOGGER.error("Unexpected type received [{}] where record name is [{}]", level, name);
			return null;
		}
	}

}