package com.vikas.auth.service.impl;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vikas.auth.dto.LoginResponse;
import com.vikas.auth.dto.PasswordResetResponse;
import com.vikas.auth.entity.RefreshTokenEntity;
import com.vikas.auth.entity.UserEntity;
import com.vikas.auth.exception.AuthServiceException;
import com.vikas.auth.jwt.JwtService;
import com.vikas.auth.repository.RefreshTokenRepository;
import com.vikas.auth.repository.UserRepository;
import com.vikas.auth.service.FirebasePhoneService;
import com.vikas.auth.service.PhoneAuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Class : PhoneAuthServiceImpl
 * Description: Firebase phone-OTP login + password reset. Refresh-token save
 *              logic AuthServiceImpl jaisi hi rakhi hai (consistency ke liye).
 *
 * Author : Vikas Yadav
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PhoneAuthServiceImpl implements PhoneAuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtProvider;
    private final FirebasePhoneService firebasePhoneService;

    @Override
    public LoginResponse loginWithPhone(String firebaseIdToken) {
        // 1. Token verify + phone nikaalo (verified truth)
        String phone = firebasePhoneService.verifyAndGetPhone(firebaseIdToken);
        log.info("Phone login attempt | phone={}", phone);

        // 2. Us phone wala user dhoondo
        UserEntity user = userRepository.findByPhone(phone)
                .orElseThrow(() -> {
                    log.warn("Phone login failed: no account for phone={}", phone);
                    return new AuthServiceException("No Account Found for this Number.");
                });

        // 3. Account status
        if (!user.getEnabled() || !user.getAccountNonLocked()) {
            log.warn("Phone login blocked: locked/disabled | username={}", user.getUsername());
            throw new AuthServiceException("Account is locked or disabled");
        }

        // 4. Success -> reset failed attempts, tokens do
        user.setFailedLoginAttempts(0);
        userRepository.save(user);

        String accessToken = jwtProvider.generateToken(
                user.getUsername(), user.getRole(), user.getPasswordVersion(), user.getFullName());
        String refreshToken = jwtProvider.generateRefreshToken(user.getUsername(), user.getRole());
        saveRefreshToken(user, refreshToken);

        log.info("Phone login successful | username={}, role={}", user.getUsername(), user.getRole());
        return LoginResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    @Override
    public PasswordResetResponse resetPasswordWithPhone(String firebaseIdToken, String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            return PasswordResetResponse.builder().success(false).message("New password required").build();
        }

        // 1. Token verify + phone
        String phone = firebasePhoneService.verifyAndGetPhone(firebaseIdToken);
        log.info("Phone password reset | phone={}", phone);

        // 2. User dhoondo
        UserEntity user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new AuthServiceException("No Account Found for this Number"));

        // 3. Account status
        if (!user.getEnabled())
            return PasswordResetResponse.builder().success(false).message("Account disabled").build();
        if (!user.getAccountNonLocked())
            return PasswordResetResponse.builder().success(false).message("Account locked").build();

        // 4. Password reset (email-OTP flow jaise hi version bump + timestamps)
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordVersion(user.getPasswordVersion() + 1);
        user.setPasswordLastUpdatedAt(LocalDateTime.now());
        user.setFailedLoginAttempts(0);
        userRepository.save(user);

        log.info("Phone password reset successful | username={}", user.getUsername());
        return PasswordResetResponse.builder()
                .success(true)
                .message("Password reset successfully, please login again")
                .build();
    }

    private void saveRefreshToken(UserEntity user, String refreshToken) {
        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .token(refreshToken)
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();
        refreshTokenRepository.save(entity);
    }
}