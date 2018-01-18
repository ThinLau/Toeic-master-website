package com.thinlau.springmvc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thinlau.springmvc.dao.ExaminationQuestionDao;
import com.thinlau.springmvc.dao.ExaminationQuestionDetailDao;
import com.thinlau.springmvc.message.Response;
import com.thinlau.springmvc.model.ExaminationQuestion;
import com.thinlau.springmvc.model.ExaminationQuestionDetail;


@RestController
public class ExaminationRestWebController {

	@Autowired
	ExaminationQuestionDao examinationQuestionDao;
	
	@Autowired 
	ExaminationQuestionDetailDao examinationQuestionDetailDao;
	
	@RequestMapping(value = "/get-exam-question", method = RequestMethod.GET)
	public Response initPhotoQuestion(@RequestParam("num") int num, @RequestParam("examId") int examId) {
		//get audio by id
		ExaminationQuestion question = null;
		question = examinationQuestionDao.findByNumAndExamId(num, examId);
		String status = "Done";
		if(question == null) status = "Fail";
		Response response = new Response(status, question);
		return response;
		
	}
	
	@RequestMapping(value = "/get-exam-answer", method = RequestMethod.GET)
	public Response getPhotoAnswer(@RequestParam("examQuestionId") int examQuestionId) {
		//get audio by id
		List<ExaminationQuestionDetail> questions = examinationQuestionDetailDao.findByExamQuestionId(examQuestionId);
		
		//System.out.println("correct answer: ========================:" +questions.get(0).getCorrectAnswer());
		
		// Create Response Object
		Response response = new Response("Done", questions);
		return response;
	}
	
	// find examination_question has num_in_exam and exam_id
	@RequestMapping(value = "/get-question-by-num-in-exam", method = RequestMethod.GET)
	public Response getExamQuestionByExamIdAndNumInExam(@RequestParam("examId") int examId,
			@RequestParam("numInExam") int numInExam) {
		
		ExaminationQuestionDetail questionDetail = examinationQuestionDetailDao.findByExamIdAndNumInExam(examId, numInExam);
		int examQuestionId = questionDetail.getExamQuestionId();
		ExaminationQuestion examQuestion = examinationQuestionDao.findOne(examQuestionId);
		// Create Response Object
		Response response = new Response("Done", examQuestion);
		return response;
	}
	

}
