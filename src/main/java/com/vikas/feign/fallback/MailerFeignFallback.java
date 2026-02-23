package com.vikas.feign.fallback;

import org.springframework.stereotype.Component;

import com.vikas.auth.dto.MailRequest;
import com.vikas.auth.dto.MailResponse;
import com.vikas.feign.MailerFeignClient;

/**
 * Class      : MailerFeignFallback
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 23, 2026
 * Version    : 1.0
 */

@Component
public class MailerFeignFallback implements MailerFeignClient {

    @Override
    public MailResponse sendMail(MailRequest request) {
        return MailResponse.builder()
                .success(false)
                .message("Mailer service is down")
                .build();
    }
}