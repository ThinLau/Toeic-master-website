package com.thinlau.springmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class WebController {

	// homepe request
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String homePage(Model model) {		
		model.addAttribute("module", "home");
		return "index";
	}
	

}