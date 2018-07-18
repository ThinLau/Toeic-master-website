package com.toeicmaster.springmvc.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.toeicmaster.springmvc.dao.ExAlreadyDoDao;
import com.toeicmaster.springmvc.dao.ExerciseDao;
import com.toeicmaster.springmvc.dao.ExerciseDetailViewDao;
import com.toeicmaster.springmvc.dao.PartDao;
import com.toeicmaster.springmvc.dao.UserDao;
import com.toeicmaster.springmvc.model.ExAlreadyDo;
import com.toeicmaster.springmvc.model.Exercise;
import com.toeicmaster.springmvc.model.ExerciseDetailView;
import com.toeicmaster.springmvc.model.User;

@Controller
public class ExerciseController {

	@Autowired
	ExerciseDao exerciseDao;

	@Autowired
	ExerciseDetailViewDao exercisedetailDao;

	@Autowired
	PartDao partDao;

	@Autowired
	UserDao userDao;

	@Autowired
	ExAlreadyDoDao exAlreadyDoDao;

	Page<ExerciseDetailView> exercises;

	int pageSize = 6;
	int currentPage = 0;
	int totalPage = 0;
	String exerciseName = null;

	// exercise homepage
	@RequestMapping(value = "/exercise/homepage", method = RequestMethod.GET)
	public String exerciseHomepage(@RequestParam("exerciseType") String exerciseType, Model model) {
		currentPage = 0;
		model.addAttribute("module", "exercise");
		exerciseName = null;
		paging(model, exerciseType, exerciseName);
		return "exercise/exercise_homepage";
	}

	// paging method
	private void paging(Model model, String exerciseType, String exerciseName) {
		// get all excerise and paging
//		exercises = exercisedetailDao.findByPartType(exerciseType, new PageRequest(currentPage, pageSize));
		if(exerciseName == null)
		{
			exercises = exercisedetailDao.findByPartType(exerciseType, new PageRequest(currentPage, pageSize));
			
			int exerSize = exercisedetailDao.findPartType(exerciseType);
			
			// exercisedetailDao.count() la so row trong table
			totalPage = (exerSize % pageSize) == 0 ? (int) exerSize / pageSize
						: (int) exerSize / pageSize + 1;			
		}
		else { 
			exercises = exercisedetailDao.findByPartTypeAndExerciseName(exerciseType, exerciseName, new PageRequest(currentPage, pageSize));		
			totalPage = 1;
		}		

		model.addAttribute("totalPage", totalPage);
		model.addAttribute("currentPage", currentPage + 1);
		model.addAttribute("exerciseType", exerciseType);
		model.addAttribute("exercises", exercises);
	}
	
	@RequestMapping(value = "/search-exercise", method = RequestMethod.POST)
	public String serchExercise(HttpSession session, Model model, @RequestParam("search") String search, 
			@RequestParam("exerciseType") String exerciseType) {
		
		if(search == "") {
			exerciseName = null;
		} else
			exerciseName = search;
		
		
		currentPage = 0;
		model.addAttribute("module", "exercise");
		paging(model, exerciseType, exerciseName);
		return "exercise/exercise_homepage";
	}

	// exercise homepage with paging
	@RequestMapping(value = "exercice-homepage/{page}", method = RequestMethod.GET)
	public String exercisePage(@RequestParam("exerciseType") String exerciseType, @PathVariable("page") int page,
			Model model) {

		currentPage = page - 1;
		model.addAttribute("module", "exercise");
		paging(model, exerciseType, exerciseName);
		return "exercise/exercise_homepage";
	}

	@RequestMapping(value = "/do-exercise/{partName}", method = RequestMethod.GET)
	public String doExercise(@PathVariable("partName") String partName, @RequestParam("exerciseNo") int exerciseId,
			Model model, HttpSession session) {

		String result = "";
		String panelHeader = "";
		switch (partName) {
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

		Exercise exercise = exerciseDao.findById(exerciseId);
		// luu bai tap da lam
		int exAlreadyDoId = -1;
		User entity = (User) session.getAttribute("user");
		if (entity != null) {
			// get user id
			int userId = entity.getId();
			ExAlreadyDo exAlreadyDo = exAlreadyDoDao.findByExerciseIdAndUserId(exerciseId, userId);
			if (exAlreadyDo == null) {
				// create new ex already do record
				ExAlreadyDo ex = new ExAlreadyDo();
				ex.setUserId(userId);
				// get current date time
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date();
				String logTime = dateFormat.format(date);
				ex.setLogTime(logTime);

				ex.setExerciseId(exerciseId);// real
				ex.setExamId(1);// fake
				ex.setExType("exercise");
				// set status
				int numOfQuest = Integer.parseInt(exercise.getNumberOfQuestion());
				double percent = 100 * (1.0 / numOfQuest);
				ex.setStatus(round(percent, 2));

				exAlreadyDoDao.save(ex);
				exAlreadyDoId = ex.getId();
			}else exAlreadyDoId = exAlreadyDo.getId();

		}

		model.addAttribute("module", "exercise");
		model.addAttribute("panelHeader", panelHeader);
		model.addAttribute("exercise", exercise);
		model.addAttribute("exAlreadyDoId", exAlreadyDoId);
		return result;
	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}
}
