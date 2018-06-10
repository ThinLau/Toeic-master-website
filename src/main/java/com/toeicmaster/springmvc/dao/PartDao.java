package com.toeicmaster.springmvc.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.toeicmaster.springmvc.model.Part;

public interface PartDao extends CrudRepository<Part, Integer> {
	// get part name by part id
	@Query(value = "select name from part  where id = ?1", nativeQuery = true)
	String getPartNameById(int id);
}
