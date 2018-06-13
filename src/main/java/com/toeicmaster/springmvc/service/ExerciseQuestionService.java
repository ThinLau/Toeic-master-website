package com.toeicmaster.springmvc.service;

import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.toeicmaster.springmvc.dao.ExerciseQuestionDao;
import com.toeicmaster.springmvc.model.ExerciseQuestion;

@Service
@Transactional
public class ExerciseQuestionService {

	private final ExerciseQuestionDao exerciseQuestionDao;

	public ExerciseQuestionService(ExerciseQuestionDao exerciseQuestionDao) {
		this.exerciseQuestionDao = exerciseQuestionDao;
	}

	public List<ExerciseQuestion> findAll() {
		List<ExerciseQuestion> tasks = new ArrayList<>();
		for (ExerciseQuestion task : exerciseQuestionDao.findAll()) {
			tasks.add(task);
		}
		return tasks;
	}

	public ExerciseQuestion findTask(int id) {
		return exerciseQuestionDao.findOne(id);
	}

	public void save(ExerciseQuestion task) {
		exerciseQuestionDao.save(task);
	}

	public void delete(int id) {
		exerciseQuestionDao.delete(id);
	}
}
