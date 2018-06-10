package com.toeicmaster.springmvc.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.toeicmaster.springmvc.model.Exercise;

public interface ExerciseDao extends CrudRepository<Exercise,Integer> {
	List<Exercise> findByAuthor(int userId);
	// get exercise name by id
	@Query(value = "select name from exercise  where id = ?1" , nativeQuery = true)
	String getExerciseNameById(int id);
	
	// get part id by exercise id
	@Query(value = "select part from exercise  where id = ?1" , nativeQuery = true)
	int getPartIdById(int id);
	
}
