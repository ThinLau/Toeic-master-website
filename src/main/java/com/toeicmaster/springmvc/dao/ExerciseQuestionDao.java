package com.toeicmaster.springmvc.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.toeicmaster.springmvc.model.ExerciseQuestion;

public interface ExerciseQuestionDao extends CrudRepository<ExerciseQuestion, Integer> {
	
	ExerciseQuestion findByNumAndExerciseId(int num, int exerciseId);
	
	List<ExerciseQuestion> findByExerciseId(int exerciseId);

	ExerciseQuestion findById(int exerciseQuestionId);
	
}
