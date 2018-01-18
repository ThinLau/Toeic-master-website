package com.thinlau.springmvc.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.thinlau.springmvc.model.Instruction;

public interface InstructionDao extends CrudRepository<Instruction,Integer>  {
	Page<Instruction> findByType(String type, Pageable pageAble);
	List<Instruction> findByAuthor(String username);
}
