package com.vikas.auth.service.impl;

import static com.vikas.auth.util.ConstantsUtils.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.vikas.auth.dto.UpdateProfileRequest;
import com.vikas.auth.dto.UserProfileResponse;
import com.vikas.auth.entity.UserEntity;
import com.vikas.auth.exception.AuthServiceException;
import com.vikas.auth.repository.UserRepository;
import com.vikas.auth.service.ProfileService;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileServiceImpl implements ProfileService {

	private final UserRepository userRepository;
	private final RedisTemplate<String, Object> redisTemplate;

	/**
	 * 1️⃣ Preload all user profiles into Redis cache at startup
	 */
	@PostConstruct
	@Async
	public void preloadUserProfiles() {
		log.info(LOG_WARMUP_START);

		// 1a. Fetch all users from DB
		List<UserEntity> users = userRepository.findAll();

		// 1b. Map each user to DTO and store in Redis
		for (UserEntity user : users) {
			String cacheKey = USER_PROFILE_CACHE_PREFIX + user.getUsername();
			UserProfileResponse response = mapToResponse(user);
			redisTemplate.opsForValue().set(cacheKey, response, USER_PROFILE_CACHE_TTL_MINUTES, TimeUnit.MINUTES);
			log.info("Preloaded profile into cache for user: {}", user.getUsername());
		}

		log.info(LOG_WARMUP_COMPLETE, users.size());
	}

	/**
	 * 2️⃣ Fetch user profile using Cache-Aside pattern
	 */
	@Override
	@Transactional(Transactional.TxType.SUPPORTS)
	public UserProfileResponse getMyProfile(String username) {

		String cacheKey = USER_PROFILE_CACHE_PREFIX + username;
		log.info("Fetching profile for username: {}", username);

		// 2a. Try Redis cache first
		UserProfileResponse cached = (UserProfileResponse) redisTemplate.opsForValue().get(cacheKey);
		if (cached != null) {
			log.info("Cache hit: Returning profile for '{}' from Redis", username);
			return cached;
		}

		// 2b. Cache miss → fetch from DB
		log.info("Cache miss: Fetching profile for '{}' from Database", username);
		UserEntity user = userRepository.findByUsername(username)
				.orElseThrow(() -> new AuthServiceException(String.format(USER_NOT_FOUND, username)));

		UserProfileResponse response = mapToResponse(user);

		// 2c. Update Redis cache
		redisTemplate.opsForValue().set(cacheKey, response, USER_PROFILE_CACHE_TTL_MINUTES, TimeUnit.MINUTES);
		log.info("Profile cached in Redis for user: {}", username);

		return response;
	}

	/**
	 * 3️⃣ Update user profile and refresh Redis cache
	 */
	@Override
	public UserProfileResponse updateProfile(String username, UpdateProfileRequest request) {

		String cacheKey = USER_PROFILE_CACHE_PREFIX + username;
		log.info(LOG_UPDATING_PROFILE, username);

		// 3a. Fetch user from DB
		UserEntity user = userRepository.findByUsername(username)
				.orElseThrow(() -> new AuthServiceException(String.format(USER_NOT_FOUND, username)));

		// 3b. Update full name if provided
		if (StringUtils.hasText(request.getFullName())) {
			user.setFullName(request.getFullName());
			log.info("Full name updated for user: {}", username);
		}

		// 3c. Update email if provided & unique
		if (StringUtils.hasText(request.getEmail())) {
			if (!request.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
				log.warn("Attempted to update email to '{}', but it's already in use", request.getEmail());
				throw new AuthServiceException(EMAIL_ALREADY_IN_USE);
			}
			user.setEmail(request.getEmail());
			log.info("Email updated for user: {}", username);
		}

		// 3d. Map updated entity to DTO
		UserProfileResponse response = mapToResponse(user);

		// 3e. Update Redis cache
		redisTemplate.opsForValue().set(cacheKey, response, USER_PROFILE_CACHE_TTL_MINUTES, TimeUnit.MINUTES);
		log.info("Profile updated and cached for user: {}", username);

		return response;
	}

	/**
	 * 4️⃣ Helper: Convert UserEntity → UserProfileResponse DTO
	 */
	private UserProfileResponse mapToResponse(UserEntity user) {
		log.info("Mapping UserEntity to UserProfileResponse for username: {}", user.getUsername());
		return UserProfileResponse.builder().fullName(user.getFullName()).username(user.getUsername())
				.email(user.getEmail()).role(user.getRole()).enabled(user.getEnabled())
				.accountNonLocked(user.getAccountNonLocked()).failedLoginAttempts(user.getFailedLoginAttempts())
				.passwordLastUpdatedAt(user.getPasswordLastUpdatedAt()).build();
	}
}