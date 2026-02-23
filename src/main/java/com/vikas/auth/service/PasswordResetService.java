package com.vikas.auth.service;

import com.vikas.auth.dto.PasswordResetRequest;
import com.vikas.auth.dto.PasswordResetResponse;

public interface PasswordResetService {

    /**
     * Generate OTP for password reset
     */
    void generateResetOtp(String username);

    /**
     * Verify OTP and reset password
     */
    PasswordResetResponse resetPassword(PasswordResetRequest request);
}