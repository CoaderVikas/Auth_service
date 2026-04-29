package com.vikas.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


import lombok.RequiredArgsConstructor;

/**
 * Class      : SecurityConfig
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 17, 2026
 * Version    : 1.0
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Enable method-level security with @PreAuthorize
@RequiredArgsConstructor
public class SecurityConfig {
	
	//@Autowired
	//private JwtAuthenticationFilter jwtFilter;
	

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(
						auth -> auth
						.requestMatchers(
								"/rent-hub/auth/**",
								"/swagger-ui/**",
								"/v3/api-docs/**",
								"/swagger-ui.html",
								"/instances/**",
								"/actuator/**"
								).permitAll())
						//.anyRequest()
						//.authenticated())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		//http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
	
	
	/** can call directly from controller but as of now we are not using it in controller */
	@Bean
	AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
	    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
	    provider.setPasswordEncoder(passwordEncoder);
	    return provider;
	}

}