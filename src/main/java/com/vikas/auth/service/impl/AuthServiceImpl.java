package com.vikas.auth.service.impl;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vikas.auth.dto.LoginRequest;
import com.vikas.auth.dto.LoginResponse;
import com.vikas.auth.dto.MailRequest;
import com.vikas.auth.dto.MailResponse;
import com.vikas.auth.dto.RegisterRequest;
import com.vikas.auth.entity.RefreshTokenEntity;
import com.vikas.auth.entity.UserEntity;
import com.vikas.auth.exception.AuthServiceException;
import com.vikas.auth.jwt.JwtService;
import com.vikas.auth.repository.RefreshTokenRepository;
import com.vikas.auth.repository.UserRepository;
import com.vikas.auth.service.AuthService;
import com.vikas.auth.util.ConstantsUtils;
import com.vikas.feign.MailerFeignClient;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Responsibilities: 
 * 1. User Registration 
 * 2. Login (Access + Refresh Token generation) 
 * 3. Failed login tracking & auto-lock 
 * 4. Refresh token rotation 
 * 5.Logout (token revocation)
 *
 * Author: Vikas Yadav
 */

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final MailerFeignClient mailerFeignClient;

	@Override
	public LoginResponse register(RegisterRequest request) {
		log.info("Attempting user registration | username={}, email={}", request.getUsername(), request.getEmail());

		if (userRepository.existsByUsername(request.getUsername())) {
			log.warn("Registration failed: username exists | username={}", request.getUsername());
			throw new AuthServiceException("Username already exists");
		}

		if (userRepository.existsByEmail(request.getEmail())) {
			log.warn("Registration failed: email exists | email={}", request.getEmail());
			throw new AuthServiceException("Email already registered");
		}

		UserEntity user = UserEntity.builder()
				.username(request.getUsername())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(request.getRole() != null ? request.getRole() : "USER")
				.enabled(true).accountNonLocked(true)
				.failedLoginAttempts(0)
				.passwordVersion(1)
				.passwordLastUpdatedAt(LocalDateTime.now())
				.email(request.getEmail())
				.fullName(request.getFullName())
				.createdAt(LocalDateTime.now())
				.build();

		userRepository.save(user);
		log.info("User registered successfully | username={}, email={}", user.getUsername(), user.getEmail());

		String accessToken = jwtProvider.generateToken(user.getUsername(), user.getRole(), user.getPasswordVersion());

		// Send welcome email
		/*try {
			MailRequest mailRequest = prepareWelcomeMail(user);
			MailResponse mailResponse = mailerFeignClient.sendMail(mailRequest);
			if (mailResponse.isSuccess()) {
				log.info("Welcome email sent successfully | username={}, email={}", user.getUsername(),
						user.getEmail());
			} else {
				log.error("Failed to send welcome email | username={}, email={}", user.getUsername(), user.getEmail());
			}
		} catch (Exception e) {
			log.error("Exception sending welcome email | username={}, email={}, error={}", user.getUsername(),
					user.getEmail(), e.getMessage(), e);
		}*/

		return LoginResponse.builder().token(accessToken).username(user.getUsername())
				.message("Account created successfully").role(user.getRole()).build();
	}

	@Override
	public LoginResponse login(LoginRequest request) {
		log.info("Login attempt | username={}", request.getUsername());

		UserEntity user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> {
			log.warn("Login failed: username not found | username={}", request.getUsername());
			return new AuthServiceException("Invalid username or password");
		});

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			incrementFailedLogin(user);
			log.warn("Login failed: invalid password | username={}, failedAttempts={}", user.getUsername(),
					user.getFailedLoginAttempts());
			throw new AuthServiceException("Invalid username or password");
		}

		if (!user.getEnabled() || !user.getAccountNonLocked()) {
			log.warn("Login blocked: account locked/disabled | username={}", user.getUsername());
			throw new AuthServiceException("Account is locked or disabled");
		}

		user.setFailedLoginAttempts(0);
		userRepository.save(user);

		String accessToken = jwtProvider.generateToken(user.getUsername(), user.getRole(), user.getPasswordVersion());
		String refreshToken = jwtProvider.generateRefreshToken(user.getUsername());
		saveRefreshToken(user, refreshToken);

		log.info("Login successful | username={}, role={}", user.getUsername(), user.getRole());
		return LoginResponse.builder().token(accessToken).refreshToken(refreshToken).username(user.getUsername())
				.role(user.getRole()).build();
	}

	@Override
	public LoginResponse refreshToken(String refreshToken) {
		log.info("Refresh token attempt");

		RefreshTokenEntity storedToken = refreshTokenRepository.findByToken(refreshToken).orElseThrow(() -> {
			log.warn("Refresh token invalid");
			return new AuthServiceException("Invalid refresh token");
		});

		if (storedToken.isRevoked() || storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
			log.warn("Refresh token expired or revoked | token={}", refreshToken);
			throw new AuthServiceException("Refresh token expired or revoked");
		}

		UserEntity user = storedToken.getUser();
		storedToken.setRevoked(true);
		refreshTokenRepository.save(storedToken);

		String newAccessToken = jwtProvider.generateToken(user.getUsername(), user.getRole(),
				user.getPasswordVersion());
		String newRefreshToken = jwtProvider.generateRefreshToken(user.getUsername());
		saveRefreshToken(user, newRefreshToken);

		log.info("Refresh token rotated successfully | username={}", user.getUsername());
		return LoginResponse.builder().token(newAccessToken).refreshToken(newRefreshToken).build();
	}

	private void saveRefreshToken(UserEntity user, String refreshToken) {
		RefreshTokenEntity entity = RefreshTokenEntity.builder().token(refreshToken).user(user)
				.expiryDate(LocalDateTime.now().plusDays(7)).revoked(false).build();
		refreshTokenRepository.save(entity);
		log.info("Refresh token saved | username={}, expiry={}", user.getUsername(), entity.getExpiryDate());
	}

	private void incrementFailedLogin(UserEntity user) {
		int attempts = user.getFailedLoginAttempts() + 1;
		user.setFailedLoginAttempts(attempts);

		if (attempts >= ConstantsUtils.MAX_FAILED_LOGIN_ATTEMPTS) {
			user.setAccountNonLocked(false);
			user.setAccountLockedAt(LocalDateTime.now());
			log.warn("User account locked due to failed attempts | username={}, attempts={}", user.getUsername(),
					attempts);
		} else {
			log.warn("Incremented failed login attempts | username={}, attempts={}", user.getUsername(), attempts);
		}

		userRepository.save(user);
	}

	@Override
	public void logout(String refreshToken) {
		RefreshTokenEntity token = refreshTokenRepository.findByToken(refreshToken).orElseThrow(() -> {
			log.warn("Logout failed: invalid refresh token");
			return new AuthServiceException("Invalid refresh token");
		});
		token.setRevoked(true);
		refreshTokenRepository.save(token);
		log.info("User logged out successfully | username={}", token.getUser().getUsername());
	}

	private MailRequest prepareWelcomeMail(UserEntity user) {
		return MailRequest.builder().to(user.getEmail()).toName(user.getFullName()).subject("Welcome to Our App!")
				.templateName("welcome")
				.body("Hello " + user.getFullName() + ",\n\n"
						+ "Welcome to Our App! We're excited to have you on board. "
						+ "Get started by exploring our features and managing your profile.\n\n"
						+ "Happy journey,\nThe App Team")
				.actionUrl("https://yourapp.com/dashboard").build();
	}
}