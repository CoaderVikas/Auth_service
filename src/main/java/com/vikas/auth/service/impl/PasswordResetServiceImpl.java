package com.vikas.auth.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vikas.auth.dto.MailRequest;
import com.vikas.auth.dto.MailResponse;
import com.vikas.auth.dto.PasswordResetRequest;
import com.vikas.auth.dto.PasswordResetResponse;
import com.vikas.auth.entity.UserEntity;
import com.vikas.auth.entity.UserOtpEntity;
import com.vikas.auth.exception.AuthServiceException;
import com.vikas.auth.repository.UserOtpRepository;
import com.vikas.auth.repository.UserRepository;
import com.vikas.auth.service.PasswordResetService;
import com.vikas.auth.util.ConstantsUtils;
import com.vikas.feign.MailerFeignClient;

import lombok.RequiredArgsConstructor;

/**
 * Class      : PasswordResetServiceImpl
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 21, 2026
 * Version    : 1.0
 */

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

	private final UserRepository userRepository;
	private final UserOtpRepository userOtpRepository;
	private final PasswordEncoder passwordEncoder;
	private final MailerFeignClient mailerFeignClient;
	MailRequest mailRequest=null;

	SecureRandom random = new SecureRandom();

	@Override
	@Transactional
	public void generateResetOtp(String username) {
		String newotp = String.format("%06d", random.nextInt(1000000));
		//1. check if user exist
		UserEntity user = userRepository.findByUsername(username)
				.orElseThrow(() -> new AuthServiceException("Incorrect User please enter valid username"));

		//2. check is user account is desable
		if (!user.getEnabled())
			throw new AuthServiceException("User account is disabled");

		if (!user.getAccountNonLocked())
			throw new AuthServiceException("User account is locked due to failed attempts");

		// 3 Rate limit: Max OTP per user in last 10 minutes
		LocalDateTime windowStart = LocalDateTime.now().minusMinutes(10);
		List<UserOtpEntity> recentOtps = userOtpRepository.findByUserAndUsedFalse(user);
		long otpCount = recentOtps.stream().filter(o -> o.getCreatedAt().isAfter(windowStart)).count();

		if (otpCount >= ConstantsUtils.MAX_OTP_PER_10_MINUTES)
			throw new AuthServiceException("Too many OTP requests. Please try later.");

		//4. Invalidate previous unused OTPs
		recentOtps.forEach(otp -> otp.setUsed(true));

		//5. Generate new OTP
		UserOtpEntity otpEntity = UserOtpEntity.builder().user(user).otp(passwordEncoder.encode(newotp)) // hashed
				.purpose("RESET_PASSWORD").createdAt(LocalDateTime.now())
				.expiryTime(LocalDateTime.now().plusMinutes(ConstantsUtils.OTP_EXPIRY_MINUTES)).attemptCount(0).used(false).build();
		userOtpRepository.save(otpEntity);

		//6. TODO: Send OTP via Email/SMS
		// Prepare Mail Request
		
		mailRequest = prepareMail(user,newotp);
		
		System.err.println("OTP for " + username + ": " + newotp);
		
		// Call Mailer Service
		MailResponse response = mailerFeignClient.sendMail(mailRequest);
		
		if (!response.isSuccess()) {
		    throw new AuthServiceException("Failed to send OTP email");
		}
	}

	@Override
	@Transactional
	public PasswordResetResponse resetPassword(PasswordResetRequest request) {
	    // 1️ Fetch user by username
	    UserEntity user = userRepository.findByUsername(request.getUsername())
	            .orElseThrow(() -> new RuntimeException("User not found"));
	    
	    checkAndUnlock(user);

	    // 2️ Check account status
	    if (!user.getEnabled())
	        return PasswordResetResponse.builder().success(false).message("Account disabled").build();
	    if (!user.getAccountNonLocked())
	        return PasswordResetResponse.builder().success(false).message("Account locked").build();

	    // 3️ Fetch latest active OTP for password reset
	    Optional<UserOtpEntity> optionalOtp = userOtpRepository
	            .findTopByUserAndPurposeAndUsedFalseOrderByCreatedAtDesc(user, "RESET_PASSWORD");

	    if (optionalOtp.isEmpty())
	        return PasswordResetResponse.builder().success(false).message("No valid OTP found").build();

	    UserOtpEntity otp = optionalOtp.get();

	    // 4️ Check if OTP is expired
	    if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
	        otp.setUsed(true);                   // mark OTP used
	        userOtpRepository.save(otp);         
	        incrementFailedLogin(user);          // increase failed login attempts
	        return PasswordResetResponse.builder().success(false).message("OTP expired").build();
	    }

	    // 5️ Check max OTP attempts
	    if (otp.getAttemptCount() >= ConstantsUtils.MAX_OTP_ATTEMPTS) {
	        otp.setUsed(true);                   // mark OTP used
	        userOtpRepository.save(otp);
	        incrementFailedLogin(user);          // increment failed login counter
	        return PasswordResetResponse.builder().success(false).message("Maximum OTP attempts exceeded").build();
	    }

	    // 6️⃣ Verify OTP match
	    if (!passwordEncoder.matches(request.getOtp(), otp.getOtp())) {
	        otp.setAttemptCount(otp.getAttemptCount() + 1); // increment OTP attempt
	        userOtpRepository.save(otp);
	        incrementFailedLogin(user);                    // increment failed login counter
	        return PasswordResetResponse.builder().success(false).message("Invalid OTP").build();
	    }

	    // 7️ OTP valid → reset password
	    user.setPassword(passwordEncoder.encode(request.getNewPassword())); // hash and save
	    user.setPasswordVersion(user.getPasswordVersion() + 1);             // increment version
	    user.setPasswordLastUpdatedAt(LocalDateTime.now());                 // update timestamp
	    user.setFailedLoginAttempts(0);                                     // reset failed login attempts
	    userRepository.save(user);

	    // 8️ Mark OTP as used after successful reset
	    otp.setUsed(true);
	    userOtpRepository.save(otp);

	    // 9️ Return success response
	    return PasswordResetResponse.builder().success(true).message("Password reset successfully please login again").build();
	}

	public void incrementFailedLogin(UserEntity user) {
		int attempts = user.getFailedLoginAttempts() + 1;
		user.setFailedLoginAttempts(attempts);

		if (attempts >= ConstantsUtils.MAX_FAILED_LOGIN_ATTEMPTS) {
			user.setAccountNonLocked(false);
			user.setAccountLockedAt(LocalDateTime.now()); // mark lock time
		}

		userRepository.save(user);
	}

	private void checkAndUnlock(UserEntity user) {
		if (!user.getAccountNonLocked() && user.getAccountLockedAt() != null) {
			LocalDateTime unlockTime = user.getAccountLockedAt().plusHours(24);
			if (LocalDateTime.now().isAfter(unlockTime)) {
				user.setAccountNonLocked(true);
				user.setFailedLoginAttempts(0); // reset failed attempts
				user.setAccountLockedAt(null);
				userRepository.save(user);
			}
		}
	}
	
	private MailRequest prepareMail(UserEntity user,String otp) {
		return MailRequest.builder()
				.to(user.getEmail())
				.toName(user.getUsername())
				.subject("Password Reset OTP")
				.templateName("otp")
				.otp(otp)
				.body("Enter OTP to reset your password.")
				.build();
	}

}