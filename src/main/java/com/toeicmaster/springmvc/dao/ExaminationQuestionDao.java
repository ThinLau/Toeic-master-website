package com.toeicmaster.springmvc.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.toeicmaster.springmvc.model.ExaminationQuestion;

public interface ExaminationQuestionDao extends CrudRepository<ExaminationQuestion, Integer> {
	
	ExaminationQuestion findByNumAndExamId(int num, int examinationId);
	
	List<ExaminationQuestion> findByExamId(int examId);
	
	List<ExaminationQuestion> findByExamIdAndPart(int examId, int part);

	ExaminationQuestion findById(int examQuestionId);
	
	@Query(value = "select * from examination_question  where examination_id = ?1", nativeQuery = true)
	List<ExaminationQuestion> getExamSunQuestionByExamination_Id(int examId);
}
