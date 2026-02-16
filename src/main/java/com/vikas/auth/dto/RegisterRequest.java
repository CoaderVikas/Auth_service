package com.vikas.auth.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

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
    private String username;
    @NotBlank
    private String password;
    private String role; // optional, default ROLE_USER
}
