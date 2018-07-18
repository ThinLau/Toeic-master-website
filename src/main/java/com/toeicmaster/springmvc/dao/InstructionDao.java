package com.toeicmaster.springmvc.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.toeicmaster.springmvc.model.Examination;
import com.toeicmaster.springmvc.model.Instruction;

public interface InstructionDao extends CrudRepository<Instruction,Integer>  {
	Page<Instruction> findByType(String type, Pageable pageAble);
	List<Instruction> findByAuthor(String username);
	@Query(value = "select * from instruction where author = ?1 order by id DESC", nativeQuery = true)
	List<Instruction> findInstructionById(String username);
	
	Instruction getInstruById(int id);
}
