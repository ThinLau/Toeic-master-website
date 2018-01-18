package com.thinlau.springmvc.model;


import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="examination_detail")
public class ExaminationDetailView {

	@Id
	private int id;
	
	@Column(name="examination_name")
	private String examinationName;
	
	
	@Column(name="date_create")
	private String dateCreate;
	
	@Column(name="author")
	private String author;
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}




	public String getExaminationName() {
		return examinationName;
	}

	public void setExaminationName(String examinationName) {
		this.examinationName = examinationName;
	}

	public String getDateCreate() {
		return dateCreate;
	}

	public void setDateCreate(String dateCreate) {
		this.dateCreate = dateCreate;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	
	
}
