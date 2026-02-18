package com.vikas.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.vikas.auth.util.ApiResponse;

/**
 * Class      : GlobalExceptionHandler
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 17, 2026
 * Version    : 1.0
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ex.getMessage()));
	}
	
	@ExceptionHandler(AuthServiceException.class)
	public ResponseEntity<ApiResponse<Object>> handleAuthServiceException(AuthServiceException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ex.getMessage()));
	}
}