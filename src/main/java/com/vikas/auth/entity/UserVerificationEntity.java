package com.vikas.auth.entity;

import java.time.LocalDateTime;

import com.vikas.enums.OwnerVerificationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class      : UserVerificationEntity
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Jul 13, 2026
 * Version    : 1.0
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "USER_VERIFICATION", schema = "userService", indexes = {
		@Index(name = "IDX_UV_USER_ID", columnList = "USER_ID"),
		@Index(name = "IDX_UV_STATUS", columnList = "status") })
public class UserVerificationEntity extends AuditableEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 🔗 Many verification attempts per user (history)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "USER_ID", nullable = false)
	private UserEntity user;

	// ---- Submitted documents ----
	@Column(nullable = false, length = 20)
	private String idDocType; // AADHAAR / PAN / PASSPORT / VOTER_ID

	@Column(nullable = false, length = 50)
	private String idDocNumber;

	@Column(name = "id_doc_url", nullable = false, columnDefinition = "TEXT")
	private String idDocUrl; // uploaded ID proof file

	@Column(name = "ownership_proof_url", columnDefinition = "TEXT")
	private String ownershipProofUrl; // property ownership proof (optional?)
	
	@Column(length = 30)
	private String ownershipProofType;  // TAX_RECEIPT / UTILITY_BILL / SALE_DEED / RENT_AGREEMENT / OTHER

	// ---- Review state ----
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	@Builder.Default
	private OwnerVerificationStatus status = OwnerVerificationStatus.PENDING;

	@Column(nullable = false)
	private LocalDateTime submittedAt;

	@Column
	private Long reviewedByAdminId; // which admin reviewed

	@Column
	private LocalDateTime reviewedAt;

	@Column(length = 500)
	private String rejectionReason;

}
