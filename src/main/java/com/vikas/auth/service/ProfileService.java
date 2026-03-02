package com.vikas.auth.service;

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

	UserProfileResponse getMyProfile(String username);

	UserProfileResponse updateProfile(String username, UpdateProfileRequest request);
}
