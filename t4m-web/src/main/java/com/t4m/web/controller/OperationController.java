package com.t4m.web.controller;

import com.t4m.extractor.T4MExtractor;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.PropertyUtil;
import com.t4m.extractor.util.TimeUtil;
import com.t4m.serializer.T4MProjectInfoSerializer;
import com.t4m.serializer.T4MSerializer;
import com.t4m.web.util.GlobalVariable;
import com.t4m.web.util.ProjectRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yuxiang Liao on 2020-07-03 02:19.
 */
@RestController
@RequestMapping("/operation")
public class OperationController {

	private static final Logger LOGGER = LoggerFactory.getLogger(OperationController.class);

	@GetMapping("/new")
	public String createNewProject(@RequestParam(name = "path") String projectPath) {
		// 更新t4m.properties中的当前项目名称
		String[] paths = projectPath.split(File.separator);
		String projectName = paths[paths.length - 1];
		// 更新全局项目指针
		GlobalVariable.updateCurrentProjectName(projectName);
		// 创建一条新的项目纪录
		ProjectInfo projectInfo = new ProjectInfo(new File(projectPath).getAbsolutePath());
		// 扫描项目
		T4MExtractor t4MExtractor = new T4MExtractor(projectInfo);
		t4MExtractor.action();
		// 持久化该记录
		T4MSerializer serializer = new T4MProjectInfoSerializer();
		String recordFileName = TimeUtil.formatToLogFileName(projectInfo.getCreateDate());
		serializer.serializeTo(projectInfo, recordFileName);
		// 更新ProjectRecord
		ProjectRecord.updateProjectInfoRecord();
		return "success";
	}

	@GetMapping("search")
	public Map<String, Object> searchAllProject() {
		Map<String, Object> data = new HashMap<>();
		data.put("projectList", ProjectRecord.getAllProjectRecordsDirName());
		data.put("currentProject", PropertyUtil.getProperty("CURRENT_PROJECT_NAME"));
		return data;
	}

	@GetMapping("/switch/{projectName}")
	public String switchProject(@PathVariable(name = "projectName") String projectName) {
		System.out.println(projectName);
		// 更新全局项目指针和项目记录
		GlobalVariable.updateCurrentProjectName(projectName);
		ProjectRecord.updateProjectInfoRecord();
		return "success";
	}

	@GetMapping("/scan")
	public String scanProject() {

		ProjectInfo oldProjectInfo = ProjectRecord.getProjectInfoList().get(
				ProjectRecord.getProjectInfoList().size() - 1);
		// 创建一条新的项目纪录
		ProjectInfo projectInfo = new ProjectInfo(oldProjectInfo.getAbsolutePath());
		// 扫描项目
		T4MExtractor t4MExtractor = new T4MExtractor(projectInfo);
		t4MExtractor.action();
		// 持久化该记录
		T4MSerializer serializer = new T4MProjectInfoSerializer();
		String recordFileName = TimeUtil.formatToLogFileName(projectInfo.getCreateDate());
		serializer.serializeTo(projectInfo, recordFileName);
		// 更新ProjectRecord
		ProjectRecord.updateProjectInfoRecord();
		return "success";
	}

}
