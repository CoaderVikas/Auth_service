package com.vikas.auth.config;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.Data;

/**
 * Class      : FeignErrorDecoder
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Feb 22, 2026
 * Version    : 1.0
 */

@Component
public class FeignErrorDecoder implements ErrorDecoder {

	private final ObjectMapper mapper = new ObjectMapper();
	
	@Override
    public Exception decode(String methodKey, Response response) {
		return null;

       /* try {
            String body = new String(
                    response.body().asInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            if (body.startsWith("\"{")) {
                body = mapper.readValue(body, String.class);
            }

            DownstreamErrorResponse error =
                    mapper.readValue(body, DownstreamErrorResponse.class);

            return new WebBffException(error);

        } catch (Exception e) {
            DownstreamErrorResponse fallback = new DownstreamErrorResponse();
            fallback.setMessage("Downstream service error");
            fallback.setStatus(response.status());
            fallback.setError("Internal Server Error");
            fallback.setTimestamp(LocalDateTime.now().toString());
            return new WebBffException(fallback);
        }*/
    }
	  @Data
	    public static class ErrorResponse {
	        private String message;
	        private String errorCode;
	        private int status;
	        private String timestamp;
	    }
}