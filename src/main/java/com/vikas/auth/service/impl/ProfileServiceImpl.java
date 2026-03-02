package com.vikas.auth.service.impl;

import org.springframework.stereotype.Service;

import com.vikas.auth.dto.UpdateProfileRequest;
import com.vikas.auth.dto.UserProfileResponse;
import com.vikas.auth.entity.UserEntity;
import com.vikas.auth.exception.AuthServiceException;
import com.vikas.auth.repository.UserRepository;
import com.vikas.auth.service.ProfileService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Class      : ProfileServiceImpl
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Mar 1, 2026
 * Version    : 1.0
 */

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileServiceImpl implements ProfileService {

	 private final UserRepository userRepository;

	    /**
	     * =========================================================
	     * 1️⃣ Fetch Logged-in User Profile
	     * =========================================================
	     */
	    @Override
	    public UserProfileResponse getMyProfile(String username) {

	        // Step 1: Fetch user from DB
	        UserEntity user = userRepository.findByUsername(username)
	                .orElseThrow(() -> new AuthServiceException("User not found"));

	        // Step 2: Convert entity → safe DTO
	        return mapToResponse(user);
	    }

	    /**
	     * =========================================================
	     * 2️⃣ Update Basic Profile
	     * =========================================================
	     */
	    @Override
	    public UserProfileResponse updateProfile(String username,
	                                             UpdateProfileRequest request) {

	        // Step 1: Fetch user from DB
	        UserEntity user = userRepository.findByUsername(username)
	                .orElseThrow(() -> new AuthServiceException("User not found"));

	        // Step 2: Update allowed fields only
	        if (request.getFullName() != null)
	            user.setFullName(request.getFullName());

	        if (request.getEmail() != null)
	            user.setEmail(request.getEmail());

	        // Step 3: Save updated entity
	        userRepository.save(user);

	        // Step 4: Return updated response
	        return mapToResponse(user);
	    }

	    /**
	     * Helper method: Convert Entity → DTO
	     */
	    private UserProfileResponse mapToResponse(UserEntity user) {

	        return UserProfileResponse.builder()
	                .fullName(user.getFullName())
	                .username(user.getUsername())
	                .email(user.getEmail())
	                .role(user.getRole())
	                .enabled(user.getEnabled())
	                .accountNonLocked(user.getAccountNonLocked())
	                .failedLoginAttempts(user.getFailedLoginAttempts())
	                .passwordLastUpdatedAt(user.getPasswordLastUpdatedAt())
	                .build();
	    }}
