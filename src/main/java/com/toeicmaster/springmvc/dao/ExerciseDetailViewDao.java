package com.toeicmaster.springmvc.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.toeicmaster.springmvc.model.ExerciseDetailView;

public interface ExerciseDetailViewDao extends CrudRepository<ExerciseDetailView, Integer>{
	
	// find by pary type: listen or read
	List<ExerciseDetailView> findByPartType(String partType);
	
	Page<ExerciseDetailView> findByPartType(String partType, Pageable pageAble);
}
