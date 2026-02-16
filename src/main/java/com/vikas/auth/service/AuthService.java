package com.vikas.auth.service;

import com.vikas.auth.dto.LoginRequest;
import com.vikas.auth.dto.LoginResponse;
import com.vikas.auth.dto.RegisterRequest;

/**
 * Class      : AuthService
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 17, 2026
 * Version    : 1.0
 */

public interface AuthService {
	/**
	 * 
	 * @param request
	 * @return
	 */
	public LoginResponse register(RegisterRequest request);
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public LoginResponse login(LoginRequest request);
}
