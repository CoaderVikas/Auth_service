package com.vikas.auth.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vikas.auth.dto.LoginRequest;
import com.vikas.auth.dto.LoginResponse;
import com.vikas.auth.dto.RegisterRequest;
import com.vikas.auth.entity.UserEntity;
import com.vikas.auth.exception.AuthServiceException;
import com.vikas.auth.repositoy.UserRepository;
import com.vikas.auth.security.JwtTokenGenerator;
import com.vikas.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

/**
 * Class      : AuthServiceImpl
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 17, 2026
 * Version    : 1.0
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenGenerator jwtProvider;
    
	@Override
	public LoginResponse register(RegisterRequest request) {
		// 1 check if user exit
		if (userRepository.existsByUsername(request.getUsername())) {
			throw new AuthServiceException("Username already exists");
		}

		// 2. create user
		UserEntity user = UserEntity.builder()
									.username(request.getUsername())
									.password(passwordEncoder.encode(request.getPassword()))
									.role(request.getRole() != null ? request.getRole() : "ROLE_USER")
									.build();
		userRepository.save(user);

		//3. generate token and return response
		String token = jwtProvider.generateToken(user.getUsername(), user.getRole());
		return new LoginResponse(token, user.getUsername(), user.getRole());
	}

	@Override
	public LoginResponse login(LoginRequest request) {
		// 1.check valid user
		UserEntity user = userRepository
							.findByUsername(request.getUsername())
							.orElseThrow(() -> new RuntimeException("Invalid username or password"));
		
		//2.check if password is correct or not 
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
	            throw new AuthServiceException("Invalid username or password");
	        }
		
		//3.generate token and return response
		String token = jwtProvider.generateToken(user.getUsername(), user.getRole());
		return new LoginResponse(token, user.getUsername(), user.getRole());
	}
}
