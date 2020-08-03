package com.t4m.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Yuxiang Liao on 2020-08-02 17:32.
 */
@Controller
public class HomeController {

	/**
	 * @param model 用于aop切片
	 */
	@RequestMapping("/")
	public String home(Model model) {
		return "page/document/introduction";
	}
}
