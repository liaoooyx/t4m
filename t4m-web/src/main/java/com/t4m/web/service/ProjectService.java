package com.t4m.web.service;

import com.t4m.conf.GlobalProperties;
import com.t4m.extractor.entity.ModuleInfo;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.EntityUtil;
import com.t4m.extractor.util.TimeUtil;
import com.t4m.web.util.ProjectRecordUtil;
import com.t4m.web.util.dataset.SLOCDatasetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yuxiang Liao on 2020-06-27 02:37.
 */
@Service("ProjectService")
public class ProjectService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectService.class);


	public ProjectInfo getCurrentProjectInfoOfIndex(int index) {
		ProjectInfo[] projectInfos = ProjectRecordUtil.getTwoProjectInfoRecordByIndex(index);
		if (projectInfos[0] == null) {
			LOGGER.debug("No record for [{}] in index [{}]. If you modified any entity class, please check them.\n " +
					             "It usually caused by NullPointerException when derializing from the object files",
			             GlobalProperties.getCurrentProjectIdentifier(), index);
		}
		return projectInfos[0];
	}

	public ProjectInfo getPreviousProjectInfoOfIndex(int index) {
		ProjectInfo[] projectInfos = ProjectRecordUtil.getTwoProjectInfoRecordByIndex(index);
		if (projectInfos[1] == null) {
			LOGGER.debug("No previous record for [{}] in index [{}]", GlobalProperties.getCurrentProjectIdentifier(),
			             index);
		}
		return projectInfos[1];
	}

	public List<ProjectInfo> getAllProjectInfos() {
		return ProjectRecordUtil.getProjectInfoList();
	}

	public List<String> getTimeRecords() {
		List<String> timeRecords = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			timeRecords.add(TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate()));
		}
		return timeRecords;
	}

	public List<Integer> getNumOfModuleForOverviewChart() {
		List<Integer> numOfModuleRecords = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			numOfModuleRecords.add(projectInfo.getModuleList().size());
		}
		return numOfModuleRecords;
	}

	public List<Integer> getNumOfPackageForOverviewChart() {
		List<Integer> numOfPackageRecords = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			numOfPackageRecords.add(projectInfo.getPackageList().size());
		}
		return numOfPackageRecords;
	}

	public List<Integer> getNumOfJavaFileForOverviewChart() {
		List<Integer> numOfClassRecords = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			numOfClassRecords.add(projectInfo.getClassList().size());
		}
		return numOfClassRecords;
	}

	public List<Integer> getNumOfClassForOverviewChart() {
		List<Integer> numOfClassRecords = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			numOfClassRecords.add(projectInfo.getClassList().size() + projectInfo.getExtraClassList().size());
		}
		return numOfClassRecords;
	}

	public List<Integer> getNumOfAllClassForOverviewChart() {
		List<Integer> numOfClassAndInnerClassRecords = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			numOfClassAndInnerClassRecords.add(projectInfo.getAllClassList().size());
		}
		return numOfClassAndInnerClassRecords;
	}


	public List<Map<String, Object>> getSLOCForTable(ProjectInfo projectInfo) {
		List<Map<String, Object>> rows = new ArrayList<>();
		Map<String, Object> row = new LinkedHashMap<>();
		row.put("name", projectInfo.getProjectDirName());
		row.put("level", "project");
		int[] slocArray = new int[8];
		for (ModuleInfo moduleInfo : projectInfo.getModuleList()) {
			SLOCDatasetUtil.sumSLOC(slocArray, moduleInfo.getSlocArray());
		}
		SLOCDatasetUtil.insertCommonRowsForTable(row, slocArray);
		rows.add(row);
		return rows;
	}

	public List<String[]> getSLOCForTableChart() {
		List<String[]> dataset = new ArrayList<>();
		dataset.add(new String[]{"time", "Source Code Lines (Source File)", "Comment Lines",
		                         "% of Comment Lines (Source File)", "Source Code Lines (JavaParser)",
		                         "Comment Lines (JavaParser)", "% of Comment Lines (JavaParser)"});
		for (ProjectInfo projectInfo : ProjectRecordUtil.getProjectInfoList()) {
			String[] tempRow;
			int[] slocArray = new int[8];
			for (ModuleInfo moduleInfo : projectInfo.getModuleList()) {
				SLOCDatasetUtil.sumSLOC(slocArray, moduleInfo.getSlocArray());
			}
			tempRow = SLOCDatasetUtil.formatRowForTableChart(projectInfo.getCreateDate(), slocArray);
			dataset.add(tempRow);
		}
		return dataset;
	}

}
