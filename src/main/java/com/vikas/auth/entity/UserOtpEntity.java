package com.vikas.auth.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

/**
 * Class      : UserOtpEntity
 * Description: Stores OTP for password reset / verification with tracking
 * Author     : Vikas Yadav
 * Version    : 2.0 (Production Ready)
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "USER_OTP",
       indexes = {
           @Index(name = "IDX_USER_ID", columnList = "USER_ID"),
           @Index(name = "IDX_EXPIRY_TIME", columnList = "expiryTime")
       })
public class UserOtpEntity extends AuditableEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Many OTPs per user (history maintained)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserEntity user;

    @Column(nullable = false, length = 100)
    private String otp;  // Store HASHED OTP only

    @Column(nullable = false, length = 30)
    private String purpose; 
    // RESET_PASSWORD / VERIFY_EMAIL / LOGIN_2FA

    @Column(nullable = false)
    private LocalDateTime expiryTime;

    @Column(nullable = false)
    @Builder.Default
    private Boolean used = false;

    @Column(nullable = false)
    @Builder.Default
    private Integer attemptCount = 0;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
}