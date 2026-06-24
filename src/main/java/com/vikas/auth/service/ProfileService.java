package com.vikas.auth.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.vikas.auth.dto.UpdateProfileRequest;
import com.vikas.auth.dto.UserProfileResponse;

/**
 * Class      : ProfileService
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Mar 1, 2026
 * Version    : 1.0
 */

public interface ProfileService {

	/**
	 * 
	 * @param username
	 * @return
	 */
	UserProfileResponse getMyProfile(String username);

	/**
	 * 
	 * @param username
	 * @param request
	 * @param file
	 * @return
	 */
	UserProfileResponse updateProfile(String username, UpdateProfileRequest request,MultipartFile file);
	
	/**
	 * 
	 * @param customId
	 * @return
	 */
	Resource getUserImageResource(String userId);
}
