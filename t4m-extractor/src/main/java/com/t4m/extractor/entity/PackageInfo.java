package com.t4m.extractor.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Yuxiang Liao on 2020-07-07 18:35.
 */
public class PackageInfo implements Serializable {

	private static final long serialVersionUID = 1661151854125377881L;
	public static final String EMPTY_IDENTIFIER = "(null)";

	private String fullyQualifiedName; // = (null), if cannot resolve package from the path of java file.
	private String absolutePath;

	private ModuleInfo moduleInfo;

	private PackageInfo previousPackage;
	private List<PackageInfo> subPackageList = new ArrayList<>();
	private List<ClassInfo> classList = new ArrayList<>(); // contains only public outer class
	private List<ClassInfo> nestedClassList = new ArrayList<>();
	private List<ClassInfo> extraClassList = new ArrayList<>();

	// metric meta data
	private List<PackageInfo> activeDependencyAkaFanOutList = new ArrayList<>();
	private List<PackageInfo> passiveDependencyAkaFanInList = new ArrayList<>();

	//basic
	private int numberOfJavaFile;
	private int numberOfAllClass;
	//sloc
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
	private int[] slocArrayForCurrentPkg; // the sum of SLOC of all children classes, excluding the subpackages.
	private int[] slocArrayForCurrentAndSubPkg; // include the subpackages as well.

	// Coupling
	private int afferentCoupling;
	private int efferentCoupling;
	private String instability;
	private String abstractness;

	public PackageInfo(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		PackageInfo that = (PackageInfo) o;
		return Objects.equals(absolutePath, that.absolutePath);
	}

	@Override
	public int hashCode() {
		return Objects.hash(absolutePath);
	}

	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}

	public void setFullyQualifiedName(String fullyQualifiedName) {
		this.fullyQualifiedName = fullyQualifiedName;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public ModuleInfo getModuleInfo() {
		return moduleInfo;
	}

	public void setModuleInfo(ModuleInfo moduleInfo) {
		this.moduleInfo = moduleInfo;
	}

	public PackageInfo getPreviousPackage() {
		return previousPackage;
	}

	public void setPreviousPackage(PackageInfo previousPackage) {
		this.previousPackage = previousPackage;
	}

	public boolean hasPreviousPackage() {
		return previousPackage != null ? true : false;
	}

	public List<PackageInfo> getSubPackageList() {
		return subPackageList;
	}

	public void setSubPackageList(List<PackageInfo> subPackageList) {
		this.subPackageList = subPackageList;
	}

	public List<ClassInfo> getClassList() {
		return classList;
	}

	public void setClassList(List<ClassInfo> classList) {
		this.classList = classList;
	}

	public List<ClassInfo> getNestedClassList() {
		return nestedClassList;
	}

	public void setNestedClassList(List<ClassInfo> nestedClassList) {
		this.nestedClassList = nestedClassList;
	}

	public List<ClassInfo> getExtraClassList() {
		return extraClassList;
	}

	public void setExtraClassList(List<ClassInfo> extraClassList) {
		this.extraClassList = extraClassList;
	}

	public List<PackageInfo> getActiveDependencyAkaFanOutList() {
		return activeDependencyAkaFanOutList;
	}

	public void setActiveDependencyAkaFanOutList(
			List<PackageInfo> activeDependencyAkaFanOutList) {
		this.activeDependencyAkaFanOutList = activeDependencyAkaFanOutList;
	}

	public List<PackageInfo> getPassiveDependencyAkaFanInList() {
		return passiveDependencyAkaFanInList;
	}

	public void setPassiveDependencyAkaFanInList(
			List<PackageInfo> passiveDependencyAkaFanInList) {
		this.passiveDependencyAkaFanInList = passiveDependencyAkaFanInList;
	}

	public String getAbstractness() {
		return abstractness;
	}

	public void setAbstractness(String abstractness) {
		this.abstractness = abstractness;
	}

	public int getAfferentCoupling() {
		return afferentCoupling;
	}

	public void setAfferentCoupling(int afferentCoupling) {
		this.afferentCoupling = afferentCoupling;
	}

	public int getEfferentCoupling() {
		return efferentCoupling;
	}

	public void setEfferentCoupling(int efferentCoupling) {
		this.efferentCoupling = efferentCoupling;
	}

	public String getInstability() {
		return instability;
	}

	public void setInstability(String instability) {
		this.instability = instability;
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

	public int[] getSlocArrayForCurrentPkg() {
		return slocArrayForCurrentPkg;
	}

	public void setSlocArrayForCurrentPkg(int[] slocArrayForCurrentPkg) {
		this.slocArrayForCurrentPkg = slocArrayForCurrentPkg;
	}

	public int[] getSlocArrayForCurrentAndSubPkg() {
		return slocArrayForCurrentAndSubPkg;
	}

	public void setSlocArrayForCurrentAndSubPkg(int[] slocArrayForCurrentAndSubPkg) {
		this.slocArrayForCurrentAndSubPkg = slocArrayForCurrentAndSubPkg;
	}

	/**
	 * @return a integrated list of @link PackageInfo#classList, @link PackageInfo#nestedClassList and @link PackageInfo#extraClassList
	 */
	public List<ClassInfo> getAllClassList() {
		List<ClassInfo> all = new ArrayList<>();
		all.addAll(classList);
		all.addAll(nestedClassList);
		all.addAll(extraClassList);
		return all;
	}
}
