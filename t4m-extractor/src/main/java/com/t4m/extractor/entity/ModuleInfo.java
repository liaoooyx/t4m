package com.t4m.extractor.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Yuxiang Liao on 2020-06-11 09:29.
 */
public class ModuleInfo implements Serializable {

	private static final long serialVersionUID = 4453849096224682890L;
	private String shortName;
	private String relativePath; // Path that start from the project root dir.
	private String absolutePath;

	private List<ModuleInfo> subModuleList = new ArrayList<>();
	private ModuleInfo previousModuleInfo;

	private List<PackageInfo> mainPackageList = new ArrayList<>();
	private List<PackageInfo> testPackageList = new ArrayList<>();
	private List<PackageInfo> otherPackageList = new ArrayList<>();

	private String mainScopePath;
	private String testScopePath;
	private String otherScopePath;

	private int numberOfJavaFile;
	private int numberOfAllClass;
	/*
	0--SLOCType.LOGIC_CODE_LINES_FROM_SOURCE_FILE；
	1--SLOCType.PHYSICAL_CODE_LINES_FROM_SOURCE_FILE
	2--SLOCType.COMMENT_LINES_FROM_SOURCE_FILE；
	3--SLOCType.TOTAL_LINES_FROM_SOURCE_FILE；
	4--SLOCType.LOGIC_CODE_LINES_FROM_AST；
	5--SLOCType.PHYSICAL_CODE_LINES_FROM_AST；
	6--SLOCType.COMMENT_LINES_FROM_AST
	7--SLOCType.TOTAL_LINES_FROM_AST
	*/
	private int[] slocArray;

	public ModuleInfo(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ModuleInfo that = (ModuleInfo) o;
		return Objects.equals(absolutePath, that.absolutePath);
	}

	@Override
	public int hashCode() {
		return Objects.hash(absolutePath);
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public List<ModuleInfo> getSubModuleList() {
		return subModuleList;
	}

	public void setSubModuleList(List<ModuleInfo> subModuleList) {
		this.subModuleList = subModuleList;
	}

	public ModuleInfo getPreviousModuleInfo() {
		return previousModuleInfo;
	}

	public void setPreviousModuleInfo(ModuleInfo previousModuleInfo) {
		this.previousModuleInfo = previousModuleInfo;
	}

	public List<PackageInfo> getMainPackageList() {
		return mainPackageList;
	}

	public void setMainPackageList(List<PackageInfo> mainPackageList) {
		this.mainPackageList = mainPackageList;
	}

	public boolean hasMainPackageList() {
		return mainPackageList.size() > 0;
	}

	public List<PackageInfo> getTestPackageList() {
		return testPackageList;
	}

	public void setTestPackageList(List<PackageInfo> testPackageList) {
		this.testPackageList = testPackageList;
	}

	public boolean hasTestPackageList() {
		return testPackageList.size() > 0;
	}

	public List<PackageInfo> getOtherPackageList() {
		return otherPackageList;
	}

	public void setOtherPackageList(List<PackageInfo> otherPackageList) {
		this.otherPackageList = otherPackageList;
	}

	public boolean hasOtherPackageList() {
		return otherPackageList.size() > 0;
	}

	public String getMainScopePath() {
		return mainScopePath;
	}

	public void setMainScopePath(String mainScopePath) {
		this.mainScopePath = mainScopePath;
	}

	public String getTestScopePath() {
		return testScopePath;
	}

	public void setTestScopePath(String testScopePath) {
		this.testScopePath = testScopePath;
	}

	public String getOtherScopePath() {
		return otherScopePath;
	}

	public void setOtherScopePath(String otherScopePath) {
		this.otherScopePath = otherScopePath;
	}

	public int[] getSlocArray() {
		return slocArray;
	}

	public void setSlocArray(int[] slocArray) {
		this.slocArray = slocArray;
	}

	public int getNumberOfJavaFile() {
		return numberOfJavaFile;
	}

	public void setNumberOfJavaFile(int numberOfJavaFile) {
		this.numberOfJavaFile = numberOfJavaFile;
	}

	public int getNumberOfAllClass() {
		return numberOfAllClass;
	}

	public void setNumberOfAllClass(int numberOfAllClass) {
		this.numberOfAllClass = numberOfAllClass;
	}


	/**
	 * @return All PackageInfo objects in {@code ModuleInfo}, testPackageList and otherPackageList.
	 */
	public List<PackageInfo> getPackageList() {
		List<PackageInfo> all = new ArrayList<>();
		all.addAll(mainPackageList);
		all.addAll(testPackageList);
		all.addAll(otherPackageList);
		return all;
	}

}
