package com.vikas.auth.dto;

import lombok.Data;

/**
 * Class      : GoogleAuthRequest
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Jun 23, 2026
 * Version    : 1.0
 */

@Data
public class GoogleAuthRequest {
    private String idToken;
    private String role;
}