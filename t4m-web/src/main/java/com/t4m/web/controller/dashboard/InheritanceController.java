package com.t4m.web.controller.dashboard;

import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.web.service.ClassService;
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
@RequestMapping("/dashboard/inheritance")
public class InheritanceController {

	@Resource(name = "ProjectService")
	private ProjectService projectService;

	@Resource(name = "ClassService")
	private ClassService classService;

	@GetMapping("")
	public String overview(Model model) {
		model.addAttribute("projectList", projectService.getAllProjectInfos());
		// for timeline chart
		model.addAttribute("timeRecords", projectService.getTimeRecords());
		model.addAttribute("deepOfInheritanceTreeDataset", classService.getDeepOfInheritanceTreeForOverviewChart());
		model.addAttribute("numberOfChildrenDataset", classService.getNumberOfChildForOverviewChart());
		return "page/dashboard/inheritance_metric";
	}

	@GetMapping("/table/class")
	@ResponseBody
	public List<Map<String, Object>> selectClassRecord(
			@RequestParam(name = "projectRecordIndex", defaultValue = "-1") int projectRecordIndex) {
		ProjectInfo projectInfo = projectService.getCurrentProjectInfoOfIndex(projectRecordIndex);
		if (projectInfo == null) {
			return new ArrayList<>();
		}
		return classService.getInheritanceForTable(projectInfo);
	}

	@GetMapping("/table/chart/class")
	@ResponseBody
	public List<Object[]> selectTableChartRecordForClass(@RequestParam(name = "qualifiedName") String qualifiedName) {
		return classService.getInheritanceForTableChart(qualifiedName);
	}

}
