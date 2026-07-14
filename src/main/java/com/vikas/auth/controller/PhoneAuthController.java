package com.vikas.auth.controller;

/**
 * Class      : PhoneAuthController
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Jul 11, 2026
 * Version    : 1.0
 */

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vikas.auth.dto.LoginResponse;
import com.vikas.auth.dto.PasswordResetResponse;
import com.vikas.auth.service.PhoneAuthService;
import com.vikas.auth.util.ConstantsUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping(ConstantsUtils.PHONE)
@RequiredArgsConstructor
@Tag(name = "Phone Auth APIs", description = "Firebase phone-OTP login and password reset")
public class PhoneAuthController {

	private final PhoneAuthService phoneAuthService;

	@PostMapping(value = "/login-otp", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Passwordless Phone Login", description = "Firebase ID token verify karke us phone wale user ko JWT deta hai")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Login successful"),
			@ApiResponse(responseCode = "400", description = "Invalid token / no account"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	public ResponseEntity<LoginResponse> loginOtp(@RequestBody PhoneLoginRequest request) {
		LoginResponse response = phoneAuthService.loginWithPhone(request.getFirebaseIdToken());
		return ResponseEntity.ok(response);
	}

	@PostMapping(value = "/reset", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Phone-based Password Reset", description = "Firebase ID token verify karke us phone wale user ka password reset karta hai")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Password reset successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid token / no account"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	public ResponseEntity<PasswordResetResponse> reset(@RequestBody PhoneResetRequest request) {
		PasswordResetResponse response = phoneAuthService.resetPasswordWithPhone(request.getFirebaseIdToken(),
				request.getNewPassword());
		return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
	}
	
	@PostMapping("/verify")
	public ResponseEntity<PasswordResetResponse> verifyMyPhone(@Valid @RequestBody PhoneLoginRequest req, Authentication authentication) {
		String username = authentication.getName(); // JWT se
		PasswordResetResponse response = phoneAuthService.verifyPhoneForUser(username, req.getFirebaseIdToken());
		return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
	}


	@Data
	public static class PhoneLoginRequest {
		 @NotBlank(message = "Firebase ID token is required")
		private String firebaseIdToken;
	}

	@Data
	public static class PhoneResetRequest {
		@NotBlank
		private String firebaseIdToken;
		@NotBlank
		private String newPassword;
	}
}