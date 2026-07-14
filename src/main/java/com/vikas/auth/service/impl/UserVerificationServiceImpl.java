package com.vikas.auth.service.impl;

import java.time.LocalDateTime;
import java.util.List;

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
import com.vikas.auth.repository.UserRepository;
import com.vikas.auth.repository.UserVerificationRepository;
import com.vikas.auth.service.UserVerificationService;
import com.vikas.auth.util.UserUtils;
import com.vikas.enums.OwnerVerificationStatus;
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
		String ownershipProofUrl = UserUtils.storeVerificationDocument(ownershipProofFile, String.valueOf(user.getId()),
				"ownership_proof");

		// Build submission
		UserVerificationEntity verification = UserVerificationEntity.builder().user(user)
				.idDocType(request.getIdDocType()).idDocNumber(request.getIdDocNumber()).idDocUrl(idDocUrl)
				.ownershipProofType(request.getOwnershipProofType()).ownershipProofUrl(ownershipProofUrl)
				.status(OwnerVerificationStatus.PENDING).submittedAt(LocalDateTime.now()).build();

		verification = verificationRepository.save(verification);

		// Sync user-level status -> PENDING
		user.setOwnerVerificationStatus(OwnerVerificationStatus.PENDING);
		userRepository.save(user);

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
}
