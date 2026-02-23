package com.vikas.auth.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vikas.auth.dto.PasswordResetRequest;
import com.vikas.auth.dto.PasswordResetResponse;
import com.vikas.auth.service.PasswordResetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Password Reset APIs", description = "APIs for forgot password and reset password using OTP")
public class PasswordResetController {

    private final PasswordResetService authService;

    
    @PostMapping(value = "/forgot-password", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Request Password Reset OTP", description = "Generates a OTP for the given username to reset password")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OTP generated successfully and sent via email/SMS"),
        @ApiResponse(responseCode = "400", description = "Invalid username or too many OTP requests"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> generateOtp(@RequestParam(name = "username") String username) {
        try {
            authService.generateResetOtp(username);
            return ResponseEntity.ok("OTP generated successfully. Please check your email/SMS.");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

   
    @PostMapping(value = "/reset-password", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Reset Password", description = "Resets password using OTP. Requires username, OTP, and new password")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Password reset successfully"),
        @ApiResponse(responseCode = "400", description = "OTP invalid, expired, or maximum attempts exceeded"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PasswordResetResponse> resetPassword(@RequestBody PasswordResetRequest request) {
        PasswordResetResponse response = authService.resetPassword(request);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }
}