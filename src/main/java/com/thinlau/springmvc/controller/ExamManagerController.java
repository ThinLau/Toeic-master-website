package com.thinlau.springmvc.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinlau.springmvc.dao.ExaminationDao;
import com.thinlau.springmvc.dao.ExaminationQuestionDao;
import com.thinlau.springmvc.dao.ExaminationQuestionDetailDao;
import com.thinlau.springmvc.dao.ExerciseDao;
import com.thinlau.springmvc.dao.ExerciseQuestionDao;
import com.thinlau.springmvc.dao.ExerciseQuestionDetailDao;
import com.thinlau.springmvc.dao.InstructionDao;
import com.thinlau.springmvc.model.Examination;
import com.thinlau.springmvc.model.ExaminationQuestion;
import com.thinlau.springmvc.model.ExaminationQuestionDetail;
import com.thinlau.springmvc.model.Exercise;
import com.thinlau.springmvc.model.ExerciseQuestion;
import com.thinlau.springmvc.model.ExerciseQuestionDetail;
import com.thinlau.springmvc.model.Instruction;
import com.thinlau.springmvc.model.User;

@Controller
@RequestMapping("exam-manager")
public class ExamManagerController {

	@Autowired
	ExerciseDao exerciseDao;

	@Autowired
	ExaminationDao examinationDao;

	@Autowired
	InstructionDao instructionDao;

	@Autowired
	ExaminationQuestionDao examinationQuestionDao;

	@Autowired
	ExaminationQuestionDetailDao examinationQuestionDetailDao;

	@Autowired
	ExerciseQuestionDao exerciseQuestionDao;

	@Autowired
	ExerciseQuestionDetailDao exerciseQuestionDetailDao;

	// homepage
	@RequestMapping(value = { "" }, method = RequestMethod.GET)
	public String exerciseHomepage(RedirectAttributes redirectAttributes) {

		redirectAttributes.addFlashAttribute("manager_module", "exercise_manager");
		return "redirect:/exam-manager/home";
	}

	@RequestMapping(value = "home", method = RequestMethod.GET)
	public String managerHomepage(Model model, HttpSession session) {

		User user = (User) session.getAttribute("user");

		// find list exercises was created by this user
		List<Exercise> exercises = exerciseDao.findByAuthor(user.getId());

		// find list examinations was create by this user
		List<Examination> examinations = examinationDao.findByAuthor(user.getId());

		// find list instructions was create by this user
		List<Instruction> instructions = instructionDao.findByAuthor(user.getUsername());

		model.addAttribute("exercises", exercises);
		model.addAttribute("examinations", examinations);
		model.addAttribute("instructions", instructions);

		model.addAttribute("module", "manager-exam");
		model.addAttribute("user", user);
		return "user/exam_manager/exam_manager_page";
	}

	// delete exercise
	@RequestMapping(value = "delete-exercise", method = RequestMethod.GET)
	public String deleteExercise(RedirectAttributes redirectAttributes, @RequestParam("id") int exerciseId) {

		// delete exercise_question_detail -> delete exercise_question -> delete exercise
		List<ExerciseQuestion> questions = exerciseQuestionDao.findByExerciseId(exerciseId);
		for (ExerciseQuestion question : questions) {
			List<ExerciseQuestionDetail> questionDetails = exerciseQuestionDetailDao.findByExerciseQuestionId(question.getId());
			exerciseQuestionDetailDao.delete(questionDetails);
		}
		exerciseQuestionDao.delete(questions);

		Exercise exercise = exerciseDao.findOne(exerciseId);

		exerciseDao.delete(exercise);

		redirectAttributes.addFlashAttribute("manager_module", "exercise_manager");
		return "redirect:/exam-manager/home";
	}

	// delete examination
	@RequestMapping(value = "delete-examination", method = RequestMethod.GET)
	public String deleteExamination(RedirectAttributes redirectAttributes, @RequestParam("id") int examId) {

		// delete examintion_question_detail -> delete examination_question -> delete
		// examination
		List<ExaminationQuestion> questions = examinationQuestionDao.findByExamId(examId);
		for (ExaminationQuestion question : questions) {
			List<ExaminationQuestionDetail> questionDetails = examinationQuestionDetailDao
					.findByExamQuestionId(question.getId());
			examinationQuestionDetailDao.delete(questionDetails);
		}
		examinationQuestionDao.delete(questions);

		Examination exam = examinationDao.findOne(examId);

		examinationDao.delete(exam);

		redirectAttributes.addFlashAttribute("manager_module", "examination_manager");
		return "redirect:/exam-manager/home";
	}

	// delete instruction
	@RequestMapping(value = "delete-instruction", method = RequestMethod.GET)
	public String deleteInstruction(RedirectAttributes redirectAttributes, @RequestParam("id") int id) {

		Instruction instruction = instructionDao.findOne(id);

		instructionDao.delete(instruction);

		redirectAttributes.addFlashAttribute("manager_module", "instruction_manager");
		return "redirect:/exam-manager/home";
	}

}
