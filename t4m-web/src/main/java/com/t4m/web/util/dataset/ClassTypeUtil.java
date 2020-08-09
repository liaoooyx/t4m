package com.t4m.web.util.dataset;

import com.t4m.extractor.entity.ClassInfo;
import com.t4m.web.service.ClassService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Yuxiang Liao on 2020-08-09 01:44.
 */
public class ClassTypeUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClassTypeUtil.class);

	private ClassTypeUtil() {
	}

	public static void insertCommonClassTypeRowForOverviewChart(Map<String, List<Object>> series) {
		series.put("Interface", new ArrayList<>());
		series.put("Abstract Class", new ArrayList<>());
		series.put("Class", new ArrayList<>());
		series.put("Enum", new ArrayList<>());
		series.put("Annotation", new ArrayList<>());
		series.put("package-info", new ArrayList<>());
	}

	public static List<Object> chooseSeriesByClassModifier(
			Map<String, List<Object>> series, ClassInfo.ClassModifier classModifier) {
		if (classModifier != null) {
			switch (classModifier) {
				case ENUM:
					return series.get("Enum");
				case ANNOTATION:
					return series.get("Annotation");
				case ABSTRACT_CLASS:
					return series.get("Abstract Class");
				case INTERFACE:
					return series.get("Interface");
				case CLASS:
					return series.get("Class");
				case NONE:
					return series.get("package-info");
			}
		} else {
			LOGGER.debug("Should not go into this statement, please use debug and check the program again.");
			throw new RuntimeException(
					"Should not go into this statement, please use debug and check the program again.");
		}
		return new ArrayList<>();
	}
}
