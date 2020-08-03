package com.t4m.web.aspect;

import com.t4m.conf.GlobalProperties;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.web.dao.ProjectRecordDao;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

/**
 * Created by Yuxiang Liao on 2020-07-03 07:24.
 */
@Component
@Aspect
public class AOPHandler {

	@Pointcut("execution(public * com.t4m.web.controller.*.*(org.springframework.ui.Model))")
	public void dashboardController() {
	}

	@Around("dashboardController()")
	public Object checkEmptyRecordList(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		ProjectInfo[] projectInfos = ProjectRecordDao.getTwoProjectInfoRecordByIndex(-1);
		Model model = (Model) proceedingJoinPoint.getArgs()[0];
		if (projectInfos == null) {
			model.addAttribute("isBlank", true);
			model.addAttribute("defaultExcludedPath", GlobalProperties.DEFAULT_EXCLUDED_PATH);
			model.addAttribute("defaultDependencyPath", GlobalProperties.DEFAULT_DEPENDENCY_PATH);
			return "page/dashboard/blank_page";
		} else {
			ProjectRecordDao.checkCurrentProjectIdentifier();
			model.addAttribute("currentProjectIdentifier", GlobalProperties.CURRENT_PROJECT_IDENTIFIER);
			model.addAttribute("currentProjectPath", projectInfos[0].getAbsolutePath());
			model.addAttribute("projectExcludedPath", projectInfos[0].getExcludedPath());
			model.addAttribute("projectDependencyPath", projectInfos[0].getDependencyPath());
			model.addAttribute("defaultExcludedPath", GlobalProperties.DEFAULT_EXCLUDED_PATH);
			model.addAttribute("defaultDependencyPath", GlobalProperties.DEFAULT_DEPENDENCY_PATH);
			return proceedingJoinPoint.proceed();
		}
	}
}
