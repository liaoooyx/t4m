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
@RequestMapping("/dashboard/coupling")
public class CouplingController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CouplingController.class);

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

		return "page/dashboard/coupling_metric";
	}

	@GetMapping("/table/package")
	@ResponseBody
	public List<Map<String, Object>> selectPackageRecord(
			@RequestParam(name = "projectRecordIndex", defaultValue = "-1") int projectRecordIndex) {
		List<Map<String, Object>> rows = new ArrayList<>();
		ProjectInfo projectInfo = ProjectRecord.getTwoProjectInfoRecordByIndex(projectRecordIndex)[0];
		for (PackageInfo packageInfo: projectInfo.getPackageList()) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("name", packageInfo.getFullyQualifiedName());
			row.put("module", packageInfo.getModuleInfo().getShortName());
			row.put("afferentCoupling", packageInfo.getAfferentCoupling());
			row.put("efferentCoupling", packageInfo.getEfferentCoupling());
			row.put("instability", packageInfo.getInstability());
			row.put("abstractness", packageInfo.getAbstractness());
			rows.add(row);
		}
		return rows;
	}

	@GetMapping("/table/chart/package")
	@ResponseBody
	public List<Object[]> selectTableChartRecordForPackage(@RequestParam(name = "qualifiedName") String qualifiedName) {

		//	第一行是系列名，从第二行开始，每一行是一条记录的数据，其中第一列是时间
		List<Object[]> dataset = new ArrayList<>();
		dataset.add(new String[]{"time", "Afferent Coupling", "Efferent Coupling", "Instability", "Abstractness"});
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			PackageInfo packageInfo = EntityUtil.getPackageByQualifiedName(projectInfo.getPackageList(), qualifiedName);
			Object[] tempRow = new Object[6];
			tempRow[0] = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			Arrays.fill(tempRow, 1, 5, null);
			if (packageInfo != null) { //类可能还未创建或已经删除
				tempRow[1] = packageInfo.getAfferentCoupling();
				tempRow[2] = packageInfo.getEfferentCoupling();
				tempRow[3] = packageInfo.getInstability();
				tempRow[4] = packageInfo.getAbstractness();
			}
			dataset.add(tempRow);
		}
		return dataset;
	}

	@GetMapping("/table/class")
	@ResponseBody
	public List<Map<String, Object>> selectClassRecord(
			@RequestParam(name = "pkgQualifiedName") String pkgQualifiedName,
			@RequestParam(name = "projectRecordIndex", defaultValue = "-1") int projectRecordIndex) {
		List<Map<String, Object>> rows = new ArrayList<>();
		ProjectInfo projectInfo = ProjectRecord.getTwoProjectInfoRecordByIndex(projectRecordIndex)[0];
		PackageInfo packageInfo = EntityUtil.getPackageByQualifiedName(projectInfo.getPackageList(), pkgQualifiedName);
		for (ClassInfo classInfo : packageInfo.getAllClassList()) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("name", classInfo.getShortName());
			row.put("type", classInfo.getClassModifier().toString());
			row.put("declaration", classInfo.getClassDeclaration().toString());
			row.put("package", classInfo.getPackageFullyQualifiedName());
			row.put("module", classInfo.getPackageInfo().getModuleInfo().getShortName());
			row.put("couplingBetweenObjects", classInfo.getCouplingBetweenObjects());
			row.put("afferentCoupling", classInfo.getAfferentCoupling());
			row.put("efferentCoupling", classInfo.getEfferentCoupling());
			row.put("instability", classInfo.getInstability());
			row.put("messagePassingCoupling", classInfo.getMessagePassingCoupling());
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
		dataset.add(new String[]{"time", "Coupling Between Objects", "Afferent Coupling", "Efferent Coupling",
		                         "Instability", "Message Passing Coupling"});
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(), qualifiedName);
			Object[] tempRow = new Object[6];
			tempRow[0] = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			Arrays.fill(tempRow, 1, 5, null);
			if (classInfo != null) { //类可能还未创建或已经删除
				tempRow[1] = classInfo.getCouplingBetweenObjects();
				tempRow[2] = classInfo.getAfferentCoupling();
				tempRow[3] = classInfo.getEfferentCoupling();
				tempRow[4] = classInfo.getInstability();
				tempRow[5] = classInfo.getMessagePassingCoupling();
			}
			dataset.add(tempRow);
		}
		return dataset;
	}

}
