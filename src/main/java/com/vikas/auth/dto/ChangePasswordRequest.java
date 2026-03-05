package com.vikas.auth.dto;

import lombok.Data;

/**
 * Class      : ChangePasswordRequest
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Mar 5, 2026
 * Version    : 1.0
 */

@Data
public class ChangePasswordRequest {

    private String currentPassword;
    private String newPassword;

}