package com.vikas.auth.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class      : AllUsersResponse
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Mar 5, 2026
 * Version    : 1.0
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserResponse {
	private Long id;
	private String fullName;
	private String username;
	private String email;
	private String role;
	private Boolean enabled;
	private Boolean accountNonLocked;
	private Integer failedLoginAttempts;
	private LocalDateTime accountLockedAt;
	private LocalDateTime createdAt;
}
