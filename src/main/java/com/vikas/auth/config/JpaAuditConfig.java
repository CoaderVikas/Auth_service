package com.vikas.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
* Class      : UserEntity
* Description: [Add brief description here]
* Author     : Vikas Yadav
* Created On : Feb 17, 2026
* Version    : 1.0
*/

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditConfig {

}
