package com.vikas.auth.service.impl;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vikas.auth.dto.LoginRequest;
import com.vikas.auth.dto.LoginResponse;
import com.vikas.auth.dto.RegisterRequest;
import com.vikas.auth.entity.UserEntity;
import com.vikas.auth.exception.AuthServiceException;
import com.vikas.auth.repository.UserRepository;
import com.vikas.auth.security.JwtTokenGenerator;
import com.vikas.auth.service.AuthService;
import com.vikas.auth.util.ConstantsUtils;

import jakarta.transaction.Transactional;
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
@Transactional
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenGenerator jwtProvider;
    
    
    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        // 1️⃣ Check if user already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AuthServiceException("Username already exists");
        }

        // 2️⃣ Build new user entity
        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))  // hash password
                .role(request.getRole() != null ? request.getRole() : "ROLE_USER") // default role
                .enabled(true)                       // account enabled
                .accountNonLocked(true)              // account unlocked
                .failedLoginAttempts(0)              // reset failed attempts
                .passwordVersion(1)                  // initial password version
                .passwordLastUpdatedAt(LocalDateTime.now()) // timestamp
                .email(request.getEmail())
                .build();

        // 3️⃣ Save user to DB
        userRepository.save(user);

        // 4️⃣ Generate JWT token
        String token = jwtProvider.generateToken(user.getUsername(), user.getRole());

        // 5️⃣ Return login response
        return new LoginResponse(token, user.getUsername(), user.getRole());
    }

	@Override
	public LoginResponse login(LoginRequest request) {
		// 1.check valid user
		UserEntity user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new RuntimeException("Invalid username or password"));

		// 2.check if password is correct or not
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			incrementFailedLogin(user);
			throw new AuthServiceException("Invalid username or password");
		}

		if (!user.getEnabled() || !user.getAccountNonLocked())
			throw new AuthServiceException("Your account is locked please unlock ur account");

		user.setFailedLoginAttempts(0);
		userRepository.save(user);

		// 3.generate token and return response
		String token = jwtProvider.generateToken(user.getUsername(), user.getRole());
		return new LoginResponse(token, user.getUsername(), user.getRole());
	}

	private void incrementFailedLogin(UserEntity user) {
		
		if (!user.getAccountNonLocked() && user.getAccountLockedAt() != null) {
	        LocalDateTime unlockTime = user.getAccountLockedAt().plusHours(24);
	        if (LocalDateTime.now().isAfter(unlockTime)) {
	            user.setAccountNonLocked(true);         // auto-unlock
	            user.setFailedLoginAttempts(0);         // reset failed attempts
	            user.setAccountLockedAt(null);          // clear lock timestamp
	        }
	    }
		int attempts = user.getFailedLoginAttempts() + 1;
		user.setFailedLoginAttempts(attempts);

		if (attempts >= ConstantsUtils.MAX_FAILED_LOGIN_ATTEMPTS) {
			user.setAccountNonLocked(false);
			user.setAccountLockedAt(LocalDateTime.now()); // mark lock time
		}

		userRepository.save(user);
	}
	
}
