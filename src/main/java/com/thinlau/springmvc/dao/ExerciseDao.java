package com.thinlau.springmvc.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.thinlau.springmvc.model.Exercise;

public interface ExerciseDao extends CrudRepository<Exercise,Integer> {
	List<Exercise> findByAuthor(int userId);
}
