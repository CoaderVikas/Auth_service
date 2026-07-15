package com.vikas.auth.service;

/**
 * Class      : UserEmailVerificationService
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Jul 15, 2026
 * Version    : 1.0
 */

public interface UserEmailVerificationService {
	/**
	 * Generates a 6-digit OTP, stores it (with expiry), and sends it
	 * to the given email address via mailer-service.
	 *
	 * @param email recipient email address
	 */
	void generateAndSendOtp(String email);

	/**
	 * Verifies the OTP entered by the user against the stored OTP.
	 * On successful match, the OTP is invalidated (single-use).
	 *
	 * @param email       the email address being verified
	 * @param enteredOtp  the OTP entered by the user
	 * @return true if OTP is valid and matches, false otherwise
	 */
	boolean verifyEmail(String username,String email, String enteredOtp);
}
