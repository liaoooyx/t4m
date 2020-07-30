package com.t4m.web.controller;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ProjectInfo;
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
@RequestMapping("/dashboard/inheritance")
public class InheritanceController {

	private static final Logger LOGGER = LoggerFactory.getLogger(InheritanceController.class);

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

		List<Object[]> deepOfInheritanceTreeDataset = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecordDao.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			for (ClassInfo classInfo : projectInfo.getAllClassList()) {
				Object[] row =
						new Object[]{time, classInfo.getDeepOfInheritanceTree(), classInfo.getFullyQualifiedName(),
						             classInfo.getPackageInfo().getModuleInfo().getRelativePath()};
				deepOfInheritanceTreeDataset.add(row);
			}
		}
		model.addAttribute("deepOfInheritanceTreeDataset", deepOfInheritanceTreeDataset);

		List<Object[]> numberOfChildrenDataset = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecordDao.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			for (ClassInfo classInfo : projectInfo.getAllClassList()) {
				Object[] row =
						new Object[]{time, classInfo.getNumberOfChildren(), classInfo.getFullyQualifiedName(),
						             classInfo.getPackageInfo().getModuleInfo().getRelativePath()};
				numberOfChildrenDataset.add(row);
			}
		}
		model.addAttribute("numberOfChildrenDataset", numberOfChildrenDataset);

		return "page/dashboard/inheritance_metric";
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
			row.put("deepOfInheritanceTree", classInfo.getDeepOfInheritanceTree());
			row.put("numberOfChildren", classInfo.getNumberOfChildren());
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
		dataset.add(new String[]{"time", "Deep of Inheritance Tree", "Number of Children"});
		for (ProjectInfo projectInfo : ProjectRecordDao.getProjectInfoList()) {
			ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(), qualifiedName);
			Object[] tempRow = new Object[3];
			tempRow[0] = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			Arrays.fill(tempRow, 1, 2, null);
			if (classInfo != null) { //类可能还未创建或已经删除
				tempRow[1] = classInfo.getDeepOfInheritanceTree();
				tempRow[2] = classInfo.getNumberOfChildren();
			}
			dataset.add(tempRow);
		}
		return dataset;
	}

}
