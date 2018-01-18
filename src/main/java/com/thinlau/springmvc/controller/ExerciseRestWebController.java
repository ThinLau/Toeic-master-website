package com.thinlau.springmvc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thinlau.springmvc.dao.ExerciseQuestionDao;
import com.thinlau.springmvc.dao.ExerciseQuestionDetailDao;
import com.thinlau.springmvc.message.Response;
import com.thinlau.springmvc.model.ExerciseQuestion;
import com.thinlau.springmvc.model.ExerciseQuestionDetail;


@RestController
public class ExerciseRestWebController {

	@Autowired
	ExerciseQuestionDao exerciseQuestionDao;
	
	@Autowired 
	ExerciseQuestionDetailDao exerciseQuestionDetailDao;
	
	@RequestMapping(value = "/get-question", method = RequestMethod.GET)
	public Response initPhotoQuestion(@RequestParam("num") int num, @RequestParam("exerciseId") int exerciseId) {
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
	
	
}
