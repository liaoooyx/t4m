package com.t4m.web.service;

import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.TimeUtil;
import com.t4m.web.util.ProjectRecord;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuxiang Liao on 2020-06-27 02:37.
 */
@Service("ProjectService")
public class ProjectService {

	/**
	 * 所有记录的创建时间
	 */
	public List<String> getTimeRecords() {
		List<String> timeRecords = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			timeRecords.add(TimeUtil.formatToStandardDatetime(projectInfo.getCreateDate()));
		}
		return timeRecords;
	}

	/**
	 * 所有记录的模块数量
	 */
	public List<Integer> getNumOfModuleRecords() {
		List<Integer> numOfModuleRecords = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			numOfModuleRecords.add(projectInfo.getModuleList().size());
		}
		return numOfModuleRecords;
	}

	/**
	 * 所有记录的包数量
	 */
	public List<Integer> getNumOfPackageRecords() {
		List<Integer> numOfPackageRecords = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			numOfPackageRecords.add(projectInfo.getPackageList().size());
		}
		return numOfPackageRecords;
	}

	/**
	 * 所有记录的类数量
	 */
	public List<Integer> getNumOfClassRecords() {
		List<Integer> numOfClassRecords = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			numOfClassRecords.add(projectInfo.getClassList().size());
		}
		return numOfClassRecords;
	}

	/**
	 * 所有记录的内部类和外部类数量
	 */
	public List<Integer> getNumOfClassAndInnerClassRecords() {
		List<Integer> numOfClassAndInnerClassRecords = new ArrayList<>();
		for (ProjectInfo projectInfo : ProjectRecord.getProjectInfoList()) {
			numOfClassAndInnerClassRecords.add(
					projectInfo.getClassList().size() + projectInfo.getInnerClassList().size());
		}
		return numOfClassAndInnerClassRecords;
	}
}
