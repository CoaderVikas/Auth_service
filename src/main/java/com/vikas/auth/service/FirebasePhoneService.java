package com.vikas.auth.service;

/**
 * Class      : FirebasePhoneService
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Jul 11, 2026
 * Version    : 1.0
 */

public interface FirebasePhoneService {
	 /**
     * Verify the token and return the normalized (10-digit) phone number.
     *
     * @param idToken Firebase ID token (from the frontend)
     * @return 10-digit phone number (country code removed)
     * @throws AuthServiceException if the token is missing, invalid, or
     *                               doesn't contain a phone_number claim
     */
    String verifyAndGetPhone(String idToken);
}
