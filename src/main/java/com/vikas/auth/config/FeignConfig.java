package com.vikas.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;

/**
 * Class      : FeignConfig
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Jun 16, 2026
 * Version    : 1.0
 */

@Configuration
@Slf4j
public class FeignConfig {
	
	@Bean
	public RequestInterceptor requestInterceptor() {

		return requestTemplate -> {

			ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

			if (attrs != null) {

				String token = attrs.getRequest().getHeader("Authorization");
				log.info("*********** Passing token via feign {}",token);

				if (token != null && !token.isEmpty()) {
					requestTemplate.header("Authorization", token);
				}
			}
		};
	}
}