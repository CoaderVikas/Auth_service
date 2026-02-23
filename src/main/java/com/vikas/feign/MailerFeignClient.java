package com.vikas.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.vikas.auth.dto.MailRequest;
import com.vikas.auth.dto.MailResponse;
import com.vikas.feign.fallback.MailerFeignFallback;

/**
 * Class      : MailerFeignClient
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 22, 2026
 * Version    : 1.0
 */
@FeignClient(name = "MAILER-SERVICE",path = "/api/mail",fallback = MailerFeignFallback.class)
public interface MailerFeignClient {
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/send")
	public MailResponse sendMail(@RequestBody MailRequest request);

}
