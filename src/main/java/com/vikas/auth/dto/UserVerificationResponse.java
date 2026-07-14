package com.vikas.auth.dto;

import java.time.LocalDateTime;

import com.vikas.enums.OwnerVerificationStatus;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class      : OwnerVerificationResponse
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Jul 14, 2026
 * Version    : 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVerificationResponse{
	private Long id;
	private Long userId;
	private String role;
	private String fullName;
	private String idDocType;
	private String idDocNumber;
	private String idDocUrl;
	private String ownershipProofType;
	private String ownershipProofUrl;
	private OwnerVerificationStatus status;
	private LocalDateTime submittedAt;
	private Long reviewedByAdminId;
	private LocalDateTime reviewedAt;
	private String rejectionReason;
}
