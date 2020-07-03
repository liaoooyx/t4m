package com.t4m.web.controller;

import com.t4m.extractor.T4MExtractor;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.TimeUtil;
import com.t4m.serializer.T4MProjectInfoSerializer;
import com.t4m.serializer.T4MSerializer;
import com.t4m.web.util.GlobalVariable;
import com.t4m.web.util.ProjectRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * Created by Yuxiang Liao on 2020-07-03 02:19.
 */
@RestController
@RequestMapping("/operation")
public class OperationController {

	private static final Logger LOGGER = LoggerFactory.getLogger(OperationController.class);

	@GetMapping("/new")
	public String createNewProject(@RequestParam(name = "path") String projectPath) {
		System.out.println(projectPath);
		// 更新t4m.properties中的当前项目名称
		String[] paths = projectPath.split(File.separator);
		String projectName = paths[paths.length - 1];
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
}
