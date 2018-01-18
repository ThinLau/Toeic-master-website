package com.thinlau.springmvc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="examination_question_detail")
public class ExaminationQuestionDetail {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Column(name="num")
	private int num;
	
	@Column(name="num_in_exam")
	private int numInExam;
	
	@Column(name="question")
	private String question;
	
	@Column(name="option_1")
	private String option1;
	
	@Column(name="option_2")
	private String option2;
	
	@Column(name="option_3")
	private String option3;
	
	@Column(name="option_4")
	private String option4;
	

	@Column(name="correct_answer")
	private String correctAnswer;
	
	public String getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	@Column(name="examination_question_id")
	private int examQuestionId;

	@Column(name="exam_id")
	private int examId;
	
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

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getOption1() {
		return option1;
	}

	public void setOption1(String option1) {
		this.option1 = option1;
	}

	public String getOption2() {
		return option2;
	}

	public void setOption2(String option2) {
		this.option2 = option2;
	}

	public String getOption3() {
		return option3;
	}

	public void setOption3(String option3) {
		this.option3 = option3;
	}

	public String getOption4() {
		return option4;
	}

	public void setOption4(String option4) {
		this.option4 = option4;
	}

	public int getExamQuestionId() {
		return examQuestionId;
	}

	public void setExamQuestionId(int examQuestionId) {
		this.examQuestionId = examQuestionId;
	}

	public int getNumInExam() {
		return numInExam;
	}

	public void setNumInExam(int numInExam) {
		this.numInExam = numInExam;
	}

	public int getExamId() {
		return examId;
	}

	public void setExamId(int examId) {
		this.examId = examId;
	}

}
