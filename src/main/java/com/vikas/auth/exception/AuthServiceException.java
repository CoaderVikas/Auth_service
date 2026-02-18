package com.vikas.auth.exception;

/**
 * Class      : AuthServiceException
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 18, 2026
 * Version    : 1.0
 */

public class AuthServiceException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	public AuthServiceException(String msg) {
		super(msg);
	}

}
