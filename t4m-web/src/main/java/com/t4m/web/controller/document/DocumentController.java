package com.t4m.web.controller.document;

import com.t4m.conf.GlobalProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Yuxiang Liao on 2020-08-01 23:16.
 */
@Controller
@RequestMapping("/document")
public class DocumentController {

	/**
	 * @param model for AOPHandler to insert values
	 * @return the path of html resource
	 */
	@GetMapping("/introduction")
	public String introduction(Model model) {
		return "page/document/introduction";
	}

	/**
	 * @param model for AOPHandler to insert values
	 * @return the path of html resource
	 */
	@GetMapping("/how-to-use")
	public String overview(Model model) {
		model.addAttribute("defaultExcludedPath", GlobalProperties.DEFAULT_EXCLUDED_PATH);
		return "page/document/how_to_use";
	}

}
