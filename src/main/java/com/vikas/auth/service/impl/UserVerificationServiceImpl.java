package com.vikas.auth.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.vikas.auth.dto.UserVerificationRequest;
import com.vikas.auth.dto.UserVerificationResponse;
import com.vikas.auth.dto.UserVerificationReviewRequest;
import com.vikas.auth.dto.UserVerificationStatusResponse;
import com.vikas.auth.entity.UserEntity;
import com.vikas.auth.entity.UserVerificationEntity;
import com.vikas.auth.exception.AuthServiceException;
import com.vikas.auth.feign.dto.VerificationNotificationRequest;
import com.vikas.auth.repository.UserRepository;
import com.vikas.auth.repository.UserVerificationRepository;
import com.vikas.auth.service.UserVerificationService;
import com.vikas.auth.util.ConstantsUtils;
import com.vikas.auth.util.UserUtils;
import com.vikas.enums.OwnerVerificationStatus;
import com.vikas.feign.NotificationServiceFeignClient;
import com.vikas.mapper.UserVerificationMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Class      : UserVerificationServiceImpl
 * Description: Owner (user) verification: submission, status, history, admin review.
 * Author     : Vikas Yadav
 * Created On : Jul 14, 2026
 * Version    : 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserVerificationServiceImpl implements UserVerificationService {

	private final UserVerificationRepository verificationRepository;
	private final UserRepository userRepository;
	private final UserVerificationMapper verificationMapper;
	private final NotificationServiceFeignClient notificationClient;


	// ===================== OWNER: SUBMIT =====================
	@Override
	@Transactional
	public UserVerificationResponse submitVerification(UserVerificationRequest request, MultipartFile idProofFile,
			MultipartFile ownershipProofFile, Authentication authentication) {

		UserEntity user = getCurrentUser(authentication);

		// Guard: already verified?
		if (user.getOwnerVerificationStatus() == OwnerVerificationStatus.VERIFIED) {
			throw new IllegalStateException("You are already a verified owner");
		}

		// Guard: a PENDING request already exists?
		boolean pendingExists = verificationRepository.existsByUser_IdAndStatus(user.getId(),
				OwnerVerificationStatus.PENDING);
		if (pendingExists) {
			throw new IllegalStateException("A verification request is already under review");
		}

		// Store files (reuse existing storage util)
		String idDocUrl = UserUtils.storeVerificationDocument(idProofFile, String.valueOf(user.getId()), "id_proof");
		String ownershipProofUrl = null;
		if (ownershipProofFile != null && !ownershipProofFile.isEmpty()) {
		    ownershipProofUrl = UserUtils.storeVerificationDocument(ownershipProofFile, String.valueOf(user.getId()), "ownership_proof");
		}
		// Build submission
		UserVerificationEntity verification = UserVerificationEntity.builder().user(user)
				.idDocType(request.getIdDocType()).idDocNumber(request.getIdDocNumber()).idDocUrl(idDocUrl)
				.ownershipProofType(request.getOwnershipProofType()).ownershipProofUrl(ownershipProofUrl)
				.status(OwnerVerificationStatus.PENDING).submittedAt(LocalDateTime.now()).build();

		verification = verificationRepository.save(verification);

		// Sync user-level status -> PENDING
		user.setOwnerVerificationStatus(OwnerVerificationStatus.PENDING);
		userRepository.save(user);
		
		// Notify all admins (best-effort; failure never breaks submission)
		notifyAdminsOfSubmission(user);

		log.info("Owner verification submitted by userId={}, verificationId={}", user.getId(), verification.getId());

		return verificationMapper.toResponse(verification);
	}

	// ===================== OWNER: STATUS =====================
	@Override
	@Transactional(readOnly = true)
	public UserVerificationStatusResponse getMyVerificationStatus(Authentication authentication) {
		UserEntity user = getCurrentUser(authentication);

		return verificationRepository.findTopByUser_IdOrderBySubmittedAtDesc(user.getId())
				.map(verificationMapper::toStatusResponse)
				// never submitted -> reflect user-level status (NOT_SUBMITTED)
				.orElse(new UserVerificationStatusResponse(user.getOwnerVerificationStatus(), null));
	}

	// ===================== OWNER: HISTORY =====================
	@Override
	@Transactional(readOnly = true)
	public List<UserVerificationResponse> getMyVerificationHistory(Authentication authentication) {
		UserEntity user = getCurrentUser(authentication);
		return verificationRepository.findByUser_IdOrderBySubmittedAtDesc(user.getId()).stream()
				.map(verificationMapper::toResponse).toList();
	}

	// ===================== ADMIN: QUEUE =====================
	@Override
	@Transactional(readOnly = true)
	public List<UserVerificationResponse> getVerificationsByStatus(OwnerVerificationStatus status) {
		return verificationRepository.findByStatusOrderBySubmittedAtAsc(status).stream()
				.map(verificationMapper::toResponse).toList();
	}

	// ===================== ADMIN: REVIEW =====================
	@Override
	@Transactional
	public UserVerificationResponse reviewVerification(Long verificationId, UserVerificationReviewRequest review,
			Authentication authentication) {

		UserEntity admin = getCurrentUser(authentication);

		UserVerificationEntity verification = verificationRepository.findById(verificationId)
				.orElseThrow(() -> new IllegalArgumentException("Verification not found: " + verificationId));

		// Only PENDING submissions can be reviewed
		if (verification.getStatus() != OwnerVerificationStatus.PENDING) {
			throw new IllegalStateException("This submission has already been reviewed");
		}

		UserEntity owner = verification.getUser();
		LocalDateTime now = LocalDateTime.now();

		if (Boolean.TRUE.equals(review.getApprove())) {
			verification.setStatus(OwnerVerificationStatus.VERIFIED);
			owner.setOwnerVerificationStatus(OwnerVerificationStatus.VERIFIED);
		} else {
			// reject -> reason mandatory
			if (review.getRejectionReason() == null || review.getRejectionReason().isBlank()) {
				throw new AuthServiceException("Rejection reason is required");
			}
			verification.setStatus(OwnerVerificationStatus.REJECTED);
			verification.setRejectionReason(review.getRejectionReason());
			owner.setOwnerVerificationStatus(OwnerVerificationStatus.REJECTED);
		}

		verification.setReviewedByAdminId(admin.getId());
		verification.setReviewedAt(now);

		verificationRepository.save(verification);
		userRepository.save(owner);
		
		// Notify owner of decision (best-effort)
		notifyOwnerOfDecision(owner, verification);

		log.info("Verification {} reviewed by adminId={}, decision={}", verificationId, admin.getId(),
				verification.getStatus());

		return verificationMapper.toResponse(verification);
	}

	// ===================== HELPER =====================
	private UserEntity getCurrentUser(Authentication authentication) {
		String username = authentication.getName();
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalStateException("User not found: " + username));
	}
	
	
	// ===================== NOTIFICATION =====================
	private void notifyAdminsOfSubmission(UserEntity owner) {
	    try {
	        List<UserEntity> admins = userRepository.findByRole("ROLE_ADMIN");
	        if (admins.isEmpty()) {
	            log.warn("No ROLE_ADMIN users found to notify for verification");
	            return;
	        }

	        // NAYA: role se tenant/owner decide karo
	        boolean isTenant = "ROLE_TENANT".equalsIgnoreCase(owner.getRole());
	        String who = isTenant ? "tenant" : "owner";
	        String msg = "New " + who + " verification request from " + owner.getFullName();
	        log.info("[VERIF-NOTIFY] submitter role='{}' | isTenant={} | who={} | msg='{}'",
	                owner.getRole(), isTenant, who, msg);

	        for (UserEntity admin : admins) {
	            VerificationNotificationRequest req = VerificationNotificationRequest.builder()
	                    .recipientId(admin.getUsername())
	                    .event("SUBMITTED")
	                    .ownerId(owner.getUsername())
	                    .message(msg)                    // ab "tenant"/"owner" word aayega
	                    .role(owner.getRole())           // NAYA: role field (agar DTO me hai)
	                    .build();
	            safeSendNotification(req, admin.getUsername());
	        }
	    } catch (Exception ex) {
	        log.error("Failed to notify admins of verification submission | error={}", ex.getMessage());
	    }
	}


	private void notifyOwnerOfDecision(UserEntity owner, UserVerificationEntity verification) {
	    try {
	        boolean verified = verification.getStatus() == OwnerVerificationStatus.VERIFIED;
	        String message;
	        if (verified) {
	            message = "Your owner verification was approved. You are now a Verified Owner.";
	        } else {
	            // reject: reason ko message me include karo
	            String reason = verification.getRejectionReason();
	            message = "Your document was rejected. Reason: "
	                    + (reason != null && !reason.isBlank() ? reason : "not specified")
	                    + ". Please resubmit with a valid document.";
	        }
	        VerificationNotificationRequest req = VerificationNotificationRequest.builder()
	                .recipientId(owner.getUsername())
	                .event(verified ? "VERIFIED" : "REJECTED")
	                .ownerId(owner.getUsername())
	                .message(message)
	                .reason(verified ? null : verification.getRejectionReason())
	                .build();
	        safeSendNotification(req, owner.getUsername());
	    } catch (Exception ex) {
	        log.error("Failed to notify owner of verification decision | error={}", ex.getMessage());
	    }
	}


	private void safeSendNotification(VerificationNotificationRequest req, String recipient) {
		try {
			notificationClient.createVerificationNotification(req);
			log.info("Verification notification sent | recipient={} | event={}", recipient, req.getEvent());
		} catch (Exception ex) {
			log.error("Notification call failed | recipient={} | error={}", recipient, ex.getMessage());
		}
	}

	// UserVerificationServiceImpl
	@Override
	public ResponseEntity<Resource> loadDocument(Long verificationId, String type) {
		UserVerificationEntity v = verificationRepository.findById(verificationId)
				.orElseThrow(() -> new AuthServiceException("Verification not found: " + verificationId));

		String path = ConstantsUtils.ID_PROOF.equalsIgnoreCase(type) ? v.getIdDocUrl() : v.getOwnershipProofUrl();
		if (path == null || path.isBlank()) {
			throw new AuthServiceException("Document not available for type: " + type);
		}
		try {
			Path filePath = Paths.get(path);
			Resource resource = new UrlResource(filePath.toUri());
			if (!resource.exists() || !resource.isReadable()) {
				throw new AuthServiceException("Document file missing on server: " + path);
			}
			// content-type guess (jpeg/png/webp/pdf)
			String contentType = Files.probeContentType(filePath);
			if (contentType == null)
				contentType = "application/octet-stream";

			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
		} catch (IOException e) {
			throw new AuthServiceException("Failed to read document: " + e.getMessage());
		}
	}
}
