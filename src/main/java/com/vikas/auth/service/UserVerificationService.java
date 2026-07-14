package com.vikas.auth.service;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.vikas.auth.dto.UserVerificationRequest;
import com.vikas.auth.dto.UserVerificationResponse;
import com.vikas.auth.dto.UserVerificationReviewRequest;
import com.vikas.auth.dto.UserVerificationStatusResponse;
import com.vikas.enums.OwnerVerificationStatus;

/**
 * Class      : UserVerificationService
 * Description: Contract for owner (user) verification — submission,
 *              status lookup, history, and admin review.
 * Author     : Vikas Yadav
 * Created On : Jul 14, 2026
 * Version    : 1.0
 */
public interface UserVerificationService {

    /**
     * Owner submits documents for verification. Sets status to PENDING.
     * Rejects if a PENDING request already exists.
     *
     * @param request            metadata (doc types + numbers)
     * @param idProofFile        uploaded ID proof document
     * @param ownershipProofFile uploaded property ownership proof
     * @param authentication     current logged-in user
     * @return the created submission
     */
    UserVerificationResponse submitVerification(UserVerificationRequest request,
                                                MultipartFile idProofFile,
                                                MultipartFile ownershipProofFile,
                                                Authentication authentication);

    /**
     * Returns the current owner's latest verification status (for badge / profile).
     *
     * @param authentication current logged-in user
     */
    UserVerificationStatusResponse getMyVerificationStatus(Authentication authentication);

    /**
     * Returns the current owner's full submission history.
     *
     * @param authentication current logged-in user
     */
    List<UserVerificationResponse> getMyVerificationHistory(Authentication authentication);

    /**
     * Admin: list all submissions filtered by status (e.g. PENDING queue).
     *
     * @param status the status to filter by
     */
    List<UserVerificationResponse> getVerificationsByStatus(OwnerVerificationStatus status);

    /**
     * Admin: approve or reject a submission. On approve -> submission VERIFIED +
     * user.ownerVerificationStatus = VERIFIED. On reject -> submission REJECTED
     * (reason required) + user status = REJECTED.
     *
     * @param verificationId the submission id
     * @param review         decision + optional rejection reason
     * @param authentication current logged-in admin (recorded as reviewer)
     * @return updated submission
     */
    UserVerificationResponse reviewVerification(Long verificationId,
                                                UserVerificationReviewRequest review,
                                                Authentication authentication);

	/**
	 * @param verificationId
	 * @param type
	 * @return
	 */
	ResponseEntity<Resource> loadDocument(Long verificationId, String type);
}
