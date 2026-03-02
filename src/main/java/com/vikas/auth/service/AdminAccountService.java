package com.vikas.auth.service;

/**
 * Class      : AdminAccountService
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Mar 1, 2026
 * Version    : 1.0
 */

public interface AdminAccountService {

	void lockUser(Long userId);

	void unlockUser(Long userId);

	void updateUserStatus(Long userId, boolean enabled);

	void updateUserRole(Long userId, String role);
}
