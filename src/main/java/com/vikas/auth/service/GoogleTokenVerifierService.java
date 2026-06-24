package com.vikas.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

/**
 * Class      : GoogleTokenVerifierService
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Jun 23, 2026
 * Version    : 1.0
 */

public interface GoogleTokenVerifierService {

	public GoogleIdToken.Payload verify(String idTokenString) throws Exception;
	
}
