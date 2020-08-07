package com.t4m.web.controller;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.EntityUtil;
import com.t4m.extractor.util.MathUtil;
import com.t4m.extractor.util.TimeUtil;
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
		List<ProjectInfo> projectInfoList = ProjectRecordDao.getProjectInfoList();
		// 基本信息
		model.addAttribute("projectList", projectInfoList);
		// 用于timeline chart
		model.addAttribute("timeRecords", projectService.getTimeRecords());
		LinkedHashMap<String, List<List<Object>>> packageInstabilityAbstractnessGraphDataset = new LinkedHashMap<>();
		for (ProjectInfo projectInfo : ProjectRecordDao.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			List<List<Object>> rows = new ArrayList<>();
			for (PackageInfo packageInfo : projectInfo.getPackageList()) {
				List<Object> cols = new ArrayList<>();
				float instability = Float.parseFloat(packageInfo.getInstability());
				float abstractness = Float.parseFloat(packageInfo.getAbstractness());
				cols.add(instability);
				cols.add(abstractness);
				cols.add(MathUtil.abs(instability + abstractness - 1));
				cols.add(packageInfo.getFullyQualifiedName());
				cols.add(packageInfo.getModuleInfo().getRelativePath());
				rows.add(cols);
			}
			packageInstabilityAbstractnessGraphDataset.put(time, rows);
		}
		model.addAttribute("packageInstabilityAbstractnessGraphDataset", packageInstabilityAbstractnessGraphDataset);

		List<Object[]> classCouplingBetweenObjectsDataset = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecordDao.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			for (ClassInfo classInfo : projectInfo.getAllClassList()) {
				Object[] row =
						new Object[]{time, classInfo.getCouplingBetweenObjects(), classInfo.getFullyQualifiedName(),
						             classInfo.getPackageInfo().getModuleInfo().getRelativePath()};
				classCouplingBetweenObjectsDataset.add(row);
			}
		}
		model.addAttribute("classCouplingBetweenObjectsDataset", classCouplingBetweenObjectsDataset);

		List<Object[]> classMessagePassingCouplingDataset = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecordDao.getProjectInfoList()) {
			String time = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			for (ClassInfo classInfo : projectInfo.getAllClassList()) {
				Object[] row =
						new Object[]{time, classInfo.getMessagePassingCoupling(), classInfo.getFullyQualifiedName(),
						             classInfo.getPackageInfo().getModuleInfo().getRelativePath()};
				classMessagePassingCouplingDataset.add(row);
			}
		}
		model.addAttribute("classMessagePassingCouplingDataset", classMessagePassingCouplingDataset);

		return "page/dashboard/coupling_metric";
	}

	@GetMapping("/table/package")
	@ResponseBody
	public List<Map<String, Object>> selectPackageRecord(
			@RequestParam(name = "projectRecordIndex", defaultValue = "-1") int projectRecordIndex) {
		List<Map<String, Object>> rows = new ArrayList<>();
		ProjectInfo projectInfo = ProjectRecordDao.getTwoProjectInfoRecordByIndex(projectRecordIndex)[0];
		for (PackageInfo packageInfo : projectInfo.getPackageList()) {
			Map<String, Object> row = new LinkedHashMap<>();
			row.put("name", packageInfo.getFullyQualifiedName());
			row.put("module", packageInfo.getModuleInfo().getShortName());
			row.put("afferentCoupling", packageInfo.getAfferentCoupling());
			row.put("efferentCoupling", packageInfo.getEfferentCoupling());
			row.put("instability", packageInfo.getInstability());
			row.put("abstractness", packageInfo.getAbstractness());
			float instability = Float.parseFloat(packageInfo.getInstability());
			float abstractness = Float.parseFloat(packageInfo.getAbstractness());
			row.put("distance", MathUtil.abs(instability + abstractness - 1));
			rows.add(row);
		}
		return rows;
	}

	@GetMapping("/table/chart/package")
	@ResponseBody
	public List<Object[]> selectTableChartRecordForPackage(@RequestParam(name = "qualifiedName") String qualifiedName) {
		//	第一行是系列名，从第二行开始，每一行是一条记录的数据，其中第一列是时间
		List<Object[]> dataset = new ArrayList<>();
		dataset.add(new String[]{"time", "Afferent Coupling", "Efferent Coupling", "Instability", "Abstractness",
		                         "Distance from Main Sequence"});
		for (ProjectInfo projectInfo : ProjectRecordDao.getProjectInfoList()) {
			PackageInfo packageInfo = EntityUtil.getPackageByQualifiedName(projectInfo.getPackageList(), qualifiedName);
			Object[] tempRow = new Object[6];
			tempRow[0] = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			Arrays.fill(tempRow, 1, 4, null);
			if (packageInfo != null) { //类可能还未创建或已经删除
				tempRow[1] = packageInfo.getAfferentCoupling();
				tempRow[2] = packageInfo.getEfferentCoupling();
				tempRow[3] = packageInfo.getInstability();
				tempRow[4] = packageInfo.getAbstractness();
				float instability = Float.parseFloat(packageInfo.getInstability());
				float abstractness = Float.parseFloat(packageInfo.getAbstractness());
				tempRow[5] = MathUtil.abs(instability + abstractness - 1);
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
		ProjectInfo projectInfo = ProjectRecordDao.getTwoProjectInfoRecordByIndex(projectRecordIndex)[0];
		PackageInfo packageInfo = EntityUtil.getPackageByQualifiedName(projectInfo.getPackageList(), pkgQualifiedName);
		if (packageInfo == null) {
			LOGGER.info("No such package in this record.");
			return rows;
		}
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
		for (ProjectInfo projectInfo : ProjectRecordDao.getProjectInfoList()) {
			ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(), qualifiedName);
			Object[] tempRow = new Object[6];
			tempRow[0] = TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate());
			Arrays.fill(tempRow, 1, 5, null);
			if (classInfo != null) { //类可能还未创建或已经删除
				tempRow[1] = classInfo.getMessagePassingCoupling();
				tempRow[2] = classInfo.getCouplingBetweenObjects();
				tempRow[3] = classInfo.getAfferentCoupling();
				tempRow[4] = classInfo.getEfferentCoupling();
				tempRow[5] = classInfo.getInstability();
			}
			dataset.add(tempRow);
		}
		return dataset;
	}

}
