package com.vikas.auth.service.impl;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vikas.auth.dto.LoginRequest;
import com.vikas.auth.dto.LoginResponse;
import com.vikas.auth.dto.RegisterRequest;
import com.vikas.auth.entity.RefreshTokenEntity;
import com.vikas.auth.entity.UserEntity;
import com.vikas.auth.exception.AuthServiceException;
import com.vikas.auth.repository.RefreshTokenRepository;
import com.vikas.auth.repository.UserRepository;
import com.vikas.auth.security.JwtTokenGenerator;
import com.vikas.auth.service.AuthService;
import com.vikas.auth.util.ConstantsUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * AuthServiceImpl
 *
 * Responsibilities:
 * 1. User Registration
 * 2. Login (Access + Refresh Token generation)
 * 3. Failed login tracking & auto-lock
 * 4. Refresh token rotation
 * 5. Logout (token revocation)
 *
 * Author: Vikas Yadav
 */

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenGenerator jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

	/**
	 * Register new user
	 */
	@Override
	public LoginResponse register(RegisterRequest request) {

		// 1️⃣ Check duplicate username
		if (userRepository.existsByUsername(request.getUsername())) {
			throw new AuthServiceException("Username already exists");
		}
		
		if (userRepository.existsByEmail(request.getEmail())) {
		    throw new AuthServiceException("Email already registered");
		}

		// 2️⃣ Create new user entity
		UserEntity user = UserEntity.builder().username(request.getUsername())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(request.getRole() != null ? request.getRole() : "USER") // FIXED (no ROLE_ prefix)
				.enabled(true).accountNonLocked(true).failedLoginAttempts(0).passwordVersion(1)
				.passwordLastUpdatedAt(LocalDateTime.now()).email(request.getEmail()).fullName(request.getFullName())
				.build();

		userRepository.save(user);

		// 3️⃣ Generate tokens
		String accessToken = jwtProvider.generateToken(user.getUsername(), user.getRole(), user.getPasswordVersion());

		//String refreshToken = jwtProvider.generateRefreshToken(user.getUsername());

		// 4️⃣ Save refresh token in DB
		//saveRefreshToken(user, refreshToken);

		// 5️⃣ Return response
		return LoginResponse.builder().token(accessToken).username(user.getUsername())
				.role(user.getRole()).build();
	}

	/**
	 * Login user and generate fresh tokens
	 */
	@Override
	public LoginResponse login(LoginRequest request) {

		UserEntity user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new AuthServiceException("Invalid username or password"));

		// Password check
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			incrementFailedLogin(user);
			throw new AuthServiceException("Invalid username or password");
		}

		if (!user.getEnabled() || !user.getAccountNonLocked()) {
			throw new AuthServiceException("Account is locked or disabled");
		}

		// Reset failed attempts
		user.setFailedLoginAttempts(0);
		userRepository.save(user);

		// Generate tokens
		String accessToken = jwtProvider.generateToken(user.getUsername(), user.getRole(), user.getPasswordVersion());

		String refreshToken = jwtProvider.generateRefreshToken(user.getUsername());

		saveRefreshToken(user, refreshToken);

		return LoginResponse.builder().token(accessToken).refreshToken(refreshToken).username(user.getUsername())
				.role(user.getRole()).build();
	}

	/**
	 * Refresh access token (WITH ROTATION 🔥)
	 */
	public LoginResponse refreshToken(String refreshToken) {

		RefreshTokenEntity storedToken = refreshTokenRepository.findByToken(refreshToken)
				.orElseThrow(() -> new AuthServiceException("Invalid refresh token"));

		if (storedToken.isRevoked() || storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
			throw new AuthServiceException("Refresh token expired or revoked");
		}

		UserEntity user = storedToken.getUser();

		// 🔥 ROTATION: revoke old refresh token
		storedToken.setRevoked(true);
		refreshTokenRepository.save(storedToken);

		// Generate new tokens
		String newAccessToken = jwtProvider.generateToken(user.getUsername(), user.getRole(),
				user.getPasswordVersion());

		String newRefreshToken = jwtProvider.generateRefreshToken(user.getUsername());

		saveRefreshToken(user, newRefreshToken);

		return LoginResponse.builder().token(newAccessToken).refreshToken(newRefreshToken).build();
	}

	/**
	 * Save refresh token in database
	 */
	private void saveRefreshToken(UserEntity user, String refreshToken) {

		RefreshTokenEntity entity = RefreshTokenEntity.builder().token(refreshToken).user(user)
				.expiryDate(LocalDateTime.now().plusDays(7)) // 7 days expiry
				.revoked(false).build();

		refreshTokenRepository.save(entity);
	}

	/**
	 * Increment failed login attempts & auto lock after threshold
	 */
	private void incrementFailedLogin(UserEntity user) {

		if (!user.getAccountNonLocked() && user.getAccountLockedAt() != null) {
			LocalDateTime unlockTime = user.getAccountLockedAt().plusHours(24);
			if (LocalDateTime.now().isAfter(unlockTime)) {
				user.setAccountNonLocked(true);
				user.setFailedLoginAttempts(0);
				user.setAccountLockedAt(null);
			}
		}

		int attempts = user.getFailedLoginAttempts() + 1;
		user.setFailedLoginAttempts(attempts);

		if (attempts >= ConstantsUtils.MAX_FAILED_LOGIN_ATTEMPTS) {
			user.setAccountNonLocked(false);
			user.setAccountLockedAt(LocalDateTime.now());
		}

		userRepository.save(user);
	}

	/**
	 * Logout user by revoking the refresh token
	 */
	public void logout(String refreshToken) {
		RefreshTokenEntity token = refreshTokenRepository.findByToken(refreshToken)
				.orElseThrow(() -> new AuthServiceException("Invalid refresh token"));
		token.setRevoked(true);
		refreshTokenRepository.save(token);
	}
}