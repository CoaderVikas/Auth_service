package com.vikas.auth.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class      : UserVerificationReviewRequest
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Jul 14, 2026
 * Version    : 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVerificationReviewRequest{

	@NotNull(message = "Approval decision is required")
	private Boolean approve;

	@Size(max = 500, message = "Reason must be at most 500 characters")
	private String rejectionReason;
}
