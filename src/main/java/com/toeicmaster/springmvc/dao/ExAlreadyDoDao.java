package com.toeicmaster.springmvc.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.toeicmaster.springmvc.model.ExAlreadyDo;

public interface ExAlreadyDoDao extends CrudRepository<ExAlreadyDo,Integer>{
	List<ExAlreadyDo> findByUserId(int userId);
}
