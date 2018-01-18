package com.thinlau.springmvc.dao;

import org.springframework.data.repository.CrudRepository;

import com.thinlau.springmvc.model.Part;

public interface PartDao extends CrudRepository<Part,Integer> {

}
