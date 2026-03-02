package com.vikas.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.vikas.auth.dto.UpdateProfileRequest;
import com.vikas.auth.dto.UserProfileResponse;
import com.vikas.auth.service.ProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles authenticated user profile operations.
 */
@RestController
@RequestMapping("/rent-hub/auth/profile")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "User Profile", description = "APIs for managing logged-in user profile")
public class ProfileController {

	private final ProfileService profileService;

	/**
	 * Get logged-in user profile
	 */
	@Operation(summary = "Get My Profile", description = "Fetches profile details of the currently authenticated user")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Profile fetched successfully"),
			@ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT"),
			@ApiResponse(responseCode = "404", description = "User not found"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@GetMapping("/me")
	public ResponseEntity<UserProfileResponse> getMyProfile(Authentication authentication) {

		String username = authentication.getName();
		log.info("Fetching profile for user: {}", username);

		UserProfileResponse response = profileService.getMyProfile(username);

		log.debug("Profile fetched successfully for user: {}", username);
		return ResponseEntity.ok(response);
	}

	/**
	 * Update logged-in user profile
	 */
	@Operation(summary = "Update My Profile", description = "Updates full name and email of the authenticated user")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid request data"),
			@ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT"),
			@ApiResponse(responseCode = "404", description = "User not found"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@PutMapping("/me")
	public ResponseEntity<UserProfileResponse> updateProfile(Authentication authentication,
			@Valid @RequestBody UpdateProfileRequest request) {

		String username = authentication.getName();
		log.info("Updating profile for user: {}", username);

		UserProfileResponse response = profileService.updateProfile(username, request);

		log.debug("Profile updated successfully for user: {}", username);
		return ResponseEntity.ok(response);
	}
}