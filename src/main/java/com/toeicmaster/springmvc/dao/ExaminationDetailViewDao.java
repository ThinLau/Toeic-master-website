package com.toeicmaster.springmvc.dao;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.toeicmaster.springmvc.model.ExaminationDetailView;

public interface ExaminationDetailViewDao extends CrudRepository<ExaminationDetailView, Integer>{
	
	Page<ExaminationDetailView> findAll(Pageable pageAble);
	
//	@Query(value = "SELECT e FROM ExaminationDetailView e WHERE e.examinationName = ?1")
//	Page<ExaminationDetailView> findExamAll(Pageable pageAble);
	
	Page<ExaminationDetailView> findByExaminationNameIgnoreCaseContaining(String examinationName ,Pageable pageable);
	
	Page<ExaminationDetailView> findByLevel(int level ,Pageable pageable);
	
	
	@Query(value = "SELECT e FROM ExaminationDetailView e WHERE e.examinationName = ?1")
	ExaminationDetailView searchName(String examinationName);
}
