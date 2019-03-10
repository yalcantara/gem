package com.gem.commons;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Configuration
public class CommonsProvider {

	public CommonsProvider() {
	}

	@Bean
	public ObjectMapper jsonMapper() {
		return Json.mapper();
	}

}
