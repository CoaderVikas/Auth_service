package com.vikas.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Class      : RegisterRequest
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 17, 2026
 * Version    : 1.0
 */

@Data
public class RegisterRequest {
	@NotBlank
	private String fullName;
	@NotBlank
	private String username;
	@NotBlank
	private String password;
	@NotBlank
	private String email;
	private String role; // optional, default ROLE_USER
}
