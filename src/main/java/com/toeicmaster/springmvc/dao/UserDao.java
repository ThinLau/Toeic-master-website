package com.toeicmaster.springmvc.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.toeicmaster.springmvc.model.Examination;
import com.toeicmaster.springmvc.model.User;

public interface UserDao extends CrudRepository<User,Integer> {
	User findByUsername(String username);
	User findByPassword(String password);
	User findByUsernameAndPassword(String username, String password);
	User findById(int userId);
	
}
