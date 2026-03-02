package com.vikas.auth.jwt;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

/**
 * Class      : JWTValidator
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 18, 2026
 * Version    : 1.0
 */
@Component
public class JWTValidator {

	@Value("${jwt.secret}")
	private String secret;

	private SecretKey signingKey;

	@PostConstruct
	public void init() {
		byte[] keyBytes = Base64.getDecoder().decode(secret);
		signingKey = Keys.hmacShaKeyFor(keyBytes);
	}

	// 1.its convert secret key into byte format
	public Claims extractClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody();
	}

	// 2. its check token is valid or not
	public boolean isTokenValid(String token) {
	    try {
	        Claims claims = extractClaims(token);
	        return !claims.getExpiration().before(new Date());
	    } catch (Exception e) {
	        return false;
	    }
	}

	// 3. check if token expired
	public boolean isTokenExpired(String token) {
		return extractClaims(token).getExpiration().before(new Date());
	}

	// 4. fetch user from the token
	public String extractUsername(String token) {
		return extractClaims(token).getSubject();
	}

	// 5. fetch role from the token
	public String extractRole(String token) {
		return extractClaims(token).get("role", String.class);
	}
}
