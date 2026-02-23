package com.vikas.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class      : MailResponse
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 22, 2026
 * Version    : 1.0
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailResponse {
    private boolean success;
    private String message;
}
