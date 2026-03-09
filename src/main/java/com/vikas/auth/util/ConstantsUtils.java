package com.vikas.auth.util;

/**
 * Class      : ConstantsUtils
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 22, 2026
 * Version    : 1.0
 */

public interface ConstantsUtils {
	
	static final int OTP_EXPIRY_MINUTES = 5;
	static final int MAX_OTP_ATTEMPTS = 3;
	static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;
	static final int MAX_OTP_PER_10_MINUTES = 5;
	
	 // Redis cache keys
    public static final String USER_PROFILE_CACHE_PREFIX = "userProfile:";

    // Redis cache TTL in minutes
    public static final long USER_PROFILE_CACHE_TTL_MINUTES = 10;

    // Exception messages
    public static final String USER_NOT_FOUND = "User not found with username: %s";
    public static final String EMAIL_ALREADY_IN_USE = "Email already in use";

    // Logging templates
    public static final String LOG_CACHE_HIT = "Cache HIT for user: {}";
    public static final String LOG_CACHE_MISS = "Cache MISS for user: {}, fetching from DB";
    public static final String LOG_PROFILE_UPDATED = "Profile cache updated for user: {}";
    public static final String LOG_UPDATING_PROFILE = "Updating profile for user: {}";
    public static final String LOG_DUPLICATE_EMAIL_WARN = "Attempt to update duplicate email: {}";
    public static final String LOG_WARMUP_START = "Starting user profile cache warmup...";
    public static final String LOG_WARMUP_COMPLETE = "User profile cache warmup completed. Total cached users: {}";
	
	

}
