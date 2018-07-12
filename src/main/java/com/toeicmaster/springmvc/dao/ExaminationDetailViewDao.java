package com.toeicmaster.springmvc.dao;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.toeicmaster.springmvc.model.ExaminationDetailView;

public interface ExaminationDetailViewDao extends CrudRepository<ExaminationDetailView, Integer>{
	
	Page<ExaminationDetailView> findAll(Pageable pageAble);
	Page<ExaminationDetailView> findByExaminationName(String examinationName ,Pageable pageAble);
	
	
	@Query(value = "SELECT * FROM ExaminationDetailView WHERE examinationName =" +"?1", nativeQuery = true)
	ExaminationDetailView searchName(String examinationName);
}
