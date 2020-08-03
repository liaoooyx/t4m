package com.t4m.web.controller;

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
	 * @param model 用于aop切片
	 */
	@GetMapping("/introduction")
	public String introduction(Model model) {
		return "page/document/introduction";
	}

	/**
	 * @param model 用于aop切片
	 */
	@GetMapping("/how-to-use")
	public String overview(Model model) {
		return "page/document/how_to_use";
	}

}
