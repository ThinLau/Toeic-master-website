package com.toeicmaster.springmvc.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.toeicmaster.springmvc.model.ExaminationQuestionDetail;

public interface ExaminationQuestionDetailDao extends CrudRepository<ExaminationQuestionDetail, Integer> {
	 
	List<ExaminationQuestionDetail> findByExamQuestionId(int examQuestionId);
	
	ExaminationQuestionDetail findByExamIdAndNumInExam(int examId, int numInExam);
}
