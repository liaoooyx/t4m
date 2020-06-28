package com.t4m.web.controller;

import com.t4m.extractor.T4MExtractor;
import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.serializer.T4MProjectInfoSerializer;
import com.t4m.serializer.T4MSerializer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Yuxiang Liao on 2020-06-23 04:51.
 */

@Controller
public class IndexController {

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/test")
	public String test(Model model) {
		T4MSerializer serializer = new T4MProjectInfoSerializer();
		List<ProjectInfo> projectInfoList = serializer.deserializeAll();
		model.addAttribute("projectList", projectInfoList);
		model.addAllAttributes(projectInfoList);
		model.addAttribute("testsize",projectInfoList.size());
		model.addAttribute("classList",projectInfoList.get(0).getClassList());
		return "test";
	}

}
