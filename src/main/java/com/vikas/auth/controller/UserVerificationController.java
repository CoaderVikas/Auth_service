package com.vikas.auth.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vikas.auth.dto.UserVerificationRequest;
import com.vikas.auth.dto.UserVerificationResponse;
import com.vikas.auth.dto.UserVerificationReviewRequest;
import com.vikas.auth.dto.UserVerificationStatusResponse;
import com.vikas.auth.service.UserVerificationService;
import com.vikas.auth.util.ConstantsUtils;
import com.vikas.enums.OwnerVerificationStatus;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Class      : UserVerificationController
 * Description: REST endpoints for owner (user) verification —
 *              owner submission/status/history and admin review.
 * Author     : Vikas Yadav
 * Created On : Jul 14, 2026
 * Version    : 1.0
 */
@RestController
@RequestMapping(ConstantsUtils.VERIFICATIONS)
@RequiredArgsConstructor
public class UserVerificationController {

	private final UserVerificationService verificationService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UserVerificationResponse> submitVerification(
			@Valid @RequestPart("request") UserVerificationRequest request,
			@RequestPart("idProofFile") MultipartFile idProofFile,
			@RequestPart("ownershipProofFile") MultipartFile ownershipProofFile, Authentication authentication) {

		UserVerificationResponse response = verificationService.submitVerification(request, idProofFile,
				ownershipProofFile, authentication);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/me/status")
	public ResponseEntity<UserVerificationStatusResponse> getMyStatus(Authentication authentication) {
		return ResponseEntity.ok(verificationService.getMyVerificationStatus(authentication));
	}

	@GetMapping("/me")
	public ResponseEntity<List<UserVerificationResponse>> getMyHistory(Authentication authentication) {
		return ResponseEntity.ok(verificationService.getMyVerificationHistory(authentication));
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<UserVerificationResponse>> getByStatus(
			@RequestParam(name = "status", defaultValue = "PENDING") OwnerVerificationStatus status) {
		return ResponseEntity.ok(verificationService.getVerificationsByStatus(status));
	}

	@PatchMapping("/admin/{verificationId}/review")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserVerificationResponse> reviewVerification(@PathVariable("verificationId") Long verificationId,
			@Valid @RequestBody UserVerificationReviewRequest review, Authentication authentication) {

		return ResponseEntity.ok(verificationService.reviewVerification(verificationId, review, authentication));
	}
	
	// UserVerificationController me add karo
	@GetMapping("/admin/{verificationId}/document")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Resource> getDocument(
	        @PathVariable("verificationId") Long verificationId,
	        @RequestParam("type") String type) { // type = ID_PROOF | OWNERSHIP_PROOF
	    return verificationService.loadDocument(verificationId, type);
	}
}
