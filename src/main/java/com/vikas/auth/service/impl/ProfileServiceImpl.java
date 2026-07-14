package com.vikas.auth.service.impl;

import static com.vikas.auth.util.ConstantsUtils.EMAIL_ALREADY_IN_USE;
import static com.vikas.auth.util.ConstantsUtils.LOG_UPDATING_PROFILE;
import static com.vikas.auth.util.ConstantsUtils.LOG_WARMUP_COMPLETE;
import static com.vikas.auth.util.ConstantsUtils.LOG_WARMUP_START;
import static com.vikas.auth.util.ConstantsUtils.USER_NOT_FOUND;
import static com.vikas.auth.util.ConstantsUtils.USER_PROFILE_CACHE_PREFIX;
import static com.vikas.auth.util.ConstantsUtils.USER_PROFILE_CACHE_TTL_MINUTES;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.vikas.auth.dto.UpdateProfileRequest;
import com.vikas.auth.dto.UserProfileResponse;
import com.vikas.auth.entity.UserEntity;
import com.vikas.auth.exception.AuthServiceException;
import com.vikas.auth.repository.UserRepository;
import com.vikas.auth.service.ProfileService;
import com.vikas.auth.util.UserUtils;

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
	public UserProfileResponse updateProfile(String username, UpdateProfileRequest request,MultipartFile file) {

		String cacheKey = USER_PROFILE_CACHE_PREFIX + username;
		log.info(LOG_UPDATING_PROFILE, username);

		// 3a. Fetch user from DB
		UserEntity user = userRepository.findByUsername(username)
				.orElseThrow(() -> new AuthServiceException(String.format(USER_NOT_FOUND, username)));

		// 3b. Update full name if provided
		if (request != null && StringUtils.hasText(request.getFullName())) {
			user.setFullName(request.getFullName());
			log.info("Full name updated for user: {}", username);
		}

		// 3c. Update email if provided & unique
		if (request != null && StringUtils.hasText(request.getEmail())) {
			if (!request.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
				log.warn("Attempted to update email to '{}', but it's already in use", request.getEmail());
				throw new AuthServiceException(EMAIL_ALREADY_IN_USE);
			}
			user.setEmail(request.getEmail());
			log.info("Profile updated for user: {}", username);
		}
		if (file != null && !file.isEmpty()) {
			String savedPhotoPath = UserUtils.storeImage(file, username);
			user.setPhotoUrl(savedPhotoPath);
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
				.email(user.getEmail()).phone(user.getPhone()).role(user.getRole()).enabled(user.getEnabled())
				.photoUrl(user.getPhotoUrl())
				.accountNonLocked(user.getAccountNonLocked()).failedLoginAttempts(user.getFailedLoginAttempts())
				.passwordLastUpdatedAt(user.getPasswordLastUpdatedAt())
				.phoneVerified(user.getPhoneVerified())
				.build();
	}

	
	@Override
	public Resource getUserImageResource(String userId) {
		UserEntity byUsername = userRepository.findByUsername(userId)
				.orElseThrow(() -> new AuthServiceException("User not found with ID: " + userId));

		String photoUrl = byUsername.getPhotoUrl();
		if (photoUrl == null || photoUrl.isEmpty()) {
			throw new AuthServiceException("No profile image found for this tenant.");
		}

		try {
			Path filePath = Paths.get(photoUrl);
			Resource resource = new UrlResource(filePath.toUri());

			if (resource.exists() && resource.isReadable()) {
				return resource;
			} else {
				throw new AuthServiceException("Image file does not exist or is not readable on server.");
			}
		} catch (MalformedURLException e) {
			throw new AuthServiceException("Error while loading image: " + e.getMessage());
		}
	}
	
}