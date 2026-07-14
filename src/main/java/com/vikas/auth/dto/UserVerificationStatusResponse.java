package com.vikas.auth.dto;

import com.vikas.enums.OwnerVerificationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class      : OwnerVerificationStatusResponse
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Jul 14, 2026
 * Version    : 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVerificationStatusResponse {
	private OwnerVerificationStatus status;
	private String rejectionReason;
}
