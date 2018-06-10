package com.toeicmaster.springmvc.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.toeicmaster.springmvc.model.ExerciseQuestionDetail;

public interface ExerciseQuestionDetailDao extends CrudRepository<ExerciseQuestionDetail, Integer> {
	 
	List<ExerciseQuestionDetail> findByExerciseQuestionId(int exerciseQuestionId);
}
