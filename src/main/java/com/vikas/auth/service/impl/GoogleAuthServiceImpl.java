package com.vikas.auth.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.vikas.auth.dto.GoogleAuthRequest;
import com.vikas.auth.dto.GoogleAuthResponse;
import com.vikas.auth.entity.UserEntity;
import com.vikas.auth.jwt.JwtService;
import com.vikas.auth.repository.UserRepository;
import com.vikas.auth.service.GoogleAuthService;
import com.vikas.auth.service.GoogleTokenVerifierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
/**
 * Class      : GoogleAuthServiceImpl
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Jun 23, 2026
 * Version    : 1.0
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleAuthServiceImpl implements GoogleAuthService {

	private final GoogleTokenVerifierService googleTokenVerifierService;
	private final UserRepository userRepository;
	private final JwtService jwtService;

	@Override
	public GoogleAuthResponse authenticateWithGoogle(GoogleAuthRequest request) throws Exception {

		// 1. Google token verify karo
		GoogleIdToken.Payload payload = googleTokenVerifierService.verify(request.getIdToken());

		String email = payload.getEmail();
		String name = (String) payload.get("name");
		String googleId = payload.getSubject();
		String username = email.split("@")[0];
		String photoUrl = (String) payload.get("picture");

		log.info("Google auth attempt for email: {}", email);

		// 2. User already exist karta hai check karo
		UserEntity user = userRepository.findByEmail(email).orElse(null);

		if (user == null) {
			// 3. Naya user banao
			user = UserEntity.builder()
					.fullName(name)
					.username(generateUniqueUsername(username))
					.email(email)
					.password("")
					.role(request.getRole())
					.photoUrl(photoUrl)
					.passwordVersion(1)
					.passwordLastUpdatedAt(LocalDateTime.now())
					.accountNonLocked(true)
					.enabled(true)
					.failedLoginAttempts(0)
					.createdAt(LocalDateTime.now()).build();

			userRepository.save(user);
			log.info("New Google user created: {}", email);
		} else {
			log.info("Existing Google user logged in: {}", email);
		}

		String token = jwtService.generateToken(user.getUsername(),user.getOwnerVerificationStatus().name(), user.getRole(), user.getPasswordVersion(),
				user.getFullName());

		return new GoogleAuthResponse(token, user.getRole(), user.getUsername(), user.getFullName());
	}

	// Username already exist karta hai to number append karo
	private String generateUniqueUsername(String base) {
		String username = base;
		int counter = 1;
		while (userRepository.existsByUsername(username)) {
			username = base + counter++;
		}
		return username;
	}
}