package com.vikas.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vikas.auth.dto.GoogleAuthRequest;
import com.vikas.auth.dto.GoogleAuthResponse;
import com.vikas.auth.service.GoogleAuthService;
import com.vikas.auth.util.ConstantsUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Class      : GoogleAuthController
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Jun 23, 2026
 * Version    : 1.0
 */

@RestController
@RequestMapping(ConstantsUtils.API_V1)
@RequiredArgsConstructor
@Slf4j
public class GoogleAuthController {

	private final GoogleAuthService googleAuthService;

	@PostMapping("/google")
	public ResponseEntity<?> googleAuth(@RequestBody GoogleAuthRequest request) {
		try {
			GoogleAuthResponse response = googleAuthService.authenticateWithGoogle(request);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			log.error("Google auth failed: {}", e.getMessage());
			return ResponseEntity.status(401).body("Google authentication failed: " + e.getMessage());
		}
	}
}