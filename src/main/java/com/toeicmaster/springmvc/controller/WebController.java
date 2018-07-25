package com.toeicmaster.springmvc.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.toeicmaster.springmvc.model.User;

@Controller
public class WebController {

	// homepe request
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String homePage(Model model, HttpSession session) {	
		User user = (User) session.getAttribute("user");
		model.addAttribute("module", "home");
		model.addAttribute("user", user);
		return "index";
	}
	

}
