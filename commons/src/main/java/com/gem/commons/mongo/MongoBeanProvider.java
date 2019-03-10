package com.gem.commons.mongo;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gem.commons.GemConfig;

@Configuration
public class MongoBeanProvider {

	@Inject
	private GemConfig config;
	
	@Bean
	@Qualifier("provided")
	public MongoDB getBean() {
		return new ConfigMongoDBImpl(config);
	}

}
