package com.gem.commons.mongo;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gem.commons.Application;
import com.gem.commons.Lazy;

@Configuration
public class MongoBeanProvider {
	
	@Inject
	private MongoService srv;

	@Bean
	@Qualifier("provided")
	public MongoDB getBean() {
		return Lazy.proxy(MongoDB.class, () -> {

			String name = Application.checkAndGet("gem.mongo.database");

			return srv.getDatabase(name);
		});
	}
	
}
