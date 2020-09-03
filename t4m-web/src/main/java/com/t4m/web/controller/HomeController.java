package com.t4m.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Yuxiang Liao on 2020-08-02 17:32.
 */
@Controller
@RequestMapping("/")
public class HomeController {

	/**
	 * @param model for AOPHandler to insert values
	 * @return the path of html resource
	 */
	@GetMapping()
	public String home(Model model) {
		return "page/document/how_to_use";
	}
}
