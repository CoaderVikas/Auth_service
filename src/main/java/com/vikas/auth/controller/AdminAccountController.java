package com.vikas.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.vikas.auth.dto.PaginatedUserResponse;
import com.vikas.auth.service.AdminAccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Admin account management APIs.
 * Accessible only to users with ADMIN role.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/rent-hub/auth/admin")
@Tag(name = "Admin Account Management", description = "APIs for managing user accounts by ADMIN")
public class AdminAccountController {

	private final AdminAccountService adminAccountService;

	/**
	 * Lock a user account.
	 */
	@Operation(summary = "Lock User Account", description = "Locks a user account and prevents login access")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "User locked successfully"),
			@ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
			@ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed"),
			@ApiResponse(responseCode = "404", description = "User not found"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@PutMapping("/users/{id}/lock")
	public ResponseEntity<String> lockUser(@PathVariable("id") Long id) {

		log.info("Admin locking user with ID: {}", id);
		adminAccountService.lockUser(id);

		return ResponseEntity.ok("User locked successfully");
	}

	/**
	 * Unlock a user account.
	 */
	@Operation(summary = "Unlock User Account", description = "Unlocks a previously locked user account")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "User unlocked successfully"),
			@ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
			@ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed"),
			@ApiResponse(responseCode = "404", description = "User not found"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@PutMapping("/users/{id}/unlock")
	public ResponseEntity<String> unlockUser(@PathVariable("id") Long id) {

		log.info("Admin unlocking user with ID: {}", id);
		adminAccountService.unlockUser(id);

		return ResponseEntity.ok("User unlocked successfully");
	}

	/**
	 * Enable or disable user account.
	 */
	@Operation(summary = "Enable/Disable User", description = "Enables or disables a user account")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "User status updated"),
			@ApiResponse(responseCode = "400", description = "Invalid request parameter"),
			@ApiResponse(responseCode = "401", description = "Unauthorized"),
			@ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed"),
			@ApiResponse(responseCode = "404", description = "User not found"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@PutMapping("/users/{id}/status")
	public ResponseEntity<String> updateUserStatus(@PathVariable("id") Long id,
			@RequestParam("enabled") boolean enabled) {

		log.info("Admin updating status for user ID: {} to enabled={}", id, enabled);
		adminAccountService.updateUserStatus(id, enabled);

		return ResponseEntity.ok("User status updated");
	}

	/**
	 * Update user role (USER / ADMIN).
	 */
	@Operation(summary = "Update User Role", description = "Changes the role of a user (USER or ADMIN)")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "User role updated"),
			@ApiResponse(responseCode = "400", description = "Invalid role supplied"),
			@ApiResponse(responseCode = "401", description = "Unauthorized"),
			@ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed"),
			@ApiResponse(responseCode = "404", description = "User not found"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@PutMapping("/users/{id}/role")
	public ResponseEntity<String> updateUserRole(@PathVariable("id") Long id, @RequestParam("role") String role) {

		log.info("Admin updating role for user ID: {} to role={}", id, role);
		adminAccountService.updateUserRole(id, role);

		return ResponseEntity.ok("Role updated");
	}

	@GetMapping("/users")
	public ResponseEntity<PaginatedUserResponse> getAllUsers(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int size) {
		return ResponseEntity.ok(adminAccountService.getAllUsers(page, size));
	}
}