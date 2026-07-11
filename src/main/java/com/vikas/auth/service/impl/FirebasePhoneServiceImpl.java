package com.vikas.auth.service.impl;

import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.vikas.auth.exception.AuthServiceException;
import com.vikas.auth.service.FirebasePhoneService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class FirebasePhoneServiceImpl implements FirebasePhoneService{

	private final FirebaseAuth firebaseAuth;

	public String verifyAndGetPhone(String idToken) {
		if (idToken == null || idToken.isBlank()) {
			throw new AuthServiceException("Missing Firebase token");
		}

		FirebaseToken decoded;
		try {
			decoded = firebaseAuth.verifyIdToken(idToken);
		} catch (FirebaseAuthException e) {
			log.warn("Firebase token verification failed | {}", e.getMessage());
			throw new AuthServiceException("Phone verification failed. Please try again.");
		}

		Object phoneClaim = decoded.getClaims().get("phone_number");
		if (phoneClaim == null) {
			log.warn("Firebase token has no phone_number claim | uid={}", decoded.getUid());
			throw new AuthServiceException("Token does not contain a verified phone number");
		}

		return normalize(phoneClaim.toString());
	}

	/**
	 * "+919876543210" -> "9876543210". India ke liye +91 / 91 / leading 0 strip.
	 */
	private String normalize(String raw) {
		String digits = raw.replaceAll("[^0-9]", "");
		if (digits.length() > 10) {
			digits = digits.substring(digits.length() - 10); // last 10 digits
		}
		return digits;
	}
}