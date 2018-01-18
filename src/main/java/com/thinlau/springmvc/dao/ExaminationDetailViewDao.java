package com.thinlau.springmvc.dao;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.thinlau.springmvc.model.ExaminationDetailView;

public interface ExaminationDetailViewDao extends CrudRepository<ExaminationDetailView, Integer>{
	

	Page<ExaminationDetailView> findAll(Pageable pageAble);
}
