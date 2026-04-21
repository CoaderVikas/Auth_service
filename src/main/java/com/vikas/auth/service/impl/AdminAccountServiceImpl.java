package com.vikas.auth.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.vikas.auth.dto.AdminUserResponse;
import com.vikas.auth.dto.PaginatedUserResponse;
import com.vikas.auth.entity.UserEntity;
import com.vikas.auth.exception.AuthServiceException;
import com.vikas.auth.repository.UserRepository;
import com.vikas.auth.service.AdminAccountService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Class      : AdminAccountServiceImpl
 * Description: Handles admin level user account operations
 * Author     : Vikas Yadav
 * Version    : 1.1
 */

@Service
@RequiredArgsConstructor
@Transactional
public class AdminAccountServiceImpl implements AdminAccountService {

	private final UserRepository userRepository;

	/**
	 * =========================================================
	 * 1️⃣ Fetch All Users (Admin Dashboard) with Pagination
	 * =========================================================
	 */
	@Override
	public PaginatedUserResponse getAllUsers(int page, int size) {

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));

		// 2. Database fetch
		Page<UserEntity> userPage = userRepository.findAll(pageable);

		// 3. Mapping logic 
		List<AdminUserResponse> mappedUsers = userPage.getContent().stream().map(this::convertToAdminResponse)
				.collect(Collectors.toList());

		// 4. Return simplified response
		return PaginatedUserResponse.builder().users(mappedUsers).totalElements(userPage.getTotalElements())
				.totalPages(userPage.getTotalPages()).currentPage(userPage.getNumber()).isLast(userPage.isLast())
				.build();
	}
	/**
	 * =========================================================
	 * 2️⃣ Lock User
	 * =========================================================
	 */
	@Override
	public void lockUser(Long userId) {

		UserEntity user = getUser(userId);

		user.setAccountNonLocked(false);
		user.setAccountLockedAt(LocalDateTime.now());

		userRepository.save(user);
	}

	/**
	 * =========================================================
	 * 3️⃣ Unlock User
	 * =========================================================
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
	 * =========================================================
	 * 4️⃣ Enable / Disable User
	 * =========================================================
	 */
	@Override
	public void updateUserStatus(Long userId, boolean enabled) {

		UserEntity user = getUser(userId);

		user.setEnabled(enabled);

		userRepository.save(user);
	}

	/**
	 * =========================================================
	 * 5️⃣ Update User Role
	 * =========================================================
	 */
	@Override
	public void updateUserRole(Long userId, String role) {

		UserEntity user = getUser(userId);

		// Validate role
		if (!role.equals("ROLE_USER") && !role.equals("ROLE_ADMIN")) {
			throw new AuthServiceException("Invalid role");
		}

		user.setRole(role);

		userRepository.save(user);
	}

	/**
	 * =========================================================
	 * Common method to fetch user safely
	 * =========================================================
	 */
	private UserEntity getUser(Long userId) {

		return userRepository.findById(userId)
				.orElseThrow(() -> new AuthServiceException("User not found"));
	}
	
	private AdminUserResponse convertToAdminResponse(UserEntity user) {
		return AdminUserResponse.builder()
				.id(user.getId()).fullName(user.getFullName()).username(user.getUsername())
				.email(user.getEmail()).role(user.getRole()).enabled(user.getEnabled())
				.accountNonLocked(user.getAccountNonLocked()).failedLoginAttempts(user.getFailedLoginAttempts())
				.accountLockedAt(user.getAccountLockedAt()).createdAt(user.getCreatedDate()).build();
	}
}