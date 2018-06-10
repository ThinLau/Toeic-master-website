package com.thinlau.springmvc.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.thinlau.springmvc.model.ExAlreadyDo;

public interface ExAlreadyDoDao extends CrudRepository<ExAlreadyDo,Integer>{
	List<ExAlreadyDo> findByUserId(int userId);
}
