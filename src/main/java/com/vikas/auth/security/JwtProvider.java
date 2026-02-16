package com.vikas.auth.security;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vikas.auth.entity.UserEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
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

	private final long JWT_EXPIRATION = 1000 * 60 * 60; // 1 hour
	private SecretKey signingKey;
	

	@PostConstruct
	public void init() {
		byte[] keyBytes = Base64.getDecoder().decode(secret);
		signingKey = Keys.hmacShaKeyFor(keyBytes);
	}


	public String generateToken(String username, String role) {
		return Jwts.builder().setSubject(username).claim("role", role).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
				.signWith(signingKey, SignatureAlgorithm.HS512).compact();
	}

	public String extractUsername(String token) {
		return Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody().getSubject();
	}
	
	

    // 3️⃣ Validate token
    public boolean isTokenValid(String token, UserEntity user) {
        final String username = extractUsername(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }

    // Check expiration
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // Extract claims
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
    
    /*public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
			return true;
		} catch (JwtException ex) {
			return false;
		}
	}*/
}