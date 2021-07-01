package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Exception.CustomeFieldValidationException;
import com.example.demo.Exception.UsernameOrIDNotFound;
import com.example.demo.dto.ChangePasswordForm;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	public Iterable<User> getAllUsers() {
		return userRepository.findAll();
	}

	private boolean checkUsernameAvailable(User user) throws Exception {
		Optional userFound = userRepository.findByUsername(user.getUsername());
		if (userFound.isPresent()) {
			throw new CustomeFieldValidationException("Username no disponible","username");
		}
		return true;
	}

	private boolean checkPasswordValid(User user) throws Exception {
		if (user.getConfirmPassword() == null || user.getConfirmPassword().isEmpty()) {
			throw new CustomeFieldValidationException("Confirm Password es obligatorio","confirmPassword");
		}
		
		if ( !user.getPassword().equals(user.getConfirmPassword())) {
			throw new CustomeFieldValidationException("Password y Confirm Password no son iguales","password");
		}
		return true;
	}

	@Override
	public User createUser(User user) throws Exception {
		if (checkUsernameAvailable(user) && checkPasswordValid(user)) {
			String encodePassword= bCryptPasswordEncoder.encode(user.getPassword());
			user.setPassword(encodePassword);
			user = userRepository.save(user);
		}
		return user;
	}

	@Override
	public User getUserById(Long id) throws UsernameOrIDNotFound {
		User user = userRepository.findById(id).orElseThrow(() -> new UsernameOrIDNotFound("El usuario no existe"));
		return user;
	}

	@Override
	public User updateUser(User fromUser) throws Exception {
		User toUser = getUserById(fromUser.getId());
		mapUser(fromUser, toUser);
		return userRepository.save(toUser);
	}

	/**
	 * Map everything but the password.
	 * 
	 * @param from
	 * @param to
	 */
	protected void mapUser(User from, User to) {
		to.setUsername(from.getUsername());
		to.setFirstName(from.getFirstName());
		to.setLastName(from.getLastName());
		to.setEmail(from.getEmail());
		to.setRoles(from.getRoles());
	}

	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	public void deleteUser(Long id) throws UsernameOrIDNotFound {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new UsernameOrIDNotFound("UsernotFound in deleteUser -" + this.getClass().getName()));

		userRepository.delete(user);
	}

	private boolean isLoggedUserADMIN() {
		//Obtener el usuario logeado
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		UserDetails loggedUser = null;
		Object roles = null;

		//Verificar que ese objeto traido de sesion es el usuario
		if (principal instanceof UserDetails) {
			loggedUser = (UserDetails) principal;

			roles = loggedUser.getAuthorities().stream()
					.filter(x -> "ROLE_ADMIN".equals(x.getAuthority())).findFirst()
					.orElse(null); 
		}
		return roles != null ? true : false;
	}

	public boolean loggedUserHasRole(String role) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserDetails loggedUser = null;
		Object roles = null;
		if (principal instanceof UserDetails) {
			loggedUser = (UserDetails) principal;

			roles = loggedUser.getAuthorities().stream().filter(x -> role.equals(x.getAuthority())).findFirst()
					.orElse(null); // loggedUser = null;
		}
		return roles != null ? true : false;
	}
	
	private User getLoggedUser() throws Exception {
		//Obtener el usuario logeado
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		UserDetails loggedUser = null;

		//Verificar que ese objeto traido de sesion es el usuario
		if (principal instanceof UserDetails) {
			loggedUser = (UserDetails) principal;
		}
		
		User myUser = userRepository
				.findByUsername(loggedUser.getUsername()).orElseThrow(() -> new Exception(""));
		
		return myUser;
	}

	@Override
	public User changePassword(ChangePasswordForm form) throws Exception {
		User user = getUserById(form.getId());
		
		
		if(loggedUserHasRole("ROLE_USER")) {
			if(!getLoggedUser().getUsername().equals(user.getUsername()) ) {
				throw new Exception ("Accion invalida");
			}
		}
		
		if( user.getPassword().equals(form.getNewPassword())) {
			throw new Exception ("Nuevo debe ser diferente al password actual.");
		}
		
		if( !form.getNewPassword().equals(form.getConfirmPassword())) {
			throw new Exception ("Nuevo Password y Confirm Password no coinciden.");
		}
		
		String encodePassword = bCryptPasswordEncoder.encode(form.getNewPassword());
		user.setPassword(encodePassword);
		return userRepository.save(user);
	}

}
