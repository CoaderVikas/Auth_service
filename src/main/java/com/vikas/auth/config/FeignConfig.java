package com.vikas.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.codec.ErrorDecoder;

/**
 * Class      : FeignConfig
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 22, 2026
 * Version    : 1.0
 */

@Configuration
public class FeignConfig {

    
    @Bean
    ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
}