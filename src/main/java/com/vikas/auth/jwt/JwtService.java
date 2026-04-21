package com.vikas.auth.jwt;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

/**
 * JwtTokenGenerator
 *
 * Purpose:
 * 1. Generate signed JWT access tokens
 * 2. Embed username (subject)
 * 3. Embed role
 * 4. Embed password version (for token invalidation after password change)
 * 5. Add issuer & token type
 *
 * Author: Vikas Yadav
 */
@Component
public class JwtService {

	@Value("${jwt.secret}")
	private String secret;

	// Token expiration configurable from application.properties
	@Value("${jwt.expiration}")
	private Long expiration; // e.g. 3600000 (1 hour)

	@Value("${jwt.issuer}")
	private String issuer; // e.g. vikas-app

	private SecretKey signingKey;

	/**
	 * Initialize signing key after bean creation Converts Base64 secret into HMAC
	 * SHA key
	 */
	@PostConstruct
	public void init() {
		byte[] keyBytes = Base64.getDecoder().decode(secret);
		signingKey = Keys.hmacShaKeyFor(keyBytes);
	}

	/**
	 * Generate ACCESS token
	 *
	 * @param username        authenticated username
	 * @param role            user role (USER / ADMIN)
	 * @param passwordVersion used to invalidate old tokens after password change
	 * @return signed JWT token
	 */
	public String generateToken(String username, String role, Integer passwordVersion) {

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + expiration);

		return Jwts.builder()
				// Standard claims
				.setSubject(username)
				.setIssuer(issuer)
				.setIssuedAt(now)
				.setExpiration(expiryDate)

				// Custom claims
				.claim("role", role)
				.claim("pwdv", passwordVersion)
				.claim("type", "ACCESS")

				// Sign with HS256 algorithm
				.signWith(signingKey, SignatureAlgorithm.HS256).compact();
	}

	/**
	 * 
	 * @param username
	 * @return
	 */
	public String generateRefreshToken(String username,String role) {

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + (1000L * 60 * 60 * 24 * 7)); // 7 days

		return Jwts.builder().setSubject(username).setIssuedAt(now).setExpiration(expiryDate).claim("type", "REFRESH").claim("role", role)
				.signWith(signingKey, SignatureAlgorithm.HS256).compact();
	}
	
	/*
	public void validateToken(final String token) {
        Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
    }*/
}