package com.thinlau.springmvc.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.thinlau.springmvc.dao.UserDao;
import com.thinlau.springmvc.model.User;

@Controller
public class LoginController {
	
	@Autowired
	UserDao userDao;
	
	@RequestMapping(value="/login-page", method=RequestMethod.GET)
	public String loginPage(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		return "login/login";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String login(@ModelAttribute("user") User user, Model model, HttpSession session) {
		// login
		User entity =  userDao.findByUsernameAndPassword(user.getUsername(), user.getPassword());
		
		String viewPage = "error_page";
		// role == 1 la nguoi binh thuong. 2,3 co quyen nhap de ||  acount is active. 
		if(entity != null && entity.getRole() != 1 ) {
			if(entity.getStatus() == 0) // block status
				 model.addAttribute("message", "Tài khoản này đã bị khóa. Vui lòng liên hệ admin để xử lý!");
			else {
				viewPage = "redirect:/user-info";
				session.setAttribute("user", entity);
				model.addAttribute("user", entity);
			}
		} else 
			model.addAttribute("message", "Lỗi: ....... Xin đăng nhập lại!");
		
		return viewPage;
	}
	
	@RequestMapping(value="/register-account", method=RequestMethod.POST)
	public String registerAccount(@ModelAttribute("user") User user, Model model) {
		
		user.setRole(2);  // role for create exam
		user.setStatus(1);  // 1 for active status
		
		userDao.save(user); // create new account
		return "redirect:/login-page";
	}
	
	
	@RequestMapping(value="/logout", method=RequestMethod.GET)
	public String logout(Model model, HttpSession session) {
		session.invalidate();  
		return "redirect:/";
	}
	
	
	@RequestMapping(value="/user-info", method=RequestMethod.GET)
	public String userInfo(Model model, HttpSession session) {
		if(session.getAttribute("user") == null)
			return "login/login";
		
		User user = (User) session.getAttribute("user");
		model.addAttribute("user", user);
		model.addAttribute("module", "user-home");
		return "user/user_info";
	}
	
	@RequestMapping(value = "/update-user-info", method = RequestMethod.POST)
	public String upadateUserInfo(HttpSession session, Model model, 
			@ModelAttribute("user") User user) {

		User entity = (User) session.getAttribute("user");
		if (entity != null) {
			entity.setBirthday(user.getBirthday());
			entity.setEmail(user.getEmail());
			entity.setFullName(user.getFullName());
			entity.setPassword(user.getPassword());
			userDao.save(entity);
			
		}

		return "redirect:/user-info";
	}
	
}
