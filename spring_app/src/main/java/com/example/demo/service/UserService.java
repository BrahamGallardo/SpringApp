package com.example.demo.service;

import com.example.demo.entity.User;

public interface UserService {

	public Iterable<User> getAllUsers();
	
	public User createUser(User formUser) throws Exception;
		
}