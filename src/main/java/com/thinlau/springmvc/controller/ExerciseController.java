package com.thinlau.springmvc.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.thinlau.springmvc.dao.ExerciseDao;
import com.thinlau.springmvc.dao.ExerciseDetailViewDao;
import com.thinlau.springmvc.dao.PartDao;
import com.thinlau.springmvc.dao.UserDao;
import com.thinlau.springmvc.model.Exercise;
import com.thinlau.springmvc.model.ExerciseDetailView;

@Controller
@RequestMapping("/exercise")
public class ExerciseController {

	@Autowired
	ExerciseDao exerciseDao;

	@Autowired
	ExerciseDetailViewDao exercisedetailDao;

	@Autowired
	PartDao partDao;

	@Autowired
	UserDao userDao;

	Page<ExerciseDetailView> exercises;

	int pageSize = 6;
	int currentPage = 0;
	int totalPage = 0;

	// exercise homepage
	@RequestMapping(value = {"","homepage"}, method = RequestMethod.GET)
	public String exerciseHomepage(@RequestParam("exerciseType") String exerciseType, Model model) {
		currentPage = 0;
		model.addAttribute("module", "exercise");
		paging(model,exerciseType);
		return "exercise/exercise_homepage";
	}

	// paging method
	private void paging(Model model, String exerciseType) {
		// get all excerise and paging
		exercises = exercisedetailDao.findByPartType(exerciseType, new PageRequest(currentPage, pageSize));

		// exercisedetailDao.count() la so row trong table
		totalPage = (exercisedetailDao.count() % pageSize) == 0 ? (int) exercisedetailDao.count() / pageSize
				: (int)exercisedetailDao.count() / pageSize + 1;

		model.addAttribute("totalPage", totalPage);
		model.addAttribute("currentPage", currentPage+1);
		model.addAttribute("exerciseType", exerciseType);
		model.addAttribute("exercises", exercises);
	}
	
	// exercise homepage with paging
	@RequestMapping(value = "homepage/{page}", method = RequestMethod.GET)
	public String exercisePage(@RequestParam("exerciseType") String exerciseType,
			@PathVariable("page") int page, Model model) {

		currentPage = page-1; 
		model.addAttribute("module", "exercise");
		paging(model,exerciseType);
		return "exercise/exercise_homepage";
	}
	
	
	@RequestMapping(value = "/do-exercise/{partName}", method = RequestMethod.GET)
	public String doExercise(@PathVariable("partName") String partName,
			@RequestParam("exerciseNo") int exerciseId, Model model){
		
		String result ="";
		String panelHeader ="";
		switch(partName) {
			case "Photo":
				result = "exercise/do_exercise_photo";
				panelHeader = "TOEIC&reg; Listening part 1: Photographs";
			break;
			case "Question-Response":
				result = "exercise/do_exercise_question_response";
				panelHeader = "TOEIC&reg; Listening part 2: Question &amp; response";
			break;
			case "Short-conversation":
				result = "exercise/do_exercise_short_conversation_and_talk";
				panelHeader = "TOEIC&reg; Listening part 3: Short Conversation";
			break;
			case "Short-talk":
				result = "exercise/do_exercise_short_conversation_and_talk";
				panelHeader = "TOEIC&reg; Listening part 4: Short talk";
			break;
			case "Incomplete-Sentence":
				result = "exercise/do_exercise_incomplete_sentence";
				panelHeader = "TOEIC&reg; Reading part 5 : Incomplete sentences";
			break;
			case "Text-completion":
				result = "exercise/do_exercise_text_completion";
				panelHeader = "TOEIC&reg; Reading part 6 : Text Completion";
			break;
			case "Single-passage":
				result = "exercise/do_exercise_passage";
				panelHeader = "TOEIC&reg; Reading part 7 : Single Passage";
			break;
			case "Double-passage":
				result = "exercise/do_exercise_passage";
				panelHeader = "TOEIC&reg; Reading part 8 : Double Passage";
			break;
			
		}
		
		Exercise exercise = exerciseDao.findOne(exerciseId);
		
		model.addAttribute("module", "exercise");
		model.addAttribute("panelHeader", panelHeader);
		model.addAttribute("exercise", exercise);
		return result;
	}
	
	
}
