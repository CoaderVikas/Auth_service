package com.vikas.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class      : UserVerificationRequest
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Jul 14, 2026
 * Version    : 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class  UserVerificationRequest {
	@NotBlank(message = "ID document type is required")
	@Size(max = 20)
	private String idDocType;

	@NotBlank(message = "ID document number is required")
	@Size(min = 5, max = 50, message = "ID number must be 5-50 characters")
	private String idDocNumber;

	@NotBlank(message = "Ownership proof type is required")
	@Size(max = 30)
	private String ownershipProofType;
}
