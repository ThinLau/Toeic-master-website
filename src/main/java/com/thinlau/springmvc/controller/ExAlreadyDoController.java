package com.thinlau.springmvc.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.thinlau.springmvc.dao.ExAlreadyDoDao;
import com.thinlau.springmvc.dao.ExaminationDao;
import com.thinlau.springmvc.dao.ExerciseDao;
import com.thinlau.springmvc.dao.PartDao;
import com.thinlau.springmvc.model.ExAlreadyDo;
import com.thinlau.springmvc.model.ExAlreadyDoDTO;
import com.thinlau.springmvc.model.User;

@Controller
@RequestMapping("/exs-already-do")
public class ExAlreadyDoController {

	@Autowired
	ExerciseDao exerciseDao;

	@Autowired
	ExaminationDao examinationDao;

	@Autowired
	ExAlreadyDoDao exAlreadyDoDao;

	@Autowired
	PartDao partDao;
	
	@RequestMapping(value = { "" }, method = RequestMethod.GET)
	public String exerciseHomepage(Model model, HttpSession session) {
		User user = (User) session.getAttribute("user");

		// find list ex already do
		List<ExAlreadyDo> exAlreadyDos = exAlreadyDoDao.findByUserId(user.getId());
		
		List<ExAlreadyDoDTO> exercises = new ArrayList<>();
		List<ExAlreadyDoDTO> examinations = new ArrayList<>();

		for(ExAlreadyDo ex : exAlreadyDos) {
			ExAlreadyDoDTO exDTO = new ExAlreadyDoDTO();
			exDTO.setId(ex.getId());
			exDTO.setLogTime(ex.getLogTime());
			exDTO.setStatus(ex.getStatus());
			exDTO.setExcerciseId(ex.getExerciseId());
			exDTO.setExamId(ex.getExamId());
			
			if(ex.getExType().equals("exam")) { // examination
				exDTO.setExName(examinationDao.getExamNameById(ex.getExamId()));
				examinations.add(exDTO);
			}else { // exercise 
				exDTO.setExName(exerciseDao.getExerciseNameById(ex.getExerciseId()));
				// get part name
				String partName = partDao.getPartNameById(exerciseDao.getPartIdById(ex.getExerciseId()));
				exDTO.setPartName(partName);
				exercises.add(exDTO);
			}
		}

		model.addAttribute("exercises", exercises);
		model.addAttribute("examinations", examinations);

		model.addAttribute("module", "exs-already-do");
		model.addAttribute("user", user);
		return "user/exs_already_do/ex_already_do_page";
	}
	
}
