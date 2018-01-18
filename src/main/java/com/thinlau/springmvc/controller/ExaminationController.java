package com.thinlau.springmvc.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.thinlau.springmvc.dao.ExaminationDao;
import com.thinlau.springmvc.dao.ExaminationDetailViewDao;
import com.thinlau.springmvc.dao.PartDao;
import com.thinlau.springmvc.dao.UserDao;
import com.thinlau.springmvc.model.Examination;
import com.thinlau.springmvc.model.ExaminationDetailView;

@Controller
@RequestMapping("/examination")
public class ExaminationController {

	@Autowired
	ExaminationDao examinationDao;

	@Autowired
	ExaminationDetailViewDao examinationdetailDao;

	@Autowired
	PartDao partDao;

	@Autowired
	UserDao userDao;

	Page<ExaminationDetailView> examinations;

	int pageSize = 6;
	int currentPage = 0;
	int totalPage = 0;

	// exercise homepage
	@RequestMapping(value = {"","homepage"}, method = RequestMethod.GET)
	public String exerciseHomepage(Model model) {
		currentPage = 0;
				
		paging(model);
		return "examination/exam_homepage";
	}

	// paging method
	private void paging(Model model) {
		examinations = examinationdetailDao.findAll(new PageRequest(currentPage, pageSize));

		totalPage = (examinationdetailDao.count() % pageSize) == 0 ? (int) examinationdetailDao.count() / pageSize
				: (int)examinationdetailDao.count() / pageSize + 1;

		model.addAttribute("totalPage", totalPage);
		model.addAttribute("currentPage", currentPage+1);
		model.addAttribute("examinations", examinations);
	}
	
	@RequestMapping(value = "homepage/{page}", method = RequestMethod.GET)
	public String exercisePage(@PathVariable("page") int page, Model model) {

		currentPage = page-1; 
		
		paging(model);
		return "examination/exam_homepage";
	}
	
	
	@RequestMapping(value = "/do-examination", method = RequestMethod.GET)
	public String doExercise(@RequestParam("examinationNo") int examinationId, Model model){
		
		Examination exam = examinationDao.findOne(examinationId);
		
		List<Integer> numbers = new ArrayList<>();
		for(int i = 1; i <= exam.getNumberOfQuestion();i++)
			numbers.add(i);
		
		model.addAttribute("exam", exam);
		model.addAttribute("numbers", numbers);
		return "examination/do_examination";
	}
	
	
}
