package com.toeicmaster.springmvc.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.toeicmaster.springmvc.model.Examination;
import com.toeicmaster.springmvc.model.Exercise;

public interface ExerciseDao extends CrudRepository<Exercise,Integer> {
	List<Exercise> findByAuthor(int userId);
	@Query(value = "select * from exercise  where author = ?1 order by id DESC" , nativeQuery = true)
	List<Exercise> findExerbyId(int userId);
	// get exercise name by id
	@Query(value = "select name from exercise  where id = ?1" , nativeQuery = true)
	String getExerciseNameById(int id);
	
	// get part id by exercise id
	@Query(value = "select part from exercise  where id = ?1" , nativeQuery = true)
	int getPartIdById(int id);
	
	Exercise findById(int exerciseId);
	
	@Query(value = "select * from exercise  where name = ?1", nativeQuery = true)
	List<Exercise> findName(String exerName);
	
	@Query(value = "select * from exercise  where name = ?1 and name != ?2", nativeQuery = true)
	List<Exercise> CheckName(String NewName, String OldName);
	
}
