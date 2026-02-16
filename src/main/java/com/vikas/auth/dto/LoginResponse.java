package com.vikas.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Class      : LoginResponse
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 17, 2026
 * Version    : 1.0
 */

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String username;
    private String role;
}
