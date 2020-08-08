package com.t4m.web.controller.dashboard;

import com.t4m.conf.GlobalProperties;
import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.PackageInfo;
import com.t4m.extractor.entity.ProjectInfo;
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
		ProjectInfo[] projectInfos = ProjectRecordDao.getTwoProjectInfoRecordByIndex(-1);
		// 基本信息
		model.addAttribute("currentProjectInfo", projectInfos[0]);
		model.addAttribute("preProjectInfo", projectInfos[1]);
		model.addAttribute("currentProjectIdentifier", GlobalProperties.getCurrentProjectIdentifier());
		// 用于趋势图
		model.addAttribute("timeRecords", projectService.getTimeRecords());
		model.addAttribute("moduleRecords", projectService.getNumOfModuleRecords());
		model.addAttribute("packageRecords", projectService.getNumOfPackageRecords());
		model.addAttribute("javaFileRecords", projectService.getNumOfJavaFileRecords());
		model.addAttribute("classRecords", projectService.getNumOfClassRecords());
		model.addAttribute("allClassRecords", projectService.getNumOfAllClassRecords());
		return "page/dashboard/basic_metric";
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
			row.put("packageNum", moduleInfo.getPackageList().size());
			row.put("classNum", moduleInfo.getNumberOfJavaFile() + " / " + moduleInfo.getNumberOfAllClass());
			rows.add(row);
		}
		return rows;
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
			row.put("classNum", packageInfo.getNumberOfJavaFile() + " / " + packageInfo.getNumberOfAllClass());
			row.put("module", packageInfo.getModuleInfo().getShortName());
			rows.add(row);
		}
		return rows;
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
			row.put("fieldNum", classInfo.getNumberOfFields());
			row.put("methodNum", classInfo.getNumberOfMethods());
			row.put("enumConstantsNum", classInfo.getNumberOfEnumConstants());
			row.put("annotationMembers", classInfo.getNumberOfAnnotationMembers());
			row.put("qualifiedName", classInfo.getFullyQualifiedName());
			rows.add(row);
		}
		return rows;
	}

}
