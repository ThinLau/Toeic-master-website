package com.toeicmaster.springmvc.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.toeicmaster.springmvc.model.ExerciseDetailView;

public interface ExerciseDetailViewDao extends CrudRepository<ExerciseDetailView, Integer>{
	
	// find by pary type: listen or read
	List<ExerciseDetailView> findByPartType(String partType);
	
	Page<ExerciseDetailView> findByPartType(String partType, Pageable pageAble);
	
	Page<ExerciseDetailView> findByPartTypeAndExerciseName(String partType,String exerciseName, Pageable pageAble);
	
	@Query(value = "SELECT * FROM exercise_detail WHERE exercise_name =" + "?1", nativeQuery = true)
	ExerciseDetailView searchName(String examinationName);
}
