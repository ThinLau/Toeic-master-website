package com.thinlau.springmvc.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.thinlau.springmvc.dao.UserDao;
import com.thinlau.springmvc.model.User;

@Controller
public class AdminController {

	@Autowired
	UserDao userDao;
	
	@RequestMapping(value="/account-manager", method=RequestMethod.GET)
	public String acountManagerPage(Model model, HttpSession session) {
		
		List<User> users = (List<User>) userDao.findAll();
		
		model.addAttribute("users", users);
		model.addAttribute("user", session.getAttribute("user"));
		model.addAttribute("module", "manager-account");
		return "user/account_manager/account_manager_page";
	}
	
	@RequestMapping(value="/change-account-status", method=RequestMethod.GET)
	public String blockAccount(Model model, @RequestParam("id") int userId) {
		User user = userDao.findOne(userId);

		if(user.getStatus() == 1) {
			user.setStatus(0);
		}
		else user.setStatus(1);
		userDao.save(user);
		
		return "redirect:/account-manager";
	}
	
	
}
