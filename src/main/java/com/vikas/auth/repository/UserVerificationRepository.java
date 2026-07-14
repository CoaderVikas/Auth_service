package com.vikas.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vikas.auth.entity.UserVerificationEntity;
import com.vikas.enums.OwnerVerificationStatus;
/**
 * Class      : UserVerificationRepository
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Jul 13, 2026
 * Version    : 1.0
 */
public interface UserVerificationRepository extends JpaRepository<UserVerificationEntity, Long> {

	// Latest submission by a user (history maintained, so order by id/submittedAt // desc)
	Optional<UserVerificationEntity> findTopByUser_IdOrderBySubmittedAtDesc(Long userId);

	// All submissions of a user (history)
	List<UserVerificationEntity> findByUser_IdOrderBySubmittedAtDesc(Long userId);

	// Admin queue: all by status (e.g. PENDING)
	List<UserVerificationEntity> findByStatusOrderBySubmittedAtAsc(OwnerVerificationStatus status);

	// Guard: does user already have a pending request?
	boolean existsByUser_IdAndStatus(Long userId, OwnerVerificationStatus status);
}
