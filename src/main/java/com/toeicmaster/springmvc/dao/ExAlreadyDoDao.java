package com.toeicmaster.springmvc.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.toeicmaster.springmvc.model.ExAlreadyDo;

@Transactional
public interface ExAlreadyDoDao extends CrudRepository<ExAlreadyDo,Integer>{
	List<ExAlreadyDo> findByUserId(int userId);
	
	ExAlreadyDo findByExerciseIdAndUserId(int exerciseId, int userId);
	
	ExAlreadyDo findByExamIdAndUserId(int examId, int userId);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM ExAlreadyDo e WHERE e.exerciseId = :exerciseId")
	public void deleteExerciseAlreadyDo(@Param("exerciseId") int exericseId);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM ExAlreadyDo e WHERE e.examId = :examId")
	public void deleteExamAlreadyDo(@Param("examId") int examId);

	ExAlreadyDo findById(int exAlreadyDoId);
	
}
