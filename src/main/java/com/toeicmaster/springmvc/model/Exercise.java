package com.toeicmaster.springmvc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="exercise")
public class Exercise {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="numberofquestion")
	private String numberOfQuestion;
	
	@Column(name="datecreate")
	private String dateCreate;
	
	@Column(name="author")
	private int author;
	
	// part is id of part table
	@Column(name="part")
	private int part;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumberOfQuestion() {
		return numberOfQuestion;
	}

	public void setNumberOfQuestion(String numberOfQuestion) {
		this.numberOfQuestion = numberOfQuestion;
	}

	public String getDateCreate() {
		return dateCreate;
	}

	public void setDateCreate(String dateCreate) {
		this.dateCreate = dateCreate;
	}

	public int getAuthor() {
		return author;
	}

	public void setAuthor(int author) {
		this.author = author;
	}

	public int getPart() {
		return part;
	}

	public void setPart(int part) {
		this.part = part;
	}
	
	
}
