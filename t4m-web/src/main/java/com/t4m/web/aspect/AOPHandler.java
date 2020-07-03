package com.t4m.web.aspect;

import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.web.util.ProjectRecord;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by Yuxiang Liao on 2020-07-03 07:24.
 */
@Component
@Aspect
public class AOPHandler {

	@Pointcut("execution(public * com.t4m.web.controller.DashboardController.*(org.springframework.ui.Model))")
	public void dashboardController() {
	}

	@Around("dashboardController()")
	public Object checkEmptyRecordList(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		ProjectInfo[] projectInfos = ProjectRecord.getProjectInfoRecordByIndex(-1);
		Model model = (Model) proceedingJoinPoint.getArgs()[0];
		if (projectInfos == null) {
			model.addAttribute("isBlank", true);
			return "page/dashboard/blank_page";
		} else {
			return proceedingJoinPoint.proceed();
		}
	}
}
