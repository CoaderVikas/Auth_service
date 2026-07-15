package com.vikas.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vikas.auth.dto.SendOtpRequest;
import com.vikas.auth.dto.VerifyOtpRequest;
import com.vikas.auth.service.UserEmailVerificationService;
import com.vikas.auth.util.ConstantsUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Class      : UserEmailVerificationController
 * Description: Exposes endpoints to generate/send OTP for email verification
 *              and to verify the OTP entered by the user.
 * Author     : Vikas Yadav
 * Created On : Jul 15, 2026
 * Version    : 1.0
 */
@RestController
@RequestMapping(ConstantsUtils.EMAIL)
@RequiredArgsConstructor
@Slf4j
public class UserEmailVerificationController {

	private final UserEmailVerificationService userEmailVerificationService;

	/**
	 * Generates a 6-digit OTP and sends it to the given email address.
	 *
	 * @param request contains the recipient email
	 */
	@PostMapping("/send-otp")
	public ResponseEntity<String> sendOtp(@RequestBody SendOtpRequest request) {

		log.info("*********** Request to send OTP received for email: {} ***********", request.getEmail());

		userEmailVerificationService.generateAndSendOtp(request.getEmail());

		return ResponseEntity.ok("OTP sent successfully to " + request.getEmail());
	}

	/**
	 * Verifies the OTP entered by the user and marks the email as verified
	 * against the given username.
	 *
	 * @param request contains username, email, and the entered OTP
	 */
	@PostMapping("/verify-otp")
	public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequest request,Authentication authentication) {

		log.info("*********** Request to verify OTP received | username={}, email={} ***********",
				authentication.getName(), request.getEmail());

		boolean isValid = userEmailVerificationService.verifyEmail(
				authentication.getName(), request.getEmail(), request.getOtp());

		if (isValid) {
			return ResponseEntity.ok("Email verified successfully");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP");
		}
	}
}