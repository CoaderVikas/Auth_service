package com.vikas.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Class      : LoginRequest
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 17, 2026
 * Version    : 1.0
 */

@Data
public class LoginRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}