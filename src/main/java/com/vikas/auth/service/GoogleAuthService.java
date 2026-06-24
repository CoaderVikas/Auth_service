package com.vikas.auth.service;

import com.vikas.auth.dto.GoogleAuthRequest;
import com.vikas.auth.dto.GoogleAuthResponse;

/**
 * Class      : GoogleAuthService
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Jun 23, 2026
 * Version    : 1.0
 */

public interface GoogleAuthService {
	/**
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
    GoogleAuthResponse authenticateWithGoogle(GoogleAuthRequest request) throws Exception;
}
