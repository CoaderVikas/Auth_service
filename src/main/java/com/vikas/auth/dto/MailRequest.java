package com.vikas.auth.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class : MailRequest 
 * Description: [Add brief description here] 
 * Author : Vikas
 * Yadav Created On : Feb 22, 2026 
 * Version : 1.0
 */

@Data
@Builder
@NoArgsConstructor       // ✅ Default constructor for Jackson
@AllArgsConstructor
public class MailRequest {
	private String to; 			// recipient email
	private String toName; 		// for greeting
	private String subject;
	private String body; 		// main message
	private String actionUrl; 	// for buttons like "Get Started"

	// Optional: CC & BCC
	private List<String> cc;
	private List<String> bcc;

	// OTP email specific
	private String otp;

	// Invoice email specific
	private String invoiceNumber;
	private String amount;
	private String invoiceUrl;

	// Template type: welcome, otp, invoice
	private String templateName;

}