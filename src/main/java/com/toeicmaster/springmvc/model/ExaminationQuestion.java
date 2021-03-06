package com.toeicmaster.springmvc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="examination_question")
public class ExaminationQuestion {
	
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	private int id;
	
	@Column(name="num")
	private int num;
	
	@Column(name="sub_question")
	private int subQuestion;
	
	@Column(name="photo")
	private String photo;
	
	@Column(name="audio")
	private String audio;
	
	@Column(name="paragraph")
	private String paragraph;
	
	@Column(name="paragraph_2")
	private String paragraph2;
	
	@Column(name="examination_id")
	private int examId;
	
	@Column(name="part")
	private int part;
	

	public int getPart() {
		return part;
	}

	public void setPart(int part) {
		this.part = part;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getSubQuestion() {
		return subQuestion;
	}

	public void setSubQuestion(int subQuestion) {
		this.subQuestion = subQuestion;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getAudio() {
		return audio;
	}

	public void setAudio(String audio) {
		this.audio = audio;
	}

	public String getParagraph() {
		return paragraph;
	}

	public void setParagraph(String paragraph) {
		this.paragraph = paragraph;
	}

	public String getParagraph2() {
		return paragraph2;
	}

	public void setParagraph2(String paragraph2) {
		this.paragraph2 = paragraph2;
	}

	public int getExamId() {
		return examId;
	}

	public void setExamId(int examId) {
		this.examId = examId;
	}

	
}
