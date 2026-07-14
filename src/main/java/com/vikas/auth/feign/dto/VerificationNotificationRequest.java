package com.vikas.auth.feign.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class      : VerificationNotificationRequest
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Jul 14, 2026
 * Version    : 1.0
 */

@SuppressWarnings("serial")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationNotificationRequest implements Serializable {
	private String recipientId;
	private String event;
	private String propertyId;
	private String role;
	//private String propertyName;
	private String ownerId;
	private String message;
	private String reason;
}