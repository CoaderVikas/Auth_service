package com.vikas.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class      : RefreshRequest
 * Description: DTO for sending refresh token to get new access token
 * Author     : Vikas Yadav
 * Created On : Mar 1, 2026
 * Version    : 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshRequest {
    
    /**
     * Refresh token issued earlier
     */
    private String refreshToken;
}