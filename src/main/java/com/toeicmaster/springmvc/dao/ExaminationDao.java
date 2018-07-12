package com.toeicmaster.springmvc.dao;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.toeicmaster.springmvc.model.Examination;
import com.toeicmaster.springmvc.model.Exercise;

public interface ExaminationDao extends CrudRepository<Examination,Integer> {
	
	List<Examination> findByAuthor(int userId);
	@Query(value = "select name from examination  where id = ?1", nativeQuery = true)
	String getExamNameById(int id);
	
	@Query(value = "select * from examination  where id = ?1", nativeQuery = true)
	Examination findById(int id);
	
	@Query(value = "select * from examination  where name = ?1", nativeQuery = true)
	List<Examination> findName(String examName);
	
}
