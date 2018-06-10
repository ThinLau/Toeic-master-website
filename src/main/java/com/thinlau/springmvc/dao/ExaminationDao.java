package com.thinlau.springmvc.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.thinlau.springmvc.model.Examination;

public interface ExaminationDao extends CrudRepository<Examination,Integer> {
	
	List<Examination> findByAuthor(int userId);
	@Query(value = "select name from examination  where id = ?1", nativeQuery = true)
	String getExamNameById(int id);
}
