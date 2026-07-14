package com.vikas.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.vikas.auth.config.FeignConfig;
import com.vikas.auth.feign.dto.VerificationNotificationRequest;

/**
 * Class      : NotificationServiceFeignClient
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Jul 14, 2026
 * Version    : 1.0
 */

@FeignClient(name = "NOTIFICATION-SERVICE", path = "/notifications", configuration = FeignConfig.class)
public interface NotificationServiceFeignClient {

	@PostMapping("/verification")
	ResponseEntity<Void> createVerificationNotification(@RequestBody VerificationNotificationRequest request);
}