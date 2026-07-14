package com.vikas.mapper;
import org.springframework.stereotype.Component;

import com.vikas.auth.dto.UserVerificationResponse;
import com.vikas.auth.dto.UserVerificationStatusResponse;
import com.vikas.auth.entity.UserVerificationEntity;

/**
 * Class      : UserVerificationMapper
 * Description: Maps UserVerification entity to response DTOs.
 * Author     : Vikas Yadav
 * Version    : 1.0
 */
@Component
public class UserVerificationMapper {

	/**
	 * Full view of a verification submission (used by owner detail view + admin
	 * review).
	 */
	public UserVerificationResponse toResponse(UserVerificationEntity entity) {
		if (entity == null) {
			return null;
		}
		return new UserVerificationResponse(entity.getId(), entity.getUser() != null ? entity.getUser().getId() : null,
				entity.getUser() != null ? entity.getUser().getFullName() : null, entity.getIdDocType(),
				entity.getIdDocNumber(), entity.getIdDocUrl(), entity.getOwnershipProofType(),
				entity.getOwnershipProofUrl(), entity.getStatus(), entity.getSubmittedAt(),
				entity.getReviewedByAdminId(), entity.getReviewedAt(), entity.getRejectionReason());
	}

	/**
	 * Lightweight status view (used for badge / profile popup).
	 */
	public UserVerificationStatusResponse toStatusResponse(UserVerificationEntity entity) {
		if (entity == null) {
			return null;
		}
		return new UserVerificationStatusResponse(entity.getStatus(), entity.getRejectionReason());
	}
}