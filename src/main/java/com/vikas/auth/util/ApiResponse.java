package com.vikas.auth.util;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Class      : ApiResponse
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 17, 2026
 * Version    : 1.0
 */

@Data
@RequiredArgsConstructor
public class ApiResponse<T> {
    private final String message;

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(message);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(message);
    }
}