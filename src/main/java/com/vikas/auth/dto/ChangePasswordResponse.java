package com.vikas.auth.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Class      : ChangePasswordResponse
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Mar 5, 2026
 * Version    : 1.0
 */

@Data
@Builder
public class ChangePasswordResponse {

    private boolean success;
    private String message;

}
