package com.thinlau.springmvc.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.thinlau.springmvc.dao.InstructionDao;
import com.thinlau.springmvc.dao.UserDao;
import com.thinlau.springmvc.model.Instruction;
import com.thinlau.springmvc.model.User;

@Controller
public class CreateInstructionController {

	@Autowired
	UserDao userDao;

	@Autowired
	InstructionDao instructionDao;
	

	
	// user call create exercise page
	@RequestMapping(value="/new-instruction-page", method=RequestMethod.GET)
	public String newInstructionPage(HttpSession session , Model model) {

		User user = (User) session.getAttribute("user");
		
		Instruction instruction = new Instruction();
		model.addAttribute("instruction", instruction);
		model.addAttribute("user", user);
		model.addAttribute("module", "new-instruction");
		return "user/instruction/create_new_instruction";
	}
	
	// save the exercise 
	
	@RequestMapping(value="/save-instruction", method=RequestMethod.POST)
	public String saveExercise(HttpSession session , Model model,
			@ModelAttribute("instruction") Instruction instruction) {
	
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date();
		
		String dateCreate = dateFormat.format(date);
		
		instruction.setDateCreate(dateCreate);
		
		User user = (User) session.getAttribute("user");
		instruction.setAuthor(user.getUsername());
		
		instructionDao.save(instruction);
		
		return "redirect:/new-instruction-page";
	}
	
	/*
	@RequestMapping(value="/update-exercise", method=RequestMethod.POST)
	public String upadateExerciseInfo(HttpSession session , Model model,
			@ModelAttribute("exercise") Exercise exercise) {
		
		Exercise entity = exerciseDao.findOne(exercise.getId());
		int exerciseId = exercise.getId();
		if(entity != null) {
			entity.setName(exercise.getName());
			entity.setNumberOfQuestion(exercise.getNumberOfQuestion());
			entity.setPart(exercise.getPart());
			exerciseDao.save(entity);
			exerciseId = entity.getId();
		}
		
		model.addAttribute("exercise", exercise);
		return "redirect:/new-exercise/info?id="+exerciseId;
	}
	*/
	
	
}
