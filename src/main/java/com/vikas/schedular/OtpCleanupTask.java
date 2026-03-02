package com.vikas.schedular;

/**
 * Class      : OtpCleanupTask
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 22, 2026
 * Version    : 1.0
 */

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vikas.auth.repository.UserOtpRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OtpCleanupTask {

    private final UserOtpRepository userOtpRepository;

    // Run every day at 2 AM
    @Scheduled(cron = "0 38 12 * * ?")
    public void cleanupOldOtps() {
    	LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(1);
        userOtpRepository.deleteOldOtps(expiryTime);
        System.out.println("Old/used OTPs cleanup executed at " + LocalDateTime.now());
    }
}