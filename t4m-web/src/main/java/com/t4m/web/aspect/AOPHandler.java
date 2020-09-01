package com.t4m.web.aspect;

import com.t4m.conf.GlobalProperties;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.web.service.ProjectService;
import com.t4m.web.util.ProjectRecordUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.annotation.Resource;

/**
 * Created by Yuxiang Liao on 2020-07-03 07:24.
 */
@Component
@Aspect
public class AOPHandler {

	@Resource(name = "ProjectService")
	private ProjectService projectService;

	@Pointcut("execution(public * com.t4m.web.controller.dashboard.*.*(org.springframework.ui.Model))")
	public void dashboardController() {
	}

	@Pointcut("execution(public * com.t4m.web.controller.document.*.*(org.springframework.ui.Model))")
	public void documentController() {
	}

	@Pointcut("execution(public * com.t4m.web.controller.HomeController.*(org.springframework.ui.Model))")
	public void homeController() {
	}

	@Pointcut("execution(public * com.t4m.web.controller.operation.*.*(org.springframework.ui.Model))")
	public void operationController() {
	}

	@Around("dashboardController() || operationController()")
	public Object checkEmptyRecordList(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		ProjectInfo projectInfo = projectService.getCurrentProjectInfoOfIndex(-1);
		Model model = (Model) proceedingJoinPoint.getArgs()[0];
		addDefaultConfToModel(model);
		if (projectInfo == null || "".equals(GlobalProperties.getCurrentProjectIdentifier())) {
			model.addAttribute("disableScan", true);
			if (ProjectRecordUtil.getAllProjectRecordsDirName().isEmpty()) {
				model.addAttribute("triggerBtn", "new");
			}else {
				model.addAttribute("triggerBtn", "switch");
			}
			return "page/dashboard/blank_page";
		} else {
			ProjectRecordUtil.checkCurrentProjectIdentifier();
			addProjectConfToModel(model, projectInfo);
			return proceedingJoinPoint.proceed();
		}
	}

	@Around("documentController() || homeController()")
	public Object disableScanButton(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		ProjectInfo projectInfo = projectService.getCurrentProjectInfoOfIndex(-1);
		Model model = (Model) proceedingJoinPoint.getArgs()[0];
		addDefaultConfToModel(model);
		if (projectInfo == null || "".equals(GlobalProperties.getCurrentProjectIdentifier())) {
			model.addAttribute("disableScan", true);
		} else {
			ProjectRecordUtil.checkCurrentProjectIdentifier();
			addProjectConfToModel(model, projectInfo);
		}
		return proceedingJoinPoint.proceed();
	}

	private void addDefaultConfToModel(Model model) {
		model.addAttribute("defaultExcludedPath", GlobalProperties.DEFAULT_EXCLUDED_PATH);
		model.addAttribute("defaultDependencyPath", GlobalProperties.DEFAULT_DEPENDENCY_PATH);
	}

	private void addProjectConfToModel(Model model, ProjectInfo projectInfo) {
		model.addAttribute("currentProjectIdentifier", GlobalProperties.getCurrentProjectIdentifier());
		model.addAttribute("currentProjectPath", projectInfo.getAbsolutePath());
		model.addAttribute("projectExcludedPath", projectInfo.getExcludedPath());
		model.addAttribute("projectDependencyPath", projectInfo.getDependencyPath());
	}
}
