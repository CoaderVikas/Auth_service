package com.vikas.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class      : PasswordResetResponse
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 21, 2026
 * Version    : 1.0
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetResponse {
    private boolean success;
    private String message;
}
