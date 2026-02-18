package com.vikas.auth.security;

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
 * Class      : JwtProvider
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 17, 2026
 * Version    : 1.0
 */

@Component
public class JwtProvider {

	@Value("${jwt.secret}")
	private String secret;
	
	@Value("${jwt.expiration}")
	private String expiration;
	
	private SecretKey signingKey;
	

	@PostConstruct
	public void init() {
		byte[] keyBytes = Base64.getDecoder().decode(secret);
		signingKey = Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateToken(String username, String role) {
		return Jwts.builder().setSubject(username).claim("role", role).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(signingKey, SignatureAlgorithm.HS512).compact();
	}
}