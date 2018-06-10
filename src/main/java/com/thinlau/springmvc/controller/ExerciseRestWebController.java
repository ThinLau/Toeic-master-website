package com.thinlau.springmvc.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thinlau.springmvc.dao.ExAlreadyDoDao;
import com.thinlau.springmvc.dao.ExerciseDao;
import com.thinlau.springmvc.dao.ExerciseQuestionDao;
import com.thinlau.springmvc.dao.ExerciseQuestionDetailDao;
import com.thinlau.springmvc.message.Response;
import com.thinlau.springmvc.model.ExAlreadyDo;
import com.thinlau.springmvc.model.Exercise;
import com.thinlau.springmvc.model.ExerciseQuestion;
import com.thinlau.springmvc.model.ExerciseQuestionDetail;
import com.thinlau.springmvc.model.User;


@RestController
public class ExerciseRestWebController {

	@Autowired
	ExerciseDao exerciseDao;
	
	@Autowired
	ExerciseQuestionDao exerciseQuestionDao;
	
	@Autowired 
	ExerciseQuestionDetailDao exerciseQuestionDetailDao;
	
	@Autowired
	ExAlreadyDoDao exAlreadyDoDao;
	
	@RequestMapping(value = "/get-question", method = RequestMethod.GET)
	public Response initPhotoQuestion(@RequestParam("num") int num, @RequestParam("exerciseId") int exerciseId,
			 HttpSession session, @RequestParam("exAlreadyDoId") int exAlreadyDoId ){
		User entity = (User) session.getAttribute("user");
		if(entity != null) {
			Exercise exercise = exerciseDao.findOne(exerciseId);
			ExAlreadyDo ex = exAlreadyDoDao.findOne(exAlreadyDoId);
			if (ex != null) {
				double currentExStatus = ex.getStatus();
				double percent = round(100 * ((double) num / Integer.parseInt(exercise.getNumberOfQuestion())), 2);

				if (currentExStatus < percent)
					ex.setStatus(percent);
				exAlreadyDoDao.save(ex);
			}
		}
		
		//get audio by id
		ExerciseQuestion question = exerciseQuestionDao.findByNumAndExerciseId(num, exerciseId);
		
		// Create Response Object
		Response response = new Response("Done", question);
		return response;
	}
	
	@RequestMapping(value = "/get-answer", method = RequestMethod.GET)
	public Response getPhotoAnswer(@RequestParam("exerciseQuestionId") int exerciseQuestionId) {
		//get audio by id
		List<ExerciseQuestionDetail> questions = exerciseQuestionDetailDao.findByExerciseQuestionId(exerciseQuestionId);
		
		//System.out.println("correct answer: ========================:" +questions.get(0).getCorrectAnswer());
		
		// Create Response Object
		Response response = new Response("Done", questions);
		return response;
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
}
