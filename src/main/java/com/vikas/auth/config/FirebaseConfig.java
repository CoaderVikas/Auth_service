package com.vikas.auth.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * Class : FirebaseConfig
 * Description: Firebase Admin SDK init from ENV VARS (no JSON file).
 *              application.yml me:
 *                firebase:
 *                  project-id:   ${FB_PROJECT_ID}
 *                  client-email: ${FB_CLIENT_EMAIL}
 *                  private-key:  ${FB_PRIVATE_KEY}
 *
 * FB_PRIVATE_KEY me service-account JSON ki "private_key" value poori daalo
 * (BEGIN/END lines samet). Env me newlines aksar "\n" (literal) ban jaate hain,
 * isliye code me unko real newline me replace karte hain.
 *
 * Ye 3 values service-account JSON se aati hain:
 *   project_id     -> FB_PROJECT_ID
 *   client_email   -> FB_CLIENT_EMAIL
 *   private_key    -> FB_PRIVATE_KEY
 *
 * Author : Vikas Yadav
 */
@Configuration
@Slf4j
public class FirebaseConfig {

	@Value("${firebase.project-id}")
	private String projectId;

	@Value("${firebase.client-email}")
	private String clientEmail;

	@Value("${firebase.private-key}")
	private String privateKey;

	@PostConstruct
	public void init() {
		try {
			if (!FirebaseApp.getApps().isEmpty()) {
				log.info("FirebaseApp already initialized, skipping");
				return;
			}

			// env/yml me "\n" literal aa jaata hai -> real newline banao
			String pk = privateKey.replace("\\n", "\n");

			GoogleCredentials credentials = ServiceAccountCredentials.newBuilder().setProjectId(projectId)
					.setClientEmail(clientEmail).setPrivateKey(parsePrivateKey(pk)).build();

			FirebaseOptions options = FirebaseOptions.builder().setCredentials(credentials).setProjectId(projectId)
					.build();

			FirebaseApp.initializeApp(options);
			log.info("FirebaseApp initialized successfully | projectId={}", projectId);
		} catch (Exception e) {
			log.error("Failed to initialize FirebaseApp", e);
			throw new IllegalStateException("Firebase init failed", e);
		}
	}

	/**
	 * PEM string -> PrivateKey object.
	 */
	private java.security.PrivateKey parsePrivateKey(String pem) throws IOException {
		String clean = pem.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "")
				.replaceAll("\\s", "");
		byte[] der = java.util.Base64.getDecoder().decode(clean);
		try {
			java.security.spec.PKCS8EncodedKeySpec spec = new java.security.spec.PKCS8EncodedKeySpec(der);
			return java.security.KeyFactory.getInstance("RSA").generatePrivate(spec);
		} catch (java.security.NoSuchAlgorithmException | java.security.spec.InvalidKeySpecException e) {
			throw new IOException("Invalid Firebase private key", e);
		}
	}

	@Bean
	public FirebaseAuth firebaseAuth() {
		return FirebaseAuth.getInstance();
	}
}