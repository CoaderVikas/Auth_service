package com.vikas.auth.service.impl;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.vikas.auth.service.GoogleTokenVerifierService;

import jakarta.annotation.PostConstruct;

/**
 * Class      : GoogleTokenVerifierServiceImpl
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Jun 23, 2026
 * Version    : 1.0
 */
@Service
public class GoogleTokenVerifierServiceImpl implements GoogleTokenVerifierService {

	@Value("${google.client.id}")
	private String clientId;
	
	private GoogleIdTokenVerifier verifier;

	@PostConstruct
	public void init() {
		verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
				.setAudience(Collections.singletonList(clientId)).build();
	}

	public GoogleIdToken.Payload verify(String idTokenString) throws Exception {
		GoogleIdToken idToken = verifier.verify(idTokenString);
		if (idToken == null) {
			throw new RuntimeException("Invalid or expired Google token");
		}
		return idToken.getPayload();
	}

}
