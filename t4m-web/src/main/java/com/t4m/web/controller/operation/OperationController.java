package com.t4m.web.controller.operation;

import com.t4m.conf.GlobalProperties;
import com.t4m.extractor.T4MExtractor;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.RegularExprUtil;
import com.t4m.extractor.util.TimeUtil;
import com.t4m.serializer.T4MProjectInfoSerializer;
import com.t4m.serializer.T4MSerializer;
import com.t4m.web.util.ProjectRecordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yuxiang Liao on 2020-07-03 02:19.
 */
@RestController
@RequestMapping("/operation")
public class OperationController {

	private static final Logger LOGGER = LoggerFactory.getLogger(OperationController.class);
	private static final String PARSE_EXCEPTION_ALERT =
			"If there are multi modules in your project, you might need to build your project at first. " +
					"Otherwise, T4M cannot find the jar file of the module on which other modules depends";

	@PostMapping("/new")
	public String createNewProject(
			@RequestParam(name = "projectPath") String projectPath,
			@RequestParam(name = "projectCreatTime") String projectCreatTime,
			@RequestParam(name = "excludedPath") String excludedPath,
			@RequestParam(name = "dependencyPath", defaultValue = "") String dependencyPath) {
		// 更新t4m.properties中的当前项目名称
		String[] paths = projectPath.split(RegularExprUtil.compatibleWithWindows("/"));
		String projectIdentifier = paths[paths.length - 1] + "#" + System.currentTimeMillis();
		// 更新全局项目指针
		GlobalProperties.updateCurrentProjectPointer(projectIdentifier);
		// 创建一条新的项目纪录
		Date createDate;
		if ("".equals(projectCreatTime)) {
			createDate = new Date();
		} else {
			try {
				createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(projectCreatTime);
			} catch (ParseException e) {
				LOGGER.error("Cannot parse {}, [{}]\n" + PARSE_EXCEPTION_ALERT, projectCreatTime, e.toString(), e);
				return PARSE_EXCEPTION_ALERT + "\nDetailed error: " + e.toString();
			}
		}

		ProjectInfo projectInfo = new ProjectInfo(new File(projectPath).getAbsolutePath(), excludedPath.strip(),
		                                          dependencyPath.strip(), createDate);
		try {
			// 扫描项目
			T4MExtractor t4MExtractor = new T4MExtractor();
			t4MExtractor.extract(projectInfo);
			// 持久化该记录
			T4MSerializer serializer = new T4MProjectInfoSerializer();
			String recordFileName = TimeUtil.formatToLogFileName(projectInfo.getCreateDate());
			serializer.serializeTo(projectInfo, recordFileName);
			// 更新本模块的 ProjectRecord
			ProjectRecordUtil.updateProjectInfoRecord();
		} catch (Exception e) {
			LOGGER.error("", e);
			return e.toString();
		}
		return "success";
	}

	@PostMapping("/scan")
	public String scanProject(
			@RequestParam(name = "projectCreatTime") String projectCreatTime,
			@RequestParam(name = "excludedPath") String excludedPath,
			@RequestParam(name = "dependencyPath", defaultValue = "") String dependencyPath) {
		List<ProjectInfo> projectInfoList = ProjectRecordUtil.getProjectInfoList();

		ProjectInfo oldProjectInfo = projectInfoList.get(ProjectRecordUtil.getProjectInfoList().size() - 1);
		// 创建一条新的项目纪录
		Date createDate;
		if ("".equals(projectCreatTime)) {
			createDate = new Date();
		} else {
			try {
				createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(projectCreatTime);
			} catch (ParseException e) {
				LOGGER.error("Cannot parse {}, [{}]\n" + PARSE_EXCEPTION_ALERT, projectCreatTime, e.toString(), e);
				return PARSE_EXCEPTION_ALERT + "\nDetailed error: " + e.toString();
			}
		}
		ProjectInfo projectInfo = new ProjectInfo(oldProjectInfo.getAbsolutePath(), excludedPath.strip(),
		                                          dependencyPath.strip(), createDate);
		try {
			// 扫描项目
			T4MExtractor t4MExtractor = new T4MExtractor();
			t4MExtractor.extract(projectInfo);
			// 持久化该记录
			T4MSerializer serializer = new T4MProjectInfoSerializer();
			String recordFileName = TimeUtil.formatToLogFileName(projectInfo.getCreateDate());
			serializer.serializeTo(projectInfo, recordFileName);
			// 更新ProjectRecord
			ProjectRecordUtil.updateProjectInfoRecord();
		} catch (Exception e) {
			LOGGER.error("", e);
			return e.toString();
		}
		return "success";
	}

	@GetMapping("search")
	public Map<String, Object> searchAllProject() {
		Map<String, Object> data = new HashMap<>();
		data.put("projectList", ProjectRecordUtil.getAllProjectRecordsDirName());
		data.put("currentProject", GlobalProperties.getCurrentProjectIdentifier());
		return data;
	}

	@GetMapping("/switch")
	public String switchProject(
			@RequestParam(name = "projectName") String projectName,
			@RequestParam(name = "projectId") String projectId) {
		// 更新全局项目指针和项目记录
		GlobalProperties.updateCurrentProjectPointer(projectName + "#" + projectId);
		ProjectRecordUtil.updateProjectInfoRecord();
		return "success";
	}

	@GetMapping("/delete")
	public String deleteProject(
			@RequestParam(name = "projectName") String projectName,
			@RequestParam(name = "projectId") String projectId) {
		T4MSerializer serializer = new T4MProjectInfoSerializer();
		serializer.delete(projectName + "#" + projectId);
		// 更新全局项目指针和项目记录
		ProjectRecordUtil.updateProjectInfoRecord();
		return "success";
	}

}
