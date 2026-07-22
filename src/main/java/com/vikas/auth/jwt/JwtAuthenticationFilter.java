package com.vikas.auth.jwt;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JwtAuthenticationFilter
 *
 * Responsibilities: 
 * 	1. Intercept all requests except public endpoints 
 *  2. Extract JWT from Authorization header 
 *  3. Validate signature and expiry 
 *  4. Extract username and role 5. Set authentication in SecurityContext
 *
 * Author: Vikas Yadav
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	
	private static final List<String> PUBLIC_PATHS = List.of(
		    "/rent-hub/api/v1/auth/password/",
		    "/rent-hub/api/v1/auth/password/reset",
		   "/rent-hub/api/v1/auth/phone/reset",
		    "/rent-hub/api/v1/auth/jwt/refresh",
		    "/rent-hub/api/v1/auth/jwt/logout",
		    "/rent-hub/api/v1/auth/phone/reset/",
		    "/rent-hub/api/v1/auth/phone/login-otp",
		    "/rent-hub/api/v1/auth/login",
		    "/rent-hub/api/v1/auth/register",
		    "/swagger-ui/",
		    "/swagger-ui.html",
		    "/v3/api-docs",
		    "/actuator/"
		);

	@Autowired
	private JWTValidator jwt;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		String path = request.getServletPath();
		logger.info("🔹 Incoming request path: {}", path);

		// 1️⃣ Skip public endpoints
		 // Public endpoints -> JWT validation skip
	    if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
	        filterChain.doFilter(request, response);
	        return;
	    }

		// 2️⃣ Extract Authorization header
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			logger.warn("*********** Missing or invalid Authorization header ***********");
			sendError(response, "Missing or Invalid Authorization Header");
			return;
		}

		// 3️⃣ Extract token
		String token = authHeader.substring(7);

		// 4️⃣ Validate token
		if (!jwt.isTokenValid(token)) {
			logger.warn("*********** Invalid token provided *********** ");
			sendError(response, "Invalid Token");
			return;
		}

		// 5️⃣ Extract username and role
		String username = jwt.extractUsername(token);
		String role = jwt.extractRole(token);
		
		// 6️⃣ Set authorities
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role.trim()));

		// 7️⃣ Set authentication in SecurityContext
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null,
				authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// 8️⃣ Continue filter chain
		filterChain.doFilter(request, response);
	}

	private void sendError(HttpServletResponse response, String message) throws IOException {
		logger.error("🔴 Unauthorized access: {}", message);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");

		response.getWriter().write("""
				{
				  "status": 401,
				  "error": "Unauthorized",
				  "message": "%s"
				}
				""".formatted(message));
	}
}