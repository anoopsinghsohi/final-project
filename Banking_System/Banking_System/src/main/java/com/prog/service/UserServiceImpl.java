package com.prog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.prog.entites.UserDtls;
import com.prog.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private BCryptPasswordEncoder passwordEncode;

	@Override
	public UserDtls createUser(UserDtls user) {

		user.setRole("ROLE_USER");
		user.setAccStatus("false");

		return userRepo.save(user);
	}

	@Override
	public boolean checkEmail(String email) {

		return userRepo.existsByEmail(email);
	}

}
