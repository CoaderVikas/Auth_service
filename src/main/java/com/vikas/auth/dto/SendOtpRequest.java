package com.vikas.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* Class      : SendOtpRequest
* Description: Request body for triggering an OTP email.
* Author     : Vikas Yadav
* Created On : Jul 15, 2026
* Version    : 1.0
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendOtpRequest {
	private String email;
}