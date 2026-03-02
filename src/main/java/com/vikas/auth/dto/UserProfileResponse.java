package com.vikas.auth.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

/**
 * Class      : UserProfileResponse
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Mar 1, 2026
 * Version    : 1.0
 */

@Data
@Builder
public class UserProfileResponse {
    private String fullName;
    private String username;
    private String email;
    private String role;
    private Boolean enabled;
    private Boolean accountNonLocked;
    private Integer failedLoginAttempts;
    private LocalDateTime passwordLastUpdatedAt;
}