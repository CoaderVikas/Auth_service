package com.vikas.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
/**
 * Class      : VerifyOtpRequest
 * Description: Request body for verifying an OTP entered by the user.
 * Author     : Vikas Yadav
 * Created On : Jul 15, 2026
 * Version    : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpRequest {
	private String username;
	private String email;
	private String otp;
}
 