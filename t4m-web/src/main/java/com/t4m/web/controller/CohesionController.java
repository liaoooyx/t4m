package com.t4m.web.controller;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.MethodInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.EntityUtil;
import com.t4m.extractor.util.TimeUtil;
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
@RequestMapping("/dashboard/cohesion")
public class CohesionController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CohesionController.class);

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
		LinkedHashMap<String, List<List<Object>>> weightedMethodsCountDataset = new LinkedHashMap<>();
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			List<List<Object>> rows = new ArrayList<>();
			for (ClassInfo classInfo : projectInfo.getAllClassList()) {
				List<Object> cols = new ArrayList<>();
				cols.add(classInfo.getNumberOfMethods()); // code line
				cols.add(classInfo.getWeightedMethodsCount()); // code line
				cols.add(classInfo.getFullyQualifiedName()); // class qualified name
				cols.add(classInfo.getPackageInfo().getModuleInfo().getRelativePath()); // of which module
				rows.add(cols);
			}
			weightedMethodsCountDataset.put(time, rows);
		}
		model.addAttribute("weightedMethodsCountDataset", weightedMethodsCountDataset);

		LinkedHashMap<String, List<List<Object>>> maxComplexityOfClassDataset = new LinkedHashMap<>();
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			List<List<Object>> rows = new ArrayList<>();
			for (ClassInfo classInfo : projectInfo.getAllClassList()) {
				List<Object> cols = new ArrayList<>();
				cols.add(classInfo.getNumberOfMethods()); // code line
				cols.add(classInfo.getMaxCyclomaticComplexity()); // code line
				cols.add(classInfo.getFullyQualifiedName()); // class qualified name
				cols.add(classInfo.getPackageInfo().getModuleInfo().getRelativePath()); // of which module
				rows.add(cols);
			}
			maxComplexityOfClassDataset.put(time, rows);
		}
		model.addAttribute("maxComplexityOfClassDataset", maxComplexityOfClassDataset);

		LinkedHashMap<String, List<List<Object>>> avgComplexityOfClassDataset = new LinkedHashMap<>();
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			List<List<Object>> rows = new ArrayList<>();
			for (ClassInfo classInfo : projectInfo.getAllClassList()) {
				List<Object> cols = new ArrayList<>();
				cols.add(classInfo.getNumberOfMethods());
				cols.add(classInfo.getAvgCyclomaticComplexity());
				cols.add(classInfo.getFullyQualifiedName()); // class qualified name
				cols.add(classInfo.getPackageInfo().getModuleInfo().getRelativePath()); // of which module
				rows.add(cols);
			}
			avgComplexityOfClassDataset.put(time, rows);
		}
		model.addAttribute("avgComplexityOfClassDataset", avgComplexityOfClassDataset);

		LinkedHashMap<String, List<List<Object>>> responseForClassDataset = new LinkedHashMap<>();
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			List<List<Object>> rows = new ArrayList<>();
			for (ClassInfo classInfo : projectInfo.getAllClassList()) {
				List<Object> cols = new ArrayList<>();
				cols.add(classInfo.getNumberOfMethods());
				cols.add(classInfo.getResponseForClass());
				cols.add(classInfo.getFullyQualifiedName()); // class qualified name
				cols.add(classInfo.getPackageInfo().getModuleInfo().getRelativePath()); // of which module
				rows.add(cols);
			}
			responseForClassDataset.put(time, rows);
		}
		model.addAttribute("responseForClassDataset", responseForClassDataset);


		List<Object[]> methodComplexityDataset = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			for (MethodInfo methodInfo : projectInfo.getMethodList()) {
				Object[] row =
						new Object[]{time, methodInfo.getCyclomaticComplexity(), methodInfo.getFullyQualifiedName(),
						             methodInfo.getMethodDeclarationString()};
				methodComplexityDataset.add(row);
			}
		}
		model.addAttribute("methodComplexityDataset", methodComplexityDataset);

		List<Object[]> methodNumDataset = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			Object[] row = new Object[]{time, projectInfo.getMethodList().size()};
			methodNumDataset.add(row);
		}
		model.addAttribute("methodNumDataset", methodNumDataset);

		return "page/dashboard/cohesion_metric";
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
			row.put("lackOfCohesionInMethods4", classInfo.getLackOfCohesionInMethods4());
			row.put("tightClassCohesion", classInfo.getTightClassCohesion());
			row.put("looseClassCohesion", classInfo.getLooseClassCohesion());
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
		dataset.add(
				new String[]{"time", "Lack of Cohesion in Methods 4", "Tight Class Cohesion", "Loose Class Cohesion"});
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(), qualifiedName);
			Object[] tempRow = new Object[4];
			tempRow[0] = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			Arrays.fill(tempRow, 1, 3, null);
			if (classInfo != null) { //类可能还未创建或已经删除
				tempRow[1] = classInfo.getLackOfCohesionInMethods4();
				tempRow[2] = classInfo.getTightClassCohesion();
				tempRow[3] = classInfo.getLooseClassCohesion();
			}
			dataset.add(tempRow);
		}
		return dataset;
	}

}