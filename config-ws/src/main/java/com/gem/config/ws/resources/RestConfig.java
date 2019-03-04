package com.gem.config.ws.resources;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
@ApplicationPath("/rest")
public class RestConfig extends ResourceConfig {

	public RestConfig() {
		packages(RestConfig.class.getPackageName());
	}
}
