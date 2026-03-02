package com.vikas.auth.util;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * Class      : ApiErrorResponse
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Mar 2, 2026
 * Version    : 1.0
 */

/**
 * Common API Error Response for all exceptions
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String message;

    // For single-field validation error
    private String field;
    private String error;
}
