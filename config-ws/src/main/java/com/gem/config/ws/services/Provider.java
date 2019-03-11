package com.gem.config.ws.services;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.gem.commons.mongo.Collection;
import com.gem.commons.mongo.Mongo;
import com.gem.commons.mongo.MongoDB;
import com.gem.config.ws.entities.App;

@Component
@Configuration
public class Provider {

	@Inject
	private MongoDB db;
	
	@Bean
	@Qualifier("apps")
	public Collection apps() {
		return Mongo.proxyCollection(db, "apps", App.class);
	}

}
