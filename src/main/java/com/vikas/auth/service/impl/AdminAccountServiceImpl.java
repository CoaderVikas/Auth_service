package com.vikas.auth.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.vikas.auth.entity.UserEntity;
import com.vikas.auth.exception.AuthServiceException;
import com.vikas.auth.repository.UserRepository;
import com.vikas.auth.service.AdminAccountService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Class      : AdminAccountServiceImpl
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Mar 1, 2026
 * Version    : 1.0
 */

@Service
@RequiredArgsConstructor
@Transactional
public class AdminAccountServiceImpl implements AdminAccountService {

	private final UserRepository userRepository;

	/**
	 * ========================================================= 1️⃣ Lock User
	 * =========================================================
	 */
	@Override
	public void lockUser(Long userId) {

		// Step 1: Fetch user
		UserEntity user = getUser(userId);

		// Step 2: Update lock fields
		user.setAccountNonLocked(false);
		user.setAccountLockedAt(LocalDateTime.now());

		// Step 3: Save changes
		userRepository.save(user);

		// Step 4 (Recommended Future): Write audit log
	}

	/**
	 *  2️⃣ Unlock User
	 */
	@Override
	public void unlockUser(Long userId) {

		UserEntity user = getUser(userId);

		user.setAccountNonLocked(true);
		user.setFailedLoginAttempts(0);
		user.setAccountLockedAt(null);

		userRepository.save(user);
	}

	/**
	 *3️⃣ Enable /Disable User 
	 */
	@Override
	public void updateUserStatus(Long userId, boolean enabled) {

		UserEntity user = getUser(userId);

		user.setEnabled(enabled);

		userRepository.save(user);
	}

	/**
	 *  4️⃣ Update User Role 
	 */
	@Override
	public void updateUserRole(Long userId, String role) {

		UserEntity user = getUser(userId);

		// Step 1: Validate role
		if (!role.equals("USER") && !role.equals("ADMIN")) {
			throw new AuthServiceException("Invalid role");
		}

		// Step 2: Update role
		user.setRole(role);

		// Step 3: Save
		userRepository.save(user);
	}

	/**
	 * Common method to fetch user safely
	 */
	private UserEntity getUser(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new AuthServiceException("User not found"));
	}
}