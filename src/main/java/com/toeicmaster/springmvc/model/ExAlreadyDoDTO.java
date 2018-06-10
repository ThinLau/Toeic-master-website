package com.toeicmaster.springmvc.model;

import java.util.Date;

public class ExAlreadyDoDTO {
	private int id;
	private String exName;
	private int excerciseId;
	private int examId;
	private String logTime;
	private Double status;
	private String partName;
	
	public String getExName() {
		return exName;
	}
	public void setExName(String exName) {
		this.exName = exName;
	}
	public String getLogTime() {
		return logTime;
	}
	public void setLogTime(String logTime) {
		this.logTime = logTime;
	}
	public Double getStatus() {
		return status;
	}
	public void setStatus(Double status) {
		this.status = status;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPartName() {
		return partName;
	}
	public void setPartName(String partName) {
		this.partName = partName;
	}
	public int getExcerciseId() {
		return excerciseId;
	}
	public void setExcerciseId(int excerciseId) {
		this.excerciseId = excerciseId;
	}
	public int getExamId() {
		return examId;
	}
	public void setExamId(int examId) {
		this.examId = examId;
	}
	
}
