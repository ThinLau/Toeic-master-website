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
	
	Page<ExerciseDetailView> findByPartTypeIgnoreCaseContaining(String partType, Pageable pageAble);
	
	Page<ExerciseDetailView> findByPartTypeIgnoreCaseContainingAndExerciseNameIgnoreCaseContaining(String partType,String exerciseName, Pageable pageAble);
	
	@Query(value = "SELECT e FROM ExerciseDetailView e WHERE e.exerciseName =" + "?1")
	ExerciseDetailView searchName(String examinationName);
	
	@Query(value = "SELECT count(e.id) FROM ExerciseDetailView e WHERE e.partType =" + "?1")
	int countType(String type);
}
