package com.toeicmaster.springmvc.controller;

import java.awt.Robot;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.toeicmaster.springmvc.dao.UserDao;
import com.toeicmaster.springmvc.model.Exercise;
import com.toeicmaster.springmvc.model.User;

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
		User user = userDao.findById(userId);

		if(user.getStatus() == 1) {
			user.setStatus(0);
		}
		else user.setStatus(1);
		userDao.save(user);
		
		return "redirect:/account-manager";
	}
	
	@RequestMapping(value = "/change-role", method = RequestMethod.POST)
	public String changeRole(HttpSession session, Model model, @ModelAttribute("userid") int userid, @ModelAttribute("gender") int role) {
		
		User user = userDao.findById(userid);
		
		user.setRole(role);
		userDao.save(user);
		
		return "redirect:/account-manager";
	}
	
}
