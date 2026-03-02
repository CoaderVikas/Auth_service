package com.vikas.auth.dto;

import lombok.Data;

/**
 * Class      : UpdateProfileRequest
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Mar 1, 2026
 * Version    : 1.0
 */

@Data
public class UpdateProfileRequest {

    private String fullName;
    private String email;
}