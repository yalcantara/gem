package com.gem.config.ws.controllers;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import com.gem.commons.Json;

@Component
@ApplicationPath("/rest")
public class RestConfig extends ResourceConfig {
	
	public RestConfig() {
		packages(RestConfig.class.getPackageName());

		JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
		provider.setMapper(Json.mapper());
		register(provider);
	}
}
