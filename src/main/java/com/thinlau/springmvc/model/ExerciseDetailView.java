package com.thinlau.springmvc.model;


import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="exercise_detail")
public class ExerciseDetailView {

	@Id
	private int id;
	
	@Column(name="exercise_name")
	private String exerciseName;
	
	@Column(name="numberofquestion")
	private String numberOfQuestion;
	
	@Column(name="date_create")
	private String dateCreate;
	
	@Column(name="author")
	private String author;
	
	@Column(name="part_id")
	private int partId;
	
	@Column(name="part_name")
	private String partName;

	@Column(name="part_type")
	private String partType;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getExerciseName() {
		return exerciseName;
	}

	public void setExerciseName(String exerciseName) {
		this.exerciseName = exerciseName;
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

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPartName() {
		return partName;
	}

	public int getPartId() {
		return partId;
	}

	public void setPartId(int partId) {
		this.partId = partId;
	}

	public String getPartType() {
		return partType;
	}

	public void setPartType(String partType) {
		this.partType = partType;
	}

	public void setPartName(String partName) {
		this.partName = partName;
	}
	
	
}
