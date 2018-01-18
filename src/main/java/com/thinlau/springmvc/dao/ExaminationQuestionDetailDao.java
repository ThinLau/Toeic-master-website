package com.thinlau.springmvc.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.thinlau.springmvc.model.ExaminationQuestionDetail;

public interface ExaminationQuestionDetailDao extends CrudRepository<ExaminationQuestionDetail, Integer> {
	 
	List<ExaminationQuestionDetail> findByExamQuestionId(int examQuestionId);
	
	ExaminationQuestionDetail findByExamIdAndNumInExam(int examId, int numInExam);
}
