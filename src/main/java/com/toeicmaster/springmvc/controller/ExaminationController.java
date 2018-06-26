package com.toeicmaster.springmvc.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.toeicmaster.springmvc.dao.ExAlreadyDoDao;
import com.toeicmaster.springmvc.dao.ExaminationDao;
import com.toeicmaster.springmvc.dao.ExaminationDetailViewDao;
import com.toeicmaster.springmvc.dao.ExaminationQuestionDao;
import com.toeicmaster.springmvc.dao.PartDao;
import com.toeicmaster.springmvc.dao.UserDao;
import com.toeicmaster.springmvc.model.ExAlreadyDo;
import com.toeicmaster.springmvc.model.Examination;
import com.toeicmaster.springmvc.model.ExaminationDetailView;
import com.toeicmaster.springmvc.model.ExaminationQuestion;
import com.toeicmaster.springmvc.model.User;

@Controller
@RequestMapping("/examination")
public class ExaminationController {

	@Autowired
	ExaminationDao examinationDao;
	
	@Autowired
	ExaminationQuestionDao examinationQuestionDao;

	@Autowired
	ExaminationDetailViewDao examinationdetailDao;

	@Autowired
	PartDao partDao;

	@Autowired
	UserDao userDao;

	@Autowired
	ExAlreadyDoDao exAlreadyDoDao;

	Page<ExaminationDetailView> examinations;

	int pageSize = 6;
	int currentPage = 0;
	int totalPage = 0;

	// exercise homepage
	@RequestMapping(value = { "", "homepage" }, method = RequestMethod.GET)
	public String exerciseHomepage(Model model) {
		currentPage = 0;

		paging(model);
		return "examination/exam_homepage";
	}

	// paging method
	private void paging(Model model) {
		examinations = examinationdetailDao.findAll(new PageRequest(currentPage, pageSize));

		totalPage = (examinationdetailDao.count() % pageSize) == 0 ? (int) examinationdetailDao.count() / pageSize
				: (int) examinationdetailDao.count() / pageSize + 1;

		model.addAttribute("totalPage", totalPage);
		model.addAttribute("currentPage", currentPage + 1);
		model.addAttribute("examinations", examinations);
	}

	@RequestMapping(value = "homepage/{page}", method = RequestMethod.GET)
	public String exercisePage(@PathVariable("page") int page, Model model) {

		currentPage = page - 1;

		paging(model);
		return "examination/exam_homepage";
	}

	@RequestMapping(value = "/do-examination", method = RequestMethod.GET)
	public String doExercise(@RequestParam("examinationNo") int examinationId, Model model, HttpSession session) {

		Examination exam = examinationDao.findById(examinationId);

		List<Integer> numbers = new ArrayList<>();
		for (int i = 1; i <= exam.getNumberOfQuestion(); i++)
			numbers.add(i);

		// luu bai thi da lam
		int exAlreadyDoId = -1;
		User entity = (User) session.getAttribute("user");
		if (entity != null) {
			// get user id
			int userId = entity.getId();
			ExAlreadyDo exAlreadyDo = exAlreadyDoDao.findByExamIdAndUserId(examinationId, userId);
			if (exAlreadyDo == null) {
				// create new ex already do record
				ExAlreadyDo ex = new ExAlreadyDo();
				ex.setUserId(userId);
				// get current date time
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date();
				String logTime = dateFormat.format(date);
				ex.setLogTime(logTime);

				ex.setExerciseId(1);// fake
				ex.setExamId(examinationId);// fake
				ex.setExType("exam");
				ex.setStatus((double) 0);
				exAlreadyDoDao.save(ex);

				exAlreadyDoId = ex.getId();
			} else exAlreadyDoId = exAlreadyDo.getId();
		}
		
		List<ExaminationQuestion> numsExamQuestions = new ArrayList<>();
		numsExamQuestions = examinationQuestionDao.getExamSunQuestionByExamination_Id(examinationId);
			
		
		
		model.addAttribute("exam", exam);
		model.addAttribute("numbers", numbers);
		model.addAttribute("numsExamQuestions", numsExamQuestions);
		model.addAttribute("exAlreadyDoId", exAlreadyDoId);
		model.addAttribute("numberOfQuestion", numsExamQuestions.size());
		return "examination/do_examination";
	}

}
