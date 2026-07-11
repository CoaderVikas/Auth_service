package com.vikas.auth.service;

import com.vikas.auth.dto.LoginResponse;
import com.vikas.auth.dto.PasswordResetResponse;

/**
 * Class : PhoneAuthService
 * Description: Firebase phone-OTP based flows (existing email-OTP se alag):
 *              1. Passwordless login (verified phone -> JWT)
 *              2. Password reset via verified phone (email-OTP bypass)
 *
 * Dono flows me frontend ek Firebase ID token bhejta hai jo backend
 * FirebasePhoneService se verify hota hai.
 *
 * Author : Vikas Yadav
 */
public interface PhoneAuthService {

	/**
	 * Verified phone se passwordless login. Token verify -> phone nikaalo -> us
	 * phone wala user dhoondo -> access + refresh token do.
	 *
	 * @param firebaseIdToken frontend Firebase ID token
	 * @return LoginResponse (JWT + refresh)
	 */
	LoginResponse loginWithPhone(String firebaseIdToken);

	/**
	 * Verified phone se password reset. Token verify -> phone nikaalo -> us phone
	 * wale user ka password set karo (version++ etc).
	 *
	 * @param firebaseIdToken frontend Firebase ID token
	 * @param newPassword     naya password (plain, yahan hash hoga)
	 * @return PasswordResetResponse
	 */
	PasswordResetResponse resetPasswordWithPhone(String firebaseIdToken, String newPassword);
}