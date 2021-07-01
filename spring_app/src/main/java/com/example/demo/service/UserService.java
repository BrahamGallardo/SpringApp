package com.example.demo.service;

import com.example.demo.Exception.UsernameOrIDNotFound;
import com.example.demo.dto.ChangePasswordForm;
import com.example.demo.entity.User;

public interface UserService {

	public Iterable<User> getAllUsers();
	
	public User createUser(User formUser) throws Exception;
	public User getUserById(Long id) throws UsernameOrIDNotFound;
	public User updateUser(User user) throws Exception;
	public void deleteUser(Long id) throws UsernameOrIDNotFound;
	public User changePassword(ChangePasswordForm form) throws Exception;
}