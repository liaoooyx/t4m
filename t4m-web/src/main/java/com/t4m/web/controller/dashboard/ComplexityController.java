package com.t4m.web.controller.dashboard;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.EntityUtil;
import com.t4m.web.service.ClassService;
import com.t4m.web.service.MethodService;
import com.t4m.web.service.ProjectService;
import com.t4m.web.util.ProjectRecordUtil;
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
import java.util.List;
import java.util.Map;

/**
 * Created by Yuxiang Liao on 2020-07-21 23:23.
 */
@Controller
@RequestMapping("/dashboard/complexity")
public class ComplexityController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ComplexityController.class);

	@Resource(name = "ProjectService")
	private ProjectService projectService;

	@Resource(name = "ClassService")
	private ClassService classService;

	@Resource(name = "MethodService")
	private MethodService methodService;

	@GetMapping("")
	public String overview(Model model) {
		model.addAttribute("projectList", projectService.getAllProjectInfos());
		// for timeline chart
		model.addAttribute("timeRecords", projectService.getTimeRecords());
		model.addAttribute("weightedMethodsCountDataset", classService.getWeightedMethodCountForOverviewChart());
		model.addAttribute("maxComplexityOfClassDataset", classService.getMaxComplexityForOverviewChart());
		model.addAttribute("avgComplexityOfClassDataset", classService.getAvgComplexityForOverviewChart());
		model.addAttribute("responseForClassDataset", classService.getResponseForClassForOverviewChart());
		model.addAttribute("methodComplexityDataset", methodService.getMethodComplexityForOverviewChart());
		model.addAttribute("methodNumDataset", methodService.getNumberOfMethodForOverviewChart());
		return "page/dashboard/complexity_metric";
	}

	@GetMapping("/table/method")
	@ResponseBody
	public List<Map<String, Object>> selectMethodRecord(
			@RequestParam(name = "classQualifiedName") String classQualifiedName,
			@RequestParam(name = "projectRecordIndex", defaultValue = "-1") int projectRecordIndex) {
		ProjectInfo projectInfo = ProjectRecordUtil.getTwoProjectInfoRecordByIndex(projectRecordIndex)[0];
		ClassInfo classInfo = EntityUtil.getClassByQualifiedName(projectInfo.getAllClassList(), classQualifiedName);
		if (classInfo == null) {
			LOGGER.debug("No such class in this record.");
			return new ArrayList<>();
		}
		return methodService.getComplexityForTable(classInfo);
	}

	@GetMapping("/table/class")
	@ResponseBody
	public List<Map<String, Object>> selectClassRecord(
			@RequestParam(name = "projectRecordIndex", defaultValue = "-1") int projectRecordIndex) {
		ProjectInfo projectInfo = ProjectRecordUtil.getTwoProjectInfoRecordByIndex(projectRecordIndex)[0];
		return classService.getComplexityForTable(projectInfo);
	}

	@GetMapping("/table/chart/class")
	@ResponseBody
	public List<Object[]> selectTableChartRecordForClass(@RequestParam(name = "qualifiedName") String qualifiedName) {
		return classService.getComplexityForTableChart(qualifiedName);
	}

}
