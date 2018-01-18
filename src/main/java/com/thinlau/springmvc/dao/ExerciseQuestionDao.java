package com.thinlau.springmvc.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.thinlau.springmvc.model.ExerciseQuestion;

public interface ExerciseQuestionDao extends CrudRepository<ExerciseQuestion, Integer> {
	
	ExerciseQuestion findByNumAndExerciseId(int num, int exerciseId);
	
	List<ExerciseQuestion> findByExerciseId(int exerciseId);
}
