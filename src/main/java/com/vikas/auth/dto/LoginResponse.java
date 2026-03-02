package com.vikas.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoginResponse DTO
 *
 * Fields:
 * 1. token → Access Token
 * 2. refreshToken → Refresh Token
 * 3. username → Optional (frontend display)
 * 4. role → Optional (frontend role check)
 *
 * Author: Vikas Yadav
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    private String token;
    private String refreshToken;
    private String username;
    private String role;
}
