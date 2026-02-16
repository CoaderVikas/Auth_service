package com.vikas.auth.audit;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

/**
* Class      : UserEntity
* Description: [Add brief description here]
* Author     : Vikas Yadav
* Created On : Feb 17, 2026
* Version    : 1.0
*/

@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
		return Optional.of("Vikas Yadav");
	}
	
	
	//it will work when spring security code is ready
	/*@Override
    public Optional<String> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return Optional.of("SYSTEM");
        }

        return Optional.of(auth.getName()); // logged-in username
    }*/
}