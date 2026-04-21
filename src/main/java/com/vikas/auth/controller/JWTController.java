package com.vikas.auth.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vikas.auth.dto.ChangePasswordRequest;
import com.vikas.auth.dto.ChangePasswordResponse;
import com.vikas.auth.dto.RefreshRequest;
import com.vikas.auth.service.AuthService;
import com.vikas.auth.service.PasswordResetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Class      : JWTController
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Mar 2, 2026
 * Version    : 1.0
 */

@RestController
@RequestMapping("/rent-hub/auth/jwt")
@RequiredArgsConstructor
@Tag(
	    name = "JWT APIs",
	    description = "APIs for refresh and logout tokens and change password for logged-in users"
	)
public class JWTController {

	private final AuthService authService;
	private final PasswordResetService passwordResetService;

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest request) {
		return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
	}

	@PostMapping("/logout")
	@Operation(summary = "Logout User", description = "Revoke the refresh token to logout the user")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "User logged out successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid refresh token") })
	public ResponseEntity<String> logout(@Valid @RequestBody RefreshRequest request) {
		authService.logout(request.getRefreshToken());
		return ResponseEntity.ok().body("{\"message\":\"Logged out successfully\"}");
	}
	
	@PutMapping(value = "/change", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Change Password", description = "Allows a logged-in user to change password using current password")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Password changed successfully"),
			@ApiResponse(responseCode = "400", description = "Current password incorrect or validation failed"),
			@ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	public ResponseEntity<ChangePasswordResponse> changePassword(@RequestBody ChangePasswordRequest request,
			Authentication authentication) {

		String username = authentication.getName();

		ChangePasswordResponse response = passwordResetService.changePassword(username, request);

		return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
	}

}
