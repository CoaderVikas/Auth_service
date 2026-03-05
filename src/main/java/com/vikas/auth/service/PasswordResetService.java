package com.vikas.auth.service;

import com.vikas.auth.dto.ChangePasswordRequest;
import com.vikas.auth.dto.ChangePasswordResponse;
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
    /**
     * 
     * @param username
     * @param request
     * @return
     */
    ChangePasswordResponse changePassword(String username, ChangePasswordRequest request);
}