package com.vikas.auth.audit;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
* Class      : UserEntity
* Description: [Add brief description here]
* Author     : Vikas Yadav
* Created On : Feb 17, 2026
* Version    : 1.0
*/

@Component("auditorProvider")
@Slf4j
public class AuditorAwareImpl implements AuditorAware<String> {

	/*@Override
	public Optional<String> getCurrentAuditor() {
		return Optional.of("Vikas Yadav");
	}*/
	
	
	// it will work when spring security code is ready
	@Override
	public Optional<String> getCurrentAuditor() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// 🔴 No authentication object
		if (authentication == null) {
			log.warn("AuditorAware: Authentication is NULL, returning SYSTEM");
			return Optional.of("SYSTEM");
		}

		// 🔴 Anonymous user case
		if (!authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {

			log.warn("AuditorAware: Anonymous user detected -> {}", authentication);
			return Optional.of("SYSTEM");
		}

		// 🟢 Valid user
		String username = authentication.getName();
		log.info("AuditorAware: Authenticated user -> {}", username);

		return Optional.of(username);
	}
}