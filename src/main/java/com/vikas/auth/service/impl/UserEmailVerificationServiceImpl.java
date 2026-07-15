package com.vikas.auth.service.impl;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.vikas.auth.dto.MailRequest;
import com.vikas.auth.dto.MailResponse;
import com.vikas.auth.entity.UserEntity;
import com.vikas.auth.exception.AuthServiceException;
import com.vikas.auth.repository.UserRepository;
import com.vikas.auth.service.UserEmailVerificationService;
import com.vikas.feign.MailerFeignClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Class      : UserEmailVerificationServiceImpl
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Jul 15, 2026
 * Version    : 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserEmailVerificationServiceImpl implements UserEmailVerificationService {

	private final RedisTemplate<String, String> redisTemplate;
	private final MailerFeignClient mailerClient;
	private final UserRepository userRepository;
	private static final String OTP_PREFIX = "otp:";
	private static final long OTP_EXPIRY_MINUTES = 5;
	
	@Override
	public void generateAndSendOtp(String email) {
		//1. generate 6 digit opt
		String otp = String.valueOf(new Random().nextInt(900000) + 100000);

		//2. store opt into redis with ttl
		redisTemplate.opsForValue().set(OTP_PREFIX + email, otp, OTP_EXPIRY_MINUTES, TimeUnit.MINUTES);

		log.info("*********** OTP generated for {} ***********", email);
		
		//send otp
		sendOtpEmail(email, otp);
	}

	@Override
	public boolean verifyEmail(String username,String email, String enteredOtp) {

		//1. get the opt from the redis
		String key = OTP_PREFIX + email;
		String storedOtp = redisTemplate.opsForValue().get(key);

		if (storedOtp == null) {
			log.warn("*********** OTP expired or not found for {} ***********", email);
			return false;
		}

		//2. check the otp and validated
		boolean isValid = storedOtp.equals(enteredOtp);

		if (isValid) {
			UserEntity user = userRepository.findByUsername(username)
					.orElseThrow(() -> new AuthServiceException("User not found"));

			userRepository.findByEmail(email).ifPresent(other -> {
				if (!other.getId().equals(user.getId())) {
					throw new AuthServiceException("This Email is already in use");
				}
			});

			user.setEmail(email);
			user.setEmailVerified(true);
			UserEntity save = userRepository.save(user);
			log.info("******** after save ******** "+save);

			log.info("Email verified successfully | username={}", username);
			redisTemplate.delete(key);
			log.info("*********** OTP verified successfully for {} ***********", email);
		} else {
			log.warn("*********** Invalid OTP entered for {} ***********", email);
		}

		return isValid;
	}

	private void sendOtpEmail(String email, String otp) {

		MailRequest request = MailRequest.builder()
				.to(email)
				.subject("Your OTP Code")
				.templateName("otp")
				.otp(otp)
				.build();

		try {
			MailResponse response = mailerClient.sendVerificationMail(request);

			if (!response.isSuccess()) {
				log.error("*********** Mailer service failed: {} ***********", response.getErrorMessage());
				throw new RuntimeException("Failed to send OTP email: " + response.getErrorMessage());
			}

		} catch (Exception e) {
			log.error("*********** Failed to call mailer-service ***********", e);
			throw new RuntimeException("Failed to send OTP email", e);
		}
	}
	

}
