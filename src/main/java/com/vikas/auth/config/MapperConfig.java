package com.vikas.auth.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
* Class      : UserEntity
* Description: [Add brief description here]
* Author     : Vikas Yadav
* Created On : Feb 17, 2026
* Version    : 1.0
*/

@Configuration
public class MapperConfig {

	@Bean
	ModelMapper modelMapper() {
		return new ModelMapper();
	}
}