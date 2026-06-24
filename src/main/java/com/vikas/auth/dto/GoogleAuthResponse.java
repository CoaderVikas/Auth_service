package com.vikas.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class      : GoogleAuthResponse
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Jun 23, 2026
 * Version    : 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleAuthResponse {
    private String token;
    private String role;
    private String username;
    private String name;
}
