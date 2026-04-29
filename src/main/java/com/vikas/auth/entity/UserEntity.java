package com.vikas.auth.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

/**
 * Class      : UserEntity
 * Description: Stores permanent authentication and security state
 * Author     : Vikas Yadav
 * Version    : 3.0 (Production Ready)
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "APP_USERS",schema = "userService",
       indexes = {
           @Index(name = "IDX_USERNAME", columnList = "username")
       })
public class UserEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= BASIC AUTH =================
    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false)
    private String password; // Always store hashed password
    
    @Column(unique = true,nullable = false)
    private String email;

    @Column(nullable = false, length = 50)
    private String role; // ROLE_USER / ROLE_ADMIN


    // ================= PASSWORD CONTROL =================

    @Column(nullable = false)
    @Builder.Default
    private Integer passwordVersion = 1;

    @Column(nullable = false)
    private LocalDateTime passwordLastUpdatedAt;


    // ================= ACCOUNT SECURITY =================

    @Column(nullable = false)
    @Builder.Default
    private Boolean accountNonLocked = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @Column(nullable = false)
    @Builder.Default
    private Integer failedLoginAttempts = 0;
    
    @Column
    private LocalDateTime accountLockedAt;

    // ================= OPTIMISTIC LOCKING =================

    @Version
    private Long version;   // Prevents concurrent update issues
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}